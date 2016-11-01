package bachelor.polarity.soPro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import bachelor.polarity.salsa.corpora.elements.Fenode;
import bachelor.polarity.salsa.corpora.elements.Flag;
import bachelor.polarity.salsa.corpora.elements.Frame;
import bachelor.polarity.salsa.corpora.elements.FrameElement;
import bachelor.polarity.salsa.corpora.elements.Global;
import bachelor.polarity.salsa.corpora.elements.Globals;
import bachelor.polarity.salsa.corpora.elements.Nonterminal;
import bachelor.polarity.salsa.corpora.elements.Sentence;
import bachelor.polarity.salsa.corpora.elements.Target;
import bachelor.polarity.salsa.corpora.elements.Terminal;

public class SubjectiveExpressionModule implements Module {

	private SentimentLex sentimentLex;
	private ShifterLex shifterLex;
	/**
	 * Stores each sentence's polarity
	 */
	private Collection<Global> globalsSentencePolarities = new ArrayList<Global>();

	/**
	 * Globals are sentence flags. Stores each sentence's polarity.
	 * 
	 * @return ArrayList<Global> globalsSentencePolarities
	 */
	public Collection<Global> getGlobalsSentencePolarities() {
		return globalsSentencePolarities;
	}

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
		globalsSentencePolarities.removeAll(globalsSentencePolarities);

		ArrayList<WordObj> sentimentList = new ArrayList<WordObj>();
		ArrayList<WordObj> shifterList = new ArrayList<WordObj>();

		Double polaritySum = 0.0;
		Double polarityOfWord = 0.0;

		// Look up every word in the shifterLex and sentimentLex lexicons and add
		// them to the shifterList or sentimentList.
		for (WordObj word : sentence.getWordList()) {
			SentimentUnit sentLexEntry = sentimentLex.getSentiment(word.getLemma());
			if (sentLexEntry != null) {
				if (word.getPos().equals(sentLexEntry.pos)) {
					sentimentList.add(word);
				} else if (word.getPos().startsWith("V") && sentLexEntry.pos.startsWith("V")) {
					sentimentList.add(word);
				} else if (word.getPos().startsWith("A") && sentLexEntry.pos.startsWith("A")) {
					sentimentList.add(word);
				} else {
					System.out.println("MISMATCH!!");
					System.out.println("word found in sentimentlex: " + word.getLemma());
					System.out.println("word Pos found in text: " + word.getPos());
					System.out.println("sentimentLex entry pos: " + sentLexEntry.pos);
				}

			}
			ShifterUnit shifterLexEntry = shifterLex.getShifter(word.getLemma());
			if (shifterLexEntry != null) {
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
				// TODO call it shifter after evaluation tool is not needed anymore!
				final FrameElement shifterElement = new FrameElement(feIds.next(), "Target");
				shifterElement.addFenode(new Fenode(sentence.getTree().getTerminal(shifter).getId()));

				// Set Frame element flag for the shifter
				String shifterType = shifterLex.getShifter(shifter.getLemma()).shifter_type;
				final Flag shifterFlag = new Flag(shifterType, "shifter");
				shifterElement.addFlag(shifterFlag);

				// Set Flag for the Frame stating the starting polarity value
				String polarityValueStr = sentimentLex.getSentiment(shifterTarget.getLemma()).value;
				String polarityCategory = sentimentLex.getSentiment(shifterTarget.getLemma()).category;
				String valueAndCat = polarityCategory + " " + polarityValueStr;
				final Flag polarityWithoutShift = new Flag("polarity without shift: " + valueAndCat, "subjExpr");
				frame.addFlag(polarityWithoutShift);
				// Compute the polarity value after the shift
				polarityOfWord = Double.valueOf(polarityValueStr);
				switch (shifterType) {
				case "on negative":
					polarityCategory = "POS";
					break;
				case "on positive":
					polarityCategory = "NEG";
					polarityOfWord = polarityOfWord * -1.0;
					break;
				// Default case = general
				// Invert category
				default:
					switch (polarityCategory) {
					case "POS":
						polarityCategory = "NEG";
						polarityOfWord = polarityOfWord * -1.0;
						break;
					case "NEG":
						polarityCategory = "POS";
					}
				}
				polaritySum += polarityOfWord;

				valueAndCat = polarityCategory + " " + polarityValueStr;
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
			String polarityValueStr = sentimentLex.getSentiment(sentiment.getLemma()).value;
			String polarityCategory = sentimentLex.getSentiment(sentiment.getLemma()).category;
			String valueAndCat = polarityCategory + " " + polarityValueStr;
			polarityOfWord = Double.valueOf(polarityValueStr);
			if (polarityCategory.equals("NEG")) {
				polarityOfWord = polarityOfWord * -1.0;
			}
			polaritySum += polarityOfWord;
			final Flag polarityWithoutShift = new Flag("polarity without shift: " + valueAndCat, "subjExpr");
			frame.addFlag(polarityWithoutShift);
		}
		// final Frame sentenceFrame = new Frame("Sentence");
		// final Flag polaritySumFlag = new Flag("Sentence polarity: " +
		// String.format("%.2f", polaritySum), "sentence");
		// final Target sentenceTarget = new Target();
		// sentenceTarget.addFenode(new
		// Fenode(sentence.getTree().getTrueRoot().getId()));
		// sentenceFrame.addFlag(polaritySumFlag);
		// sentenceFrame.setTarget(sentenceTarget);
		// frames.add(sentenceFrame);

		final Global sentencePolarity = new Global("INTERESTING");
		sentencePolarity.setParam(String.format("%.2f", polaritySum));
		sentencePolarity.setText("The sentence polarity.");
		globalsSentencePolarities.add(sentencePolarity);

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
	 *          A list of found sentiments in the current sentence. These are the
	 *          potential shifter target candidates.
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
			if (!(containsDeleted.getLemma().equals(""))) {
				// shifter = containsDeleted;
			}
		}

		HashSet<Edge> edges = sentence.getGraph().getEdges();

		for (String scopeEntry : shifterUnit.shifter_scope) {
			System.out.println("scopeEntry: " + scopeEntry);
			for (Edge edge : edges) {
				// Special case for "nicht"
				if (shifter.getLemma().equals("nicht")) {
					if (edge.toString().contains("nicht")) {
						System.out.println("edge: " + edge);
						shifterTarget = edge.source;
						if (sentimentList.contains(shifterTarget) && !shifterTarget.equals(shifter)) {
							return shifterTarget;
						}
					}
				}
				switch (scopeEntry) {
				case "objp-*":
					if (edge.depRel.contains("objp") && edge.source.equals(shifter)) {
						System.out.println("edge: " + edge);
						shifterTarget = edge.target;
						if (sentimentList.contains(shifterTarget) && !shifterTarget.equals(shifter)) {
							return shifterTarget;
						}
					}
					break;
				case "attr-rev":
					if (edge.depRel.equals("attr") && edge.target.equals(shifter)) {
						System.out.println("edge: " + edge);
						shifterTarget = edge.source;
						if (sentimentList.contains(shifterTarget) && !shifterTarget.equals(shifter)) {
							return shifterTarget;
						}
					}
					break;
				case "det":
					if (edge.depRel.equals(scopeEntry) && edge.source.equals(shifter)) {
						if (edge.target.getPos().equals("PPOSAT")) {
							System.out.println("edge: " + edge);
							shifterTarget = edge.target;
							if (sentimentList.contains(shifterTarget) && !shifterTarget.equals(shifter)) {
								return shifterTarget;
							}
						}
					}
					break;
				// TODO ?
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
					// List<Object> terminalNonterminalList =
					// tree.getMainClause(containingClause);
					shifterTarget = edge.target;
					if (!tree.getChildren(containingClause).contains(tree.getTerminal(shifterTarget))) {
						continue;
					}
					if (sentimentList.contains(shifterTarget) && !shifterTarget.equals(shifter)) {
						System.out.println("Found shifterTarget for Clause!: " + shifterTarget);
						return shifterTarget;
					}
					break;
				default:
					if (edge.depRel.equals(scopeEntry) && edge.source.equals(shifter)) {
						shifterTarget = edge.target;
						System.out.println("found with edge: " + edge);
						if (sentimentList.contains(shifterTarget) && !shifterTarget.equals(shifter)) {
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
