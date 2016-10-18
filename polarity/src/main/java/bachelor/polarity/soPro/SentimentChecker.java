package bachelor.polarity.soPro;

import bachelor.polarity.salsa.corpora.elements.*;

import java.io.IOException;
import java.util.Set;

/**
 * Looks for sentiment expressions in every {@link SentenceObj} and adds the
 * sentiment information to the Tiger XML document.
 *
 */
public class SentimentChecker {
	private final SalsaAPIConnective salsaCon;
	private final SentenceList list;
	private final Set<Module> modules;

	/**
	 *
	 * @param salsaCon
	 *          A {@link SalsaAPIConnective} object used to add the sentiment
	 *          information to the Tiger XML corpus.
	 * @param modules
	 *          The modules that will be used to find sentiment expressions
	 * @param list
	 *          <!--TODO-->
	 */
	public SentimentChecker(SalsaAPIConnective salsaCon, SentenceList list, Set<Module> modules) {
		this.salsaCon = salsaCon;
		this.list = list;
		this.modules = modules;
	}

	/**
	 * Calls the <code>findFrames</code> method of each enabled {@link Module} and
	 * combines their output into a single {@link Semantics} object.
	 * 
	 * @param sentence
	 *          The {@link SentenceObj} that will be passed to each module
	 * @return A {@link Semantics} object
	 */
	private Semantics findSentiment(SentenceObj sentence) {
		/*
		 * The SALSA API and SALTO can handle multiple Frames objects in one
		 * sentence but evaltool can't. Thus we merge all frames into a single
		 * Frames object.
		 */
		final Frames frames = new Frames();
		for (Module module : modules) {

			for (Frame frame : module.findFrames(sentence)) {
				frames.addFrame(frame);
			}
		}
		final Semantics sem = new Semantics();
		sem.addFrames(frames);
		return sem;
	}

	/**
	 * Calls {@link #findSentiment(SentenceObj)} for every {@link SentenceObj} in
	 * {@link SentenceList} and exports the Salsa XML structure to filename. Also
	 * adds general specification of frames to the Salsa XML structure.
	 * 
	 * @param filename
	 *          The path of the output file.
	 */
	public void findSentiments(String filename) {

		Frames hframes = new Frames();
		Frame f1 = new Frame("SubjectiveExpression");
		Element e1 = new Element("Shifter", "true");
		f1.addElement(e1);
		hframes.addFrame(f1);
		Flags hflags = new Flags();
		Flag hflag1 = new Flag("Polarity without shift", "subjExpr");
		Flag hflag2 = new Flag("Polarity after shift", "subjExpr");
		Flag hflag3 = new Flag("Type", "shifter");
		Flag polarityFlag = new Flag("Polarity", "sentence");
		hflags.addFlag(hflag1);
		hflags.addFlag(hflag2);
		hflags.addFlag(hflag3);
		hflags.addFlag(polarityFlag);
		this.salsaCon.getHead().setFlags(hflags);
		this.salsaCon.getHead().setFrames(hframes);

		System.out.println("Analysing...");

		int listSize = list.sentenceList.size();

		for (int i = 0; i < listSize; i++) {
			SentenceObj stmp = list.sentenceList.get(i);
			Semantics sem = findSentiment(stmp);
			this.salsaCon.getSentences().get(i).setSem(sem);
			System.out.println("Sentence " + (i + 1) + " of " + list.sentenceList.size());
		}

		System.out.println((list.sentenceList.size()) + " sentences have been analysed successfully.");
		MyFileWriter writer = new MyFileWriter(filename);
		try {
			writer.writeToFile(this.salsaCon.getCorpus().toString());
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
}
