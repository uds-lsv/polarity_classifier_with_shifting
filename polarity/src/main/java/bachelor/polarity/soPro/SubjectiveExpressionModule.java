package bachelor.polarity.soPro;

import java.awt.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import bachelor.polarity.salsa.corpora.elements.Fenode;
import bachelor.polarity.salsa.corpora.elements.Frame;
import bachelor.polarity.salsa.corpora.elements.FrameElement;
import bachelor.polarity.salsa.corpora.elements.Target;

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
			WordObj shifterTarget = findShifterTarget(sentimentList);
			if (shifterTarget != null) {

				// Create Frame object
				final Frame frame = new Frame("SubjectiveExpression", frameIds.next());
				final FrameElementIds feIds = new FrameElementIds(frame);

				// Set Frames for the sentiment word
				final Target target = new Target();
				setFrames(sentence, frames, shifterTarget, frame, target);

				// Set FrameElement for the shifter
				final FrameElement shifterElement = new FrameElement(feIds.next(), "Shifter");
				shifterElement.addFenode(new Fenode(sentence.getTree().getTerminal(shifter).getId()));
				frame.addFe(shifterElement);

				// Remove the shifterTarget from the sentiment list so it doesn't get
				// another frame when iterating over the remaining sentiments.
				sentimentList.remove(shifterTarget);
			} else {
//				System.out.println("No shifterTarget found for " + shifter.getName());
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
		}
		return frames;
	}

	private WordObj findShifterTarget(ArrayList<WordObj> sentimentList) {
		// TODO Auto-generated method stub
		return null;
	}

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
