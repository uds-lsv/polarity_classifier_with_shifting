package bachelor.polarity.soPro;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import bachelor.polarity.salsa.corpora.elements.Fenode;
import bachelor.polarity.salsa.corpora.elements.Flag;
import bachelor.polarity.salsa.corpora.elements.Frame;
import bachelor.polarity.salsa.corpora.elements.FrameElement;
import bachelor.polarity.salsa.corpora.elements.Global;
import bachelor.polarity.salsa.corpora.elements.Nonterminal;
import bachelor.polarity.salsa.corpora.elements.Target;
import bachelor.polarity.salsa.corpora.elements.Terminal;

/**
 * Find subjective expressions (SE) and shifter and their targets. Optionally
 * use given SE locations. Add Frame data to Salsa XML output.
 * 
 * @author Maximilian
 *
 */
public class SubjectiveExpressionModule extends ModuleBasics implements Module {

	/**
	 * Constructs a new SubjectiveExpressionModule.
	 * 
	 * @param sentimentLex
	 *          A SentimentLex object in which the rules for finding sentiment
	 *          sources and targets are saved as SentimentUnits.
	 * @param shifterLex
	 *          A ShifterLex object in which the rules for finding shifter targets
	 *          are saved as ShifterUnits.
	 * @param pos_lookup_sentiment
	 *          Optionally use pos matching for subjective expressions. Compares
	 *          lexicon entries with words found in the input.
	 * @param pos_lookup_shifter
	 *          Optionally use pos matching for shifters. Compares lexicon entries
	 *          with words found in the input.
	 */
	public SubjectiveExpressionModule(SentimentLex sentimentLex, ShifterLex shifterLex, Boolean pos_lookup_sentiment,
			Boolean pos_lookup_shifter) {
		this.sentimentLex = sentimentLex;
		this.shifterLex = shifterLex;
		this.posLookupSentiment = pos_lookup_sentiment;
		this.posLookupShifter = pos_lookup_shifter;
	}

	/**
	 * Constructs a new SubjectiveExpressionModule with given SE locations.
	 * 
	 * @param salsa
	 *          A salsa API connective with information to the location of SE
	 *          expressions in the input sentences.
	 * @param sentimentLex
	 *          A SentimentLex object in which the rules for finding sentiment
	 *          sources and targets are saved as SentimentUnits.
	 * @param shifterLex
	 *          A ShifterLex object in which the rules for finding shifter targets
	 *          are saved as ShifterUnits.
	 * @param pos_lookup_sentiment
	 *          Optionally use pos matching for subjective expressions. Compares
	 *          lexicon entries with words found in the input.
	 * @param pos_lookup_shifter
	 *          Optionally use pos matching for shifters. Compares lexicon entries
	 *          with words found in the input.
	 */
	public SubjectiveExpressionModule(SalsaAPIConnective salsa, SentimentLex sentimentLex, ShifterLex shifterLex,
			Boolean pos_lookup_sentiment, Boolean pos_lookup_shifter) {
		this.salsa = salsa;
		this.sentimentLex = sentimentLex;
		this.shifterLex = shifterLex;
		this.posLookupSentiment = pos_lookup_sentiment;
		this.posLookupShifter = pos_lookup_shifter;
		this.usePresetSELocations = Boolean.TRUE;
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

		if (usePresetSELocations) {
			usePresetSELocations(sentence, sentimentList);
		}

		Double polaritySum = 0.0;
		Double polarityOfWord = 0.0;

		// Look up every word of the sentence in the shifterLex and sentimentLex
		// lexicons and add them to the shifterList or sentimentList.
		for (WordObj word : sentence.getWordList()) {
			ArrayList<ShifterUnit> shifterLexEntries = shifterLex.getAllShifters(word.getLemma());
			if (shifterLexEntries != null) {
				for (ShifterUnit shifterLexEntry : shifterLexEntries) {
					if (shifterLexEntry != null) {
						if (posLookupShifter) {
							posLookupShifter(shifterList, word, shifterLexEntry);
						} else {
							shifterList.add(word);
							break;
						}
					}
				}
			}

			if (!usePresetSELocations) {
				ArrayList<SentimentUnit> sentLexEntries = sentimentLex.getAllSentiments(word.getLemma());
				if (sentLexEntries != null) {
					for (SentimentUnit sentLexEntry : sentLexEntries) {
						if (sentLexEntry != null) {
							if (posLookupSentiment) {
								posLookupSentiment(sentimentList, word, sentLexEntry);
							} else {
								sentimentList.add(word);
								break;
							}
						}
					}
				}
			}
		}
		// Iterate over every found shifter and search for targets in their scope.
		// Also set frames.
		for (WordObj shifter : shifterList) {
			// Look for the shifterTarget
			WordObj shifterTarget = findShifterTarget(shifter, sentimentList, sentence, false);

			if (shifterTarget == null) {
				// No shifter target could be found. Look once more, but this time
				// expanded to words indirectly relating to the shifter in the
				// dependency parse.
				// E.g. an adj of a connected subj. "Nicht die [schlechteste] Sache"
				// instead
				// of "Nicht die schlechteste [Sache]".
				System.out.println("Second time shifter target search!");
				shifterTarget = findShifterTarget(shifter, sentimentList, sentence, true);
			}

			if (shifterTarget != null) {

				// Create Frame object for Salsa XML output
				final Frame frame = new Frame("SubjectiveExpression", frameIds.next());
				final FrameElementIds feIds = new FrameElementIds(frame);

				// Set Frames for the sentiment word
				final Target target = new Target();
				System.out.println("found shifterTarget: " + shifterTarget);
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

				// Compute the polarity value after a shift
				polarityOfWord = Double.valueOf(polarityValueStr);
				switch (shifterType) {
				case ShifterLex.SHIFTER_TYPE_ON_NEGATIVE:
					polarityCategory = "POS";
					break;
				case ShifterLex.SHIFTER_TYPE_ON_POSITIVE:
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
				// Also remove the shifter itself from the sentimentList.
				// Not necessary ?
				// sentimentList.remove(shifter);
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

		// System.out.println("MISSING:");
		// System.out.println(missingInGermanLex.toString());
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
	private WordObj findShifterTarget(WordObj shifter, ArrayList<WordObj> sentimentList, SentenceObj sentence,
			Boolean secondTime) {
		System.out.println("Shifter: " + shifter.getLemma());
		WordObj shifterTarget = null;
		ShifterUnit shifterUnit = shifterLex.getShifter(shifter.getLemma());
		// System.out.println("Scope: " +
		// Arrays.toString(shifterUnit.shifter_scope));

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
						if (secondTime) {
							shifterTarget = sentence.getGraph().getChild(shifterTarget, "attr");
							// old way
							/*
							 * if ((shifterTarget.getPosition() - 2) >= 0) {
							 * System.out.println("shifterTarget orig: " + shifterTarget);
							 * shifterTarget =
							 * sentence.getWordList().get(shifterTarget.getPosition() - 2);
							 * System.out.println("shifterTarget after: " + shifterTarget); }
							 */
						}
						if (sentimentList.contains(shifterTarget) && !shifterTarget.equals(shifter)) {
							return shifterTarget;
						}
					}
					/* System.out.println("edge: " + edge); */ continue;
				}
				switch (scopeEntry) {
				case "objp-*":
					if (edge.depRel.contains("objp") && edge.source.equals(shifter)) {
						System.out.println("edge: " + edge);
						shifterTarget = edge.target;
						if (secondTime) {
							shifterTarget = sentence.getGraph().getChild(shifterTarget, "attr");
						}
						if (sentimentList.contains(shifterTarget) && !shifterTarget.equals(shifter)) {
							return shifterTarget;
						}
					}
					break;
				case "attr-rev":
					if (edge.depRel.equals("attr") && edge.target.equals(shifter)) {
						System.out.println("edge: " + edge);
						shifterTarget = edge.source;
						if (secondTime) {
							shifterTarget = sentence.getGraph().getChild(shifterTarget, "attr");
						}
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
							if (secondTime) {
								shifterTarget = sentence.getGraph().getChild(shifterTarget, "attr");
							}
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
					if (secondTime) {
						shifterTarget = sentence.getGraph().getChild(shifterTarget, "attr");
					}
					// System.out.println(shifterTarget.toString());
					if (shifterTarget != null && !tree.getChildren(containingClause).contains(tree.getTerminal(shifterTarget))) {
						// TODO: check conditions
						// continue;
					}
					if (sentimentList.contains(shifterTarget) && !shifterTarget.equals(shifter)) {
						System.out.println("Found shifterTarget for Clause!: " + shifterTarget);
						return shifterTarget;
					}
					break;
				default:
					if (edge.depRel.equals(scopeEntry) && edge.source.equals(shifter)) {
						shifterTarget = edge.target;
						if (secondTime) {
							shifterTarget = sentence.getGraph().getChild(shifterTarget, "attr");
						}
						System.out.println("found with edge: " + edge);
						if (sentimentList.contains(shifterTarget) && !shifterTarget.equals(shifter)) {
							System.out.println("return: " + shifterTarget);
							return shifterTarget;
						}
					}
				}
			}
		}
		return null;
	}

}
