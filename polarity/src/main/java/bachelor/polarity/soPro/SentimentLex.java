package bachelor.polarity.soPro;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

/**
 * SentimentLex object contains all informations from a given sentiment lexicon
 *
 */
public class SentimentLex {

	List<SentimentUnit> sentimentList = new ArrayList<SentimentUnit>();
	Map<String, SentimentUnit> sentimentMap = new HashMap<String, SentimentUnit>();
	boolean flexibleMWEs = false;
	String[] collectSubjectiveExpressions = { "" };

	/**
	 * Constructs a new SentimentLex
	 *
	 * @param flexMWE
	 *          indicates if the multi word expressions in the lexicon should be
	 *          interpreted in a flexible way (more info see
	 *          {@link #mweFlexibility(SentimentUnit)}).
	 */
	public SentimentLex(boolean flexMWE) {
		this.flexibleMWEs = flexMWE;
	}

	// Lists containing words for flexible mwe interpretations.
	// Used in the method mweFlexibility of this class.
	/**
	 * List containing reflexive nominatives for flexible interpretations. If mwe
	 * contains "sich". "mich", "Mich", "dich", "Dich", "ihn", "Ihn", "es", "Es",
	 * "euch", "Euch", "Sie"
	 */
	private List<String> sichAlternatives = Arrays.asList("mich", "Mich", "dich", "Dich", "ihn", "Ihn", "es", "Es",
			"euch", "Euch", "Sie");

	/**
	 * List containing seinenAlternatives for flexible interpretations. If mwe
	 * contains "seinen" or "seinem". "meine", "Meine", "deine", "Deine", "ihre",
	 * "eure", "Ihre", "unsere", "Unsere"
	 */
	private List<String> seinenAlternatives = Arrays.asList("meine", "Meine", "deine", "Deine", "ihre", "eure", "Ihre",
			"unsere", "Unsere");

	/**
	 * List containing words for flexible interpretations. If mwe contains "den".
	 * "einen", "dem"
	 */
	private List<String> denAlternatives = Arrays.asList("einen", "dem");

	/**
	 * List containing words for flexible interpretations. If mwe contains "ein".
	 * "kein"
	 */
	private List<String> einAlternatives = Arrays.asList("kein");

	/**
	 * List containing words for flexible interpretations. If mwe contains
	 * "einen". "keinen"
	 */
	private List<String> einenAlternatives = Arrays.asList("keinen");

	/**
	 * List containing words for flexible interpretations. If mwe contains "sein".
	 * "werden"
	 */
	private List<String> seinAlternatives = Arrays.asList("werden");

	/**
	 * Returns the SentimentUnit corresponding to the given name or null.
	 *
	 * @param name
	 *          The name of the sentiment to look for.
	 * @return SentimentUnit for a given name or null if no SentimentUnit with the
	 *         given name exists
	 */
	public SentimentUnit getSentiment(String name) {
		for (int i = 0; i < sentimentList.size(); i++) {
			SentimentUnit tmp = (SentimentUnit) sentimentList.get(i);
			if (tmp.mwe) {
				String mweParts = new String();
				for (String mwePart : tmp.collocations) {
					mweParts += mwePart + "_";
				}
				mweParts += tmp.name;
				if (mweParts.equals(name)) {
					return tmp;
				}
			} else {
				if (tmp.name.equals(name)) {
					return tmp;
				}
			}
		}
		return null;
	}

	/**
	 * Returns the SentimentUnits (more than one if there is more than one lexicon
	 * entry) corresponding to the given name or null. Up to 50 entries possible
	 * for one sentiment.
	 *
	 * @param name
	 *          The name of the sentiment to look for.
	 * @return SentimentUnit for a given name or null if no SentimentUnit with the
	 *         given name exists
	 */
	public ArrayList<SentimentUnit> getAllSentiments(String name) {
		ArrayList<SentimentUnit> sentiments = new ArrayList<SentimentUnit>();
		for (int i = 0; i < sentimentList.size(); i++) {
			SentimentUnit tmp = (SentimentUnit) sentimentList.get(i);
			if (tmp.mwe) {
				String mweParts = new String();
				for (String mwePart : tmp.collocations) {
					mweParts += mwePart + "_";
				}
				mweParts += tmp.name;
				if (mweParts.equals(name)) {
					sentiments.add(tmp);
				}
			} else {
				if (tmp.name.equals(name)) {
					sentiments.add(tmp);
				}
			}
		}
		if (!sentiments.isEmpty()) {
			return sentiments;
		} else {
			return null;
		}
	}

	/**
	 * Adds a sentiment expression to the internal lexicon, possibly interpreting
	 * the with some flexibility (for MWEs).
	 *
	 * @param sentiment
	 *          is added to SentimentLex
	 * @return nothing (void)
	 */
	public void addSentiment(SentimentUnit sentiment) {
		sentimentList.add(sentiment);
		addToMap(sentiment.name, sentiment);

		if (sentiment.mwe && this.flexibleMWEs) {
			for (SentimentUnit flex : mweFlexibility(sentiment)) {
				sentimentList.add(flex);
				addToMap(flex.name, flex);

			}
		}
	}

	/**
	 * Returns an ArrayList of SenimentUnits that are the result of flexible
	 * interpretation of the given MWE SentimentUnit. Flexibility is applied
	 * w.r.t. the following words that are found in the MWE: 1) Reflexive
	 * pronouns: sich --> sich, mich, dich, ihn, ... etc 2) Possessive pronouns:
	 * seinen --> seinen, meinen, deinen, ... etc 3) Ein(en)/Kein(en): ein(en) -->
	 * ein(en), kein(en) 4) Sein/Werden: sein --> sein, werden 5) Other: den -->
	 * den, einen, dem
	 *
	 * @param sentiment
	 *          given a MWE SentimentUnit
	 * @return ArrayList of SentimentUnits that are created by flexibility
	 */
	public ArrayList<SentimentUnit> mweFlexibility(SentimentUnit sentiment) {
		boolean containsSich = false; // sich > mich, dich, ...
		int sichIndex = -1;
		boolean containsSeinen = false; // seinen > meinen, deinen, ...
		int seinenIndex = -1;
		boolean containsDen = false; // den > einen, dem
		int denIndex = -1;
		boolean containsEin = false; // ein > kein
		int einIndex = -1;
		boolean containsEinen = false; // einen > keinen
		int einenIndex = -1;
		boolean containsSein = false; // sein > werden
		int seinIndex = -1;

		for (int j = 0; j < sentiment.collocations.length; j++) {
			if (sentiment.collocations[j].equals("sich")) {
				containsSich = true;
				sichIndex = j;
			}
			if (sentiment.collocations[j].equals("seinen") || sentiment.collocations[j].equals("seinem")) {
				containsSeinen = true;
				seinenIndex = j;
			}
			if (sentiment.collocations[j].equals("ein")) {
				containsEin = true;
				einIndex = j;
			}
			if (sentiment.collocations[j].equals("einen")) {
				containsEinen = true;
				einenIndex = j;
			}
			if (sentiment.collocations[j].equals("den")) {
				containsDen = true;
				denIndex = j;
			}
			if (sentiment.collocations[j].equals("sein")) {
				containsSein = true;
				seinIndex = j;
			}
		}

		ArrayList<SentimentUnit> alternativeSUnits = new ArrayList<SentimentUnit>();
		if (containsSich) {
			String[] alternativeCollocations;
			SentimentUnit alternativeSentiment;

			for (String reflexive : sichAlternatives) {
				alternativeSentiment = new SentimentUnit(sentiment.name, sentiment.category, sentiment.value, sentiment.pos,
						sentiment.mwe);
				alternativeCollocations = sentiment.collocations.clone();
				alternativeCollocations[sichIndex] = reflexive;
				alternativeSentiment.collocations = alternativeCollocations;
				alternativeSUnits.add(alternativeSentiment);
			}
		}
		if (containsSeinen) {
			String[] alternativeCollocations;
			SentimentUnit alternativeSentiment;
			for (String reflexive : seinenAlternatives) {
				alternativeSentiment = new SentimentUnit(sentiment.name, sentiment.category, sentiment.value, sentiment.pos,
						sentiment.mwe);
				alternativeCollocations = sentiment.collocations.clone();
				alternativeCollocations[seinenIndex] = reflexive;
				alternativeSentiment.collocations = alternativeCollocations;
				alternativeSUnits.add(alternativeSentiment);
			}
		}

		if (containsDen) {
			SentimentUnit alternativeSentiment;
			String[] alternativeCollocations;
			for (String denOption : denAlternatives) {
				alternativeSentiment = new SentimentUnit(sentiment.name, sentiment.category, sentiment.value, sentiment.pos,
						sentiment.mwe);
				alternativeCollocations = sentiment.collocations.clone();
				alternativeCollocations[denIndex] = denOption;
				alternativeSentiment.collocations = alternativeCollocations;
				alternativeSUnits.add(alternativeSentiment);
			}
		}
		if (containsEin) {
			SentimentUnit alternativeSentiment;
			String[] alternativeCollocations;
			for (String einOption : einAlternatives) {
				alternativeSentiment = new SentimentUnit(sentiment.name, sentiment.category, sentiment.value, sentiment.pos,
						sentiment.mwe);
				alternativeCollocations = sentiment.collocations.clone();
				alternativeCollocations[einIndex] = einOption;
				alternativeSentiment.collocations = alternativeCollocations;
				alternativeSUnits.add(alternativeSentiment);
			}
		}
		if (containsEinen) {
			SentimentUnit alternativeSentiment;
			String[] alternativeCollocations;
			for (String einenOption : einenAlternatives) {
				alternativeSentiment = new SentimentUnit(sentiment.name, sentiment.category, sentiment.value, sentiment.pos,
						sentiment.mwe);
				alternativeCollocations = sentiment.collocations.clone();
				alternativeCollocations[einenIndex] = einenOption;
				alternativeSentiment.collocations = alternativeCollocations;
				alternativeSUnits.add(alternativeSentiment);
			}
		}
		if (containsSein) {
			SentimentUnit alternativeSentiment;
			String[] alternativeCollocations;
			for (String seinOption : seinAlternatives) {
				alternativeSentiment = new SentimentUnit(sentiment.name, sentiment.category, sentiment.value, sentiment.pos,
						sentiment.mwe);
				alternativeCollocations = sentiment.collocations.clone();
				alternativeCollocations[seinIndex] = seinOption;
				alternativeSentiment.collocations = alternativeCollocations;
				alternativeSUnits.add(alternativeSentiment);
			}
		}
		if (sentiment.name.equals("sein")) {
			SentimentUnit alternativeSentiment = new SentimentUnit("werden", sentiment.category, sentiment.value,
					sentiment.pos, sentiment.mwe);
			alternativeSentiment.collocations = sentiment.collocations.clone();
			alternativeSUnits.add(alternativeSentiment);
		}

		return alternativeSUnits;
	}

	/**
	 * @param sentiment
	 *          is removed from SentimentLex
	 */
	public void removeSentiment(SentimentUnit sentiment) {
		sentimentList.remove(sentiment);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String printer = "Name[Category][Value][Pos]";
		for (int i = 0; i < sentimentList.size(); i++) {
			printer = printer + "\n";
			printer = printer + sentimentList.get(i);
		}
		return printer;
	}

	/**
	 * Reads in sentiment lexicon from filename
	 *
	 * @param filename
	 */
	public void fileToLex(String filename) {
		String wordFromInput = new String();
		String category = new String();
		Double value = 0.0;
		String pos = new String();
		Boolean mwe = Boolean.FALSE;
		Scanner scanner;

		try {
			scanner = new Scanner(new File(filename), "UTF-8");
			scanner.useLocale(Locale.GERMANY);
			String inputLine;

			while (scanner.hasNext()) {
				inputLine = scanner.nextLine();

				// Ignore lines starting with "%%" (comments).
				if (!(inputLine.startsWith("%%"))) {

					// Ignore lines without the correct form.
					// Correct form example: fehlschlagen NEG=0.7 verben
					if (inputLine.matches("[\\w+[-_äöüÄÖÜß]*\\w*]+\\s\\w\\w\\w=\\d.?\\d?\\s\\w+")) {

						wordFromInput = inputLine.substring(0, inputLine.indexOf(" "));
						// System.out.println("word: " + wordFromInput);

						category = inputLine.substring(inputLine.indexOf(" ") + 1, inputLine.indexOf("="));
						// System.out.println("category: " + category);

						// TODO make this work for "0.7" as well as "0,7".
						// Locale original = Locale.getDefault();
						// Locale.setDefault(new Locale("de", "DE"));
						Scanner doubleScanner = new Scanner(inputLine.substring(inputLine.indexOf("=") + 1).replace('.', ','));
						if (doubleScanner.hasNextDouble()) {
							value = doubleScanner.nextDouble();
							// System.out.println("valueToSetFeatureTo: " + value);
						} else {
							System.out.println("no valueToSetFeatureTo has been found for: " + wordFromInput);
						}
						doubleScanner.close();
						// Locale.setDefault(original);

						pos = inputLine.substring(inputLine.lastIndexOf(" ") + 1, inputLine.length());
						// System.out.println("pos: " + pos);

						mwe = wordFromInput.contains("_");

						if (category != null && value != null && pos != null) {
							SentimentUnit unit = new SentimentUnit(wordFromInput, category, value.toString(), pos, mwe);
							this.addSentiment(unit);
						} else {
							System.err.println("Sentiment-Lexicon entry for " + wordFromInput + " is incomplete!");
						}

					} else {
						System.err.println("Line with wrong format in Sentiment-Lexicon, will be ignored: ");
						System.err.println("\"" + inputLine + "\"");
						// getContext().getLogger().log(Level.WARNING, "Found: " +
						// inputLine);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Builds a {@link #sentimentMap} from a given {@link #sentimentList}
	 *
	 * @param key
	 * @param value
	 */
	public void addToMap(String key, SentimentUnit value) {
		if (this.sentimentMap.containsKey(key)) {
			key = key + "+";
			addToMap(key, value);
		} else {
			this.sentimentMap.put(key, value);
		}
	}
}
