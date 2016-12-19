package bachelor.polarity.soPro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import bachelor.polarity.salsa.corpora.elements.Fenode;
import bachelor.polarity.salsa.corpora.elements.Frame;
import bachelor.polarity.salsa.corpora.elements.Frames;
import bachelor.polarity.salsa.corpora.elements.Global;
import bachelor.polarity.salsa.corpora.elements.Graph;
import bachelor.polarity.salsa.corpora.elements.Sentence;
import bachelor.polarity.salsa.corpora.elements.Target;
import bachelor.polarity.salsa.corpora.elements.Terminal;

public class ModuleBasics {

	protected SalsaAPIConnective salsa;
	protected SentimentLex sentimentLex;
	protected ShifterLex shifterLex;
	protected Boolean posLookupSentiment;
	protected Boolean posLookupShifter;
	protected Boolean usePresetSELocations = Boolean.FALSE;
	protected ArrayList<String> missingInGermanLex = new ArrayList<String>();
	/**
	 * Stores each sentence's polarity
	 */
	protected Collection<Global> globalsSentencePolarities = new ArrayList<Global>();

	public ModuleBasics() {
		super();
	}

	/**
	 * Globals are sentence flags. Stores each sentence's polarity.
	 * 
	 * @return ArrayList<Global> globalsSentencePolarities
	 */
	public Collection<Global> getGlobalsSentencePolarities() {
		return globalsSentencePolarities;
	}

	/**
	 * Compares pos tags of found words in a sentence with shifter lexicon
	 * entries.
	 * 
	 * @param shifterList
	 *          the list of found shifters in the current sentence.
	 * @param word
	 *          the current word's WordObj.
	 * @param shifterLexEntry
	 *          the entry for the shifter in the shifter lexicon.
	 */
	public void posLookupShifter(ArrayList<WordObj> shifterList, WordObj word, ShifterUnit shifterLexEntry) {
		if ((word.getPos().startsWith("N") || word.getPos().equals("PIS")) && shifterLexEntry.shifter_pos.equals("nomen")) {
			shifterList.add(word);
		} else if (word.getPos().startsWith("ADJ") && shifterLexEntry.shifter_pos.equals("adj")) {
			shifterList.add(word);
		} else if (word.getPos().startsWith("ADV") && shifterLexEntry.shifter_pos.equals("adv")) {
			shifterList.add(word);
		} else if (word.getPos().startsWith("V") && shifterLexEntry.shifter_pos.equals("verb")) {
			shifterList.add(word);
		} else if (word.getPos().equals("PTKNEG")) {
			shifterList.add(word);
		} else if (word.getPos().equals("APPR") && shifterLexEntry.shifter_pos.equals("appr")) {
			shifterList.add(word);
		} else {
			ShifterUnit shifterLexEntryNew = shifterLex.getShifter(word.getName());
			if (shifterLexEntryNew != null && shifterLexEntry != shifterLexEntryNew) {
				posLookupShifter(shifterList, word, shifterLexEntryNew);
			} else {
				System.out.println("Shifter POS-MISMATCH!");
				System.out.println("word: " + word.getName() + " pos: " + word.getPos());
				System.out.println("shifterLex entry pos: " + shifterLexEntry.shifter_pos);
			}
		}
	}

	/**
	 * Compares pos tags of found words in a sentence with sentiment lexicon
	 * entries.
	 * 
	 * @param sentimentList
	 *          the list of found sentiments in the current sentence.
	 * @param word
	 *          the current word's WordObj.
	 * @param sentLexEntry
	 *          the entry for the sentiment word in the sentiment lexicon.
	 */
	public void posLookupSentiment(ArrayList<WordObj> sentimentList, WordObj word, SentimentUnit sentLexEntry) {
		if (word.getPos().startsWith("N") && sentLexEntry.pos.equals("nomen")) {
			sentimentList.add(word);
		} else if (word.getPos().startsWith("V") && sentLexEntry.pos.equals("verben")) {
			sentimentList.add(word);
		} else if (word.getPos().startsWith("A") && sentLexEntry.pos.equals("adj")) {
			sentimentList.add(word);
		} else {
			// Check for another possible sentLexEntry using the exact word instead of
			// its Lemma
			// Example: "abweisen" vs "abweisend" gets found this way.
			SentimentUnit sentLexEntryNew = sentimentLex.getSentiment(word.getName());
			if (sentLexEntryNew != null && sentLexEntry != sentLexEntryNew) {
				posLookupSentiment(sentimentList, word, sentLexEntryNew);
			} else {
				System.out.println("Sentiment POS-MISMATCH!");
				System.out.println("word: " + word.getName() + " pos: " + word.getPos());
				System.out.println("sentimentLex entry pos: " + sentLexEntry.pos);
			}
		}
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
	protected void setFrames(SentenceObj sentence, final Collection<Frame> frames, WordObj sentiment, final Frame frame, final Target target) {
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
			Fenode particleFeNode = new Fenode(sentence.getTree().getTerminal(particle).getId());
			if (!target.getFenodes().contains(particleFeNode)) {
				target.addFenode(particleFeNode);
			}
		}
		frame.setTarget(target);
		frames.add(frame);
	}

	/**
	 * If preset SE locations are given, identifies and uses them.
	 * 
	 * @param sentence
	 *          The current sentence.
	 * @param sentimentList
	 *          List of subjective expressions in the current sentence.
	 */
	public void usePresetSELocations(SentenceObj sentence, ArrayList<WordObj> sentimentList) {
		ArrayList<Sentence> salsaSentences = salsa.getBody().getSentences();
	
		Sentence presetSentence = null;
		Graph presetGraph = null;
		ConstituencyTree presetTree = null;
	
		// Find the current sentence in the salsa corpus
		for (int i = 0; i < salsaSentences.size(); i++) {
			presetSentence = salsaSentences.get(i);
			presetGraph = presetSentence.getGraph();
			presetTree = new ConstituencyTree(presetGraph);
			if (sentence.getTree().toString().equals(presetTree.toString())) {
				break;
			}
		}
	
		// Collect the preset SEs
		ArrayList<Frames> presetFrames = presetSentence.getSem().getFrames();
	
		// First collect Ids of SE fenodes
		ArrayList<String> fenodeIds = new ArrayList<>();
	
		for (Frames allPresetFrames : presetFrames) {
			for (int i = 0; i < allPresetFrames.getFrames().size(); i++) {
				Frame presetFrame = allPresetFrames.getFrames().get(i);
				ArrayList<Fenode> fenodes = presetFrame.getTarget().getFenodes();
				for (Fenode fe : fenodes) {
					fenodeIds.add(fe.getIdref().getId());
				}
			}
		}
	
		// Compare fenodeIds with terminal Ids of the tree terminals to get to the
		// WordObjs.
		int wordIndex = 0;
		ArrayList<WordObj> particles = new ArrayList<WordObj>();
		
		for (Terminal terminal : presetTree.getTerminals()) {
			wordIndex += 1;
			String terminalId = terminal.getId().getId();
			for (String fenodeId : fenodeIds) {
				if (terminalId.equals(fenodeId)) {
					WordObj wordObj = sentence.getWordList().get(wordIndex - 1);
					// System.out.println("found preset SE: " + terminal.getWord());
					// System.out.println("with wordIndex: " + wordIndex);
					sentimentList.add(wordObj);
					// System.out.println("added given SE: " + wordObj);
					if(wordObj.getIsParticleVerb()){
						particles.add(wordObj.getParticle());
					}
				}
			}
		}
		// In case of multi word expressions, things might be added twice.
		// Remove particles of particle words as they are already accounted for.
		sentimentList.removeAll(particles);
	
		// Preset SEs might not have an entry as SentimentUnit in the SentimentLex,
		// with lemma, pos, value, etc.
		// Create dummy entries in those cases
		for (WordObj sentiment : sentimentList) {
			if (sentimentLex.getAllSentiments(sentiment.getLemma()) == null) {
				System.out.println("no entry for: " + sentiment.getLemma());
				missingInGermanLex.add(sentiment.getLemma());
				SentimentUnit newUnit = new SentimentUnit(sentiment.getLemma(), "UNKNOWN", "0.0", sentiment.getPos(),
						Boolean.FALSE);
				sentimentLex.addSentiment(newUnit);
			}
		}
	}

}