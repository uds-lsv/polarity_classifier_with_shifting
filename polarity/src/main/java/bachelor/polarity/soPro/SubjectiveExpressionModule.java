package bachelor.polarity.soPro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import bachelor.polarity.salsa.corpora.elements.Fenode;
import bachelor.polarity.salsa.corpora.elements.Flag;
import bachelor.polarity.salsa.corpora.elements.Frame;
import bachelor.polarity.salsa.corpora.elements.FrameElement;
import bachelor.polarity.salsa.corpora.elements.Nonterminal;
import bachelor.polarity.salsa.corpora.elements.Target;
import bachelor.polarity.salsa.corpora.elements.Terminal;

public class SubjectiveExpressionModule implements Module {

	private SentimentLex sentimentLex;
	private ShifterLex shifterLex;

	/**
	 * Constructs a new SubjectiveExpressionModule.
	 * 
	 * @param sentimentLex
	 *          A SentimentLex object in which the rules for finding sentiment
	 *          sources and targets are saved as SentimentUnits.
	 * @param shifterLex
	 *          A ShifterLex object in which the rules for finding shifter targets
	 *          are saved as ShifterUnits.
	 */
	public SubjectiveExpressionModule(SentimentLex sentimentLex, ShifterLex shifterLex) {
		this.sentimentLex = sentimentLex;
		this.shifterLex = shifterLex;
	}

	/**
	 * Looks at a single SentenceObj to find any subjective expressions and adds
	 * the information to the Salsa XML structure. First shifters and their
	 * targets are located, afterwards subjective expressions outside the scope of
	 * a shifter are considered.
	 *
	 * @param sentence
	 *          The SentenceObj which is to be checked for subjective expressions.
	 */
	public Collection<Frame> findFrames(SentenceObj sentence) {
		/**
		 * Collects the found frames
		 */
		final Collection<Frame> frames = new ArrayList<Frame>();
		final FrameIds frameIds = new FrameIds(sentence, "se");

		ArrayList<WordObj> sentimentList = new ArrayList<WordObj>();
		ArrayList<WordObj> shifterList = new ArrayList<WordObj>();

		// Look up every word in the shifterLex and sentimentLex lexicons and add
		// them to the shifterList or sentimentList.
		for (WordObj word : sentence.getWordList()) {
			if (sentimentLex.getSentiment(word.getLemma()) != null) {
				sentimentList.add(word);
			}
			if (shifterLex.getShifter(word.getLemma()) != null) {
				shifterList.add(word);
			}
		}

		// Iterate over every found shifter and search for targets in their scope.
		// Also set frames.
		for (WordObj shifter : shifterList) {
			// Look for the shifterTarget
			WordObj shifterTarget = findShifterTarget(shifter, sentimentList, sentence);
			if (shifterTarget != null) {

				// Create Frame object
				final Frame frame = new Frame("SubjectiveExpression", frameIds.next());
				final FrameElementIds feIds = new FrameElementIds(frame);

				// Set Frames for the sentiment word
				final Target target = new Target();
				System.out.println("shifterTarget: " + shifterTarget);
				setFrames(sentence, frames, shifterTarget, frame, target);

				// Set FrameElement for the shifter
				final FrameElement shifterElement = new FrameElement(feIds.next(), "Shifter");
				shifterElement.addFenode(new Fenode(sentence.getTree().getTerminal(shifter).getId()));

				// Set Frame element flag for the shifter
				String shifterType = shifterLex.getShifter(shifter.getLemma()).shifter_type;
				final Flag shifterFlag = new Flag(shifterType, "shifter");
				shifterElement.addFlag(shifterFlag);

				// Set Flag for the Frame stating the starting polarity value
				String polarityValue = sentimentLex.getSentiment(shifterTarget.getLemma()).value;
				String polarityCategory = sentimentLex.getSentiment(shifterTarget.getLemma()).category;
				String valueAndCat = polarityCategory + " " + polarityValue;
				final Flag polarityWithoutShift = new Flag("polarity without shift: " + valueAndCat, "subjExpr");
				frame.addFlag(polarityWithoutShift);
				// Compute the polarity value after the shift
				Double polarityValueD = Double.valueOf(polarityValue);
				switch (shifterType) {
				case "on negative":
					polarityCategory = "POS";
					break;
				case "on positive":
					polarityCategory = "NEG";
					break;
				// Default case = general
				// Invert category
				default:
					switch (polarityCategory) {
					case "POS":
						polarityCategory = "NEG";
						break;
					case "NEG":
						polarityCategory = "POS";
					}
				}
				valueAndCat = polarityCategory + " " + polarityValue;
				final Flag polarityAfterShift = new Flag("polarity after shift: " + valueAndCat, "subjExpr");
				frame.addFlag(polarityAfterShift);

				frame.addFe(shifterElement);

				// Remove the shifterTarget from the sentiment list so it doesn't get
				// another frame when iterating over the remaining sentiments.
				sentimentList.remove(shifterTarget);
			} else {
				// System.out.println("No shifterTarget found for " +
				// shifter.getName());
			}
		}

		// *********************sentiments without shifter*************************
		// Iterate over every found sentiment and set frames.
		for (WordObj sentiment : sentimentList) {
			// Create Frame object
			final Frame frame = new Frame("SubjectiveExpression", frameIds.next());

			// Set Target for the sentiment word
			final Target target = new Target();
			setFrames(sentence, frames, sentiment, frame, target);

			// Set Flag for the Frame stating the starting polarity value
			String polarityValue = sentimentLex.getSentiment(sentiment.getLemma()).value;
			String polarityCategory = sentimentLex.getSentiment(sentiment.getLemma()).category;
			String valueAndCat = polarityCategory + " " + polarityValue;
			final Flag polarityWithoutShift = new Flag("polarity without shift: " + valueAndCat, "subjExpr");
			frame.addFlag(polarityWithoutShift);
		}
		return frames;
	}

	/**
	 * This method is used to look for the target of a shifter using dependency
	 * relations. The found shifter target must be contained in the sentimentList
	 * in order to be returned by this method.
	 * 
	 * @param shifter
	 *          The shifter for which a target is searched for.
	 * @param sentimentList
	 *          A list of found sentiments. These are the potential shifter target
	 *          candidates.
	 * @param sentence
	 *          The sentence the shifter is in.
	 * @return The WordObj corresponding to the found shifter target, or null.
	 */
	private WordObj findShifterTarget(WordObj shifter, ArrayList<WordObj> sentimentList, SentenceObj sentence) {
		System.out.println("Shifter: " + shifter.getLemma());
		WordObj shifterTarget = null;
		ShifterUnit shifterUnit = shifterLex.getShifter(shifter.getLemma());
		System.out.println("Scope: " + Arrays.toString(shifterUnit.shifter_scope));

		// TODO deletedNodes?
		WordObj containsDeleted = shifter.getDeleted().peekFirst();
		if (containsDeleted != null) {
			System.out.println("DeletedNodes != null: " + shifter.getDeleted().toString());
			System.out.println("containsDeleted: " + containsDeleted.getLemma());
			if (!(containsDeleted.getLemma().equals(""))) {
				// shifter = containsDeleted;
			}
		}

		HashSet<Edge> edges = sentence.getGraph().getEdges();

		for (String scopeEntry : shifterUnit.shifter_scope) {
			for (Edge edge : edges) {
				// Special case for "nicht"
				if (shifter.getLemma().equals("nicht")) {
					if (edge.toString().contains("nicht")) {
						System.out.println("edge: " + edge);
						shifterTarget = edge.source;
						if (sentimentList.contains(shifterTarget)) {
							return shifterTarget;
						}
					}
				}
				switch (scopeEntry) {
				case "objp-*":
					if (edge.depRel.contains("objp") && edge.source.equals(shifter)) {
						System.out.println("edge: " + edge);
						shifterTarget = edge.target;
						if (sentimentList.contains(shifterTarget)) {
							return shifterTarget;
						}
					}
				case "attr-rev":
					if (edge.depRel.equals("attr") && edge.target.equals(shifter)) {
						System.out.println("edge: " + edge);
						shifterTarget = edge.source;
						if (sentimentList.contains(shifterTarget)) {
							return shifterTarget;
						}
					}
				case "det":
					if (edge.depRel.equals(scopeEntry) && edge.source.equals(shifter)) {
						if (edge.target.getPos().equals("PPOSAT")) {
							System.out.println("edge: " + edge);
							shifterTarget = edge.target;
							if (sentimentList.contains(shifterTarget)) {
								return shifterTarget;
							}
						}
					}
					// TODO
				case "clause":
					final ConstituencyTree tree = sentence.getTree();
					final Terminal wordNode = tree.getTerminal(shifter);
					final Nonterminal containingClause;

					if (tree.hasDominatingNode(wordNode, "S")) {
						containingClause = tree.getLowestDominatingNode(wordNode, "S");
					} else {
						// This isn't supposed to happen except in case of parsing errors
						containingClause = tree.getTrueRoot();
					}
					tree.getMainClause(containingClause);
					shifterTarget = edge.target;
					if (sentimentList.contains(shifterTarget)) {
						return shifterTarget;
					}
				default:
					if (edge.depRel.equals(scopeEntry) && edge.source.equals(shifter)) {
						shifterTarget = edge.target;
						System.out.println("edge: " + edge);
						if (sentimentList.contains(shifterTarget)) {
							return shifterTarget;
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Helper method used to set Salsa Frames for subjective expressions.
	 * 
	 * @param sentence
	 *          The relevant sentence.
	 * @param frames
	 *          The Frame collection to be modified.
	 * @param sentiment
	 *          The relevant sentiment.
	 * @param frame
	 *          The Frame to be set.
	 * @param target
	 *          The target to set the Frame to.
	 */
	private void setFrames(SentenceObj sentence, final Collection<Frame> frames, WordObj sentiment, final Frame frame,
			final Target target) {
		target.addFenode(new Fenode(sentence.getTree().getTerminal(sentiment).getId()));

		// In case of mwe: add all collocations to the subjective expression
		// xml/frame
		SentimentUnit unit = sentimentLex.getSentiment(sentiment.getLemma());
		if (unit.mwe) {
			ArrayList<WordObj> matches = sentence.getGraph().getMweMatches(sentiment,
					new ArrayList<String>(Arrays.asList(unit.collocations)), true);
			for (WordObj match : matches) {
				target.addFenode(new Fenode(sentence.getTree().getTerminal(match).getId()));
			}
		}
		// add extra Fenode for particle of a verb if existent
		if (sentiment.getIsParticleVerb()) {
			WordObj particle = sentiment.getParticle();
			target.addFenode(new Fenode(sentence.getTree().getTerminal(particle).getId()));
		}
		frame.setTarget(target);
		frames.add(frame);
	}
}
