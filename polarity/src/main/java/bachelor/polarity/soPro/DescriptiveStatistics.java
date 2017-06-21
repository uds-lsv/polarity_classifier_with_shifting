package bachelor.polarity.soPro;

import java.util.ArrayList;
import java.util.HashMap;

import bachelor.polarity.salsa.corpora.elements.Fenode;
import bachelor.polarity.salsa.corpora.elements.Frame;
import bachelor.polarity.salsa.corpora.elements.FrameElement;
import bachelor.polarity.salsa.corpora.elements.Frames;
import bachelor.polarity.salsa.corpora.elements.Graph;
import bachelor.polarity.salsa.corpora.elements.Sentence;
import bachelor.polarity.salsa.corpora.elements.Terminal;

public class DescriptiveStatistics {

	public static void main(String[] args) {

		// Read config
		java.nio.file.Path goldStandardPath = java.nio.file.Paths.get("src", "main", "resources", "Input",
				"constituency_parse.annotated_extended.only_polar.wrongsubcorpusRemoved.xml");
		System.out.println("Reading goldStandard from " + goldStandardPath.toString());

		String text_input = java.nio.file.Paths
				.get("src", "main", "resources", "Input", "raw_text.wrongsubcorpusRemoved.txt").toString();
		String preset_se_input = goldStandardPath.toString();

		// Read in raw input text and create SentenceList based on it.
		System.out.println("Reading rawText from : " + text_input);
		SentenceList sentences = new SentenceList();
		sentences.rawToSentenceList(text_input);

		// Read in dependency parse file and create a DependencyGraph object for
		// each sentence.
		String dependency_input = java.nio.file.Paths
				.get("src", "main", "resources", "Input", "dependency_parse.wrongsubcorpusRemoved.txt").toString();
		sentences.readDependencyParse(dependency_input);

		// Read in preset se file
		System.out.println("Reader preset se file from " + preset_se_input + "...");
		SalsaAPIConnective salsa = new SalsaAPIConnective(preset_se_input, sentences);

		// Read in sentiment lexicon.
		SentimentLex sentimentLex = new SentimentLex(true);
		sentimentLex
				.fileToLex(java.nio.file.Paths.get("src", "main", "resources", "dictionaries", "germanlex.txt").toString());

		// Read in shifter lexicon.
		ShifterLex shifterLex = new ShifterLex(true);
		shifterLex.fileToLex(java.nio.file.Paths
				.get("src", "main", "resources", "dictionaries", "shifter_lex_german_extended_revised.txt").toString());

		// Counter
		int n_sentences_total = 0;
		int n_se = 0;
		int n_se_with_shifter = 0;

		int n_shifter = 0;
		int n_shifter_p = 0;
		int n_shifter_n = 0;
		int n_shifter_g = 0;
		int n_shifter_left_of_se = 0;
		int n_shifter_right_of_se = 0;

		int n_se_pos = 0;
		int n_se_neg = 0;
		int n_se_unknown = 0;

		int n_shifter_unknown = 0;

		int n_sentence_with_shifter_modification = 0;
		double p_sentence_with_shifter_mod_compared_to_all_s = 0.0;
		double p_se_with_shifter_compared_to_se_without = 0.0;

		int n_double_negation = 0;

		ArrayList<Sentence> salsaSentences = salsa.getBody().getSentences();
		ArrayList<WordObj> sentimentList = new ArrayList<>();
		SentenceObj sentence = null;
		for (Sentence sentenceSentence : salsaSentences) {
			Boolean already_counted = false;
			n_sentences_total += 1;
			System.out.println("Sentence " + n_sentences_total);
			sentence = sentences.sentenceList.get(n_sentences_total - 1);

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
			ArrayList<String> fenodeIdsMWE = new ArrayList<>();
			String shifterFeId = new String();
			HashMap<String, ArrayList<String>> se_shifter_map = new HashMap<>();

			for (Frames allPresetFrames : presetFrames) {
				for (int i = 0; i < allPresetFrames.getFrames().size(); i++) {
					Frame presetFrame = allPresetFrames.getFrames().get(i);
					Boolean hasShifter = false;
					ArrayList<Fenode> fenodes = presetFrame.getTarget().getFenodes();
					ArrayList<FrameElement> fes_shifter = new ArrayList<>();
					fes_shifter = presetFrame.getFes();
					// System.out.println("presetFrame.getFes: " +
					// presetFrame.getFes().toString());
					FrameElement shifterFe = null;
					ArrayList<Fenode> shifterFenodes = new ArrayList<>();
					ArrayList<String> shifterFenodeIds = new ArrayList<>();

					if (fes_shifter.size() > 1) {
						System.err.println("MEHR ALS EIN SHIFTER??");
						System.err.println(fes_shifter.toString());
					}
					if (!fes_shifter.isEmpty()) {
						hasShifter = true;
						shifterFe = fes_shifter.get(0);
						shifterFeId = shifterFe.getId().getId();
						shifterFenodes = shifterFe.getFenodes();
						for (Fenode shfe : shifterFenodes) {
							shifterFenodeIds.add(shfe.getIdref().getId());
						}
					}
					if (fenodes.size() == 2) {
						for (Fenode fe : fenodes) {
							fenodeIdsMWE.add(fe.getIdref().getId());
							if (hasShifter) {
								se_shifter_map.put(fe.getIdref().getId(), shifterFenodeIds);
							}
						}
					} else {
						for (Fenode fe : fenodes) {
							fenodeIds.add(fe.getIdref().getId());
							// add shifter to se
							if (hasShifter) {
								// name="SubjectiveExpression" id="1_f2"
								// name="Shifter" id="1_f2_e1"
								se_shifter_map.put(fe.getIdref().getId(), shifterFenodeIds);
							}
						}
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
						System.out.println("found preset SE: " + terminal.getWord());
						n_se += 1;
						// check se type
						SentimentUnit sentimentUnit = sentimentLex.getSentiment(wordObj.getLemma());
						if (sentimentUnit != null) {
							if (sentimentUnit.getTyp().equals("POS")) {
								n_se_pos += 1;
							} else if (sentimentUnit.getTyp().equals("NEG")) {
								n_se_neg += 1;
							} else {
								n_se_unknown += 1;
							}
						} else {
							n_se_unknown += 1;
						}

						// check for shifter
						if (se_shifter_map.containsKey(fenodeId)) {
							n_se_with_shifter += 1;
							if (!already_counted) {
								n_sentence_with_shifter_modification += 1;
								already_counted = true;
							}
							for (String shifterFenodeId : se_shifter_map.get(fenodeId)) {
								System.out.println("se_shifter_map: " + se_shifter_map.get(fenodeId).toString());
								int shifterIndex = 0;
								n_shifter += 1;
								// get shifter wordObj
								for (Terminal terminal2 : presetTree.getTerminals()) {
									shifterIndex += 1;
									String terminalId2 = terminal2.getId().getId();
									if (terminalId2.equals(shifterFenodeId)) {
										System.out.println("found shifter to " + terminal.getWord() + ": " + terminal2.getWord());
										WordObj shifterWordObj = sentence.getWordList().get(shifterIndex - 1);
										// check shifter position in relation to se
										if (shifterIndex < wordIndex) {
											n_shifter_left_of_se += 1;
										} else if (shifterIndex > wordIndex) {
											n_shifter_right_of_se += 1;
										} else {
											System.err.println("what??? shifterIndex = wordIndex");
										}
										// check shifter orientation
										if (shifterLex.getShifter(shifterWordObj.getLemma()) == null) {
											System.err.println("unknown shifter: " + terminal2.getWord());
											n_shifter_unknown += 1;
										} else {
											ShifterUnit shifterUnit = shifterLex.getShifter(shifterWordObj.getLemma());
											String shifterType = shifterUnit.getType();
											switch (shifterType) {
											case ShifterLex.SHIFTER_TYPE_GENERAL:
												n_shifter_g += 1;
												break;
											case ShifterLex.SHIFTER_TYPE_ON_NEGATIVE:
												n_shifter_n += 1;
												break;
											case ShifterLex.SHIFTER_TYPE_ON_POSITIVE:
												n_shifter_p += 1;
											}
										}
									}
								}
								// se_shifter_map.remove(fenodeId);
							}
						}

						sentimentList.add(wordObj);
						// System.out.println("added given SE: " + wordObj);
						if (wordObj.getIsParticleVerb()) {
							particles.add(wordObj.getParticle());
						}
					}
				}
			}
			// In case of multi word expressions, things might be added twice.
			// Remove particles of particle words as they are already accounted for.
			sentimentList.removeAll(particles);

			// ************************CASE Particle MWE***************************
			// Compare fenodeIds with terminal Ids of the tree terminals to get to the
			// WordObjs.
			if (fenodeIdsMWE.size() == 2) {
				wordIndex = 0;
				int index = 0;
				Terminal terminal1 = new Terminal();
				WordObj wordObjFirst = null;
				WordObj wordObjSecond = null;

				for (Terminal terminal : presetTree.getTerminals()) {
					wordIndex += 1;
					String terminalId = terminal.getId().getId();
					for (String fenodeId : fenodeIdsMWE) {
						if (terminalId.equals(fenodeId)) {
							index++;
							if (fenodeIdsMWE.size() == 2) {
								if (index == 1) {
									wordObjFirst = sentence.getWordList().get(wordIndex - 1);
									wordObjFirst.setIsParticleVerb(true);
									terminal1 = terminal;
								}
								if (index == 2) {
									wordObjSecond = sentence.getWordList().get(wordIndex - 1);
									wordObjSecond.setIsParticleVerb(true);
								}
							}
						}
						if (wordObjFirst != null && wordObjSecond != null && !sentimentList.contains(wordObjFirst)) {
							System.out.println("added " + wordObjFirst + " with particle: " + wordObjSecond);
							wordObjFirst.setParticle(wordObjSecond);
							sentimentList.add(wordObjFirst);

							n_se += 1;
							// check se type
							SentimentUnit sentimentUnit = sentimentLex.getSentiment(wordObjFirst.getLemma());
							if (sentimentUnit != null) {
								if (sentimentUnit.getTyp().equals("POS")) {
									n_se_pos += 1;
								} else if (sentimentUnit.getTyp().equals("NEG")) {
									n_se_neg += 1;
								} else {
									n_se_unknown += 1;
								}
							} else {
								n_se_unknown += 1;
							}

							// check for shifter
							if (se_shifter_map.containsKey(fenodeId)) {
								n_se_with_shifter += 1;
								if (!already_counted) {
									n_sentence_with_shifter_modification += 1;
									already_counted = true;
								}
								for (String shifterFenodeId : se_shifter_map.get(fenodeId)) {
									int shifterIndex = 0;
									n_shifter += 1;
									// get shifter wordObj
									for (Terminal terminal2 : presetTree.getTerminals()) {
										shifterIndex += 1;
										String terminalId2 = terminal2.getId().getId();
										if (terminalId2.equals(shifterFenodeId)) {
											System.out.println("found shifter to " + terminal1.getWord() + ": " + terminal2.getWord());
											WordObj shifterWordObj = sentence.getWordList().get(shifterIndex - 1);
											// check shifter position in relation to se
											if (shifterIndex < wordIndex) {
												n_shifter_left_of_se += 1;
											} else if (shifterIndex > wordIndex) {
												n_shifter_right_of_se += 1;
											} else {
												System.err.println("what??? shifterIndex = wordIndex");
											}
											// check shifter orientation
											if (shifterLex.getShifter(shifterWordObj.getLemma()) == null) {
												System.err.println("unknown shifter: " + terminal2.getWord());
												n_shifter_unknown += 1;
											} else {
												ShifterUnit shifterUnit = shifterLex.getShifter(shifterWordObj.getLemma());
												String shifterType = shifterUnit.getType();
												switch (shifterType) {
												case ShifterLex.SHIFTER_TYPE_GENERAL:
													n_shifter_g += 1;
													break;
												case ShifterLex.SHIFTER_TYPE_ON_NEGATIVE:
													n_shifter_n += 1;
													break;
												case ShifterLex.SHIFTER_TYPE_ON_POSITIVE:
													n_shifter_p += 1;
												}
											}
										}
									}
								}
							}

						}
					}
				}
			}

			// Preset SEs might not have an entry as SentimentUnit in the
			// SentimentLex,
			// with lemma, pos, value, etc.
			// Create dummy entries in those cases
			for (WordObj sentiment : sentimentList) {
				if (sentimentLex.getSentiment(sentiment.getLemma()) == null) {
					// System.out.println("no entry for: " + sentiment.getLemma());
					SentimentUnit newUnit = new SentimentUnit(sentiment.getLemma(), "UNKNOWN", "0.0", sentiment.getPos(),
							Boolean.FALSE);
					sentimentLex.addSentiment(newUnit);
				}
			}
		}
		// print shit
		System.out.println("n_sentences_total: " + n_sentences_total);
		System.out.println("n_sentence_with_shifter_modification: " + n_sentence_with_shifter_modification);

		System.out.println("n_se: " + n_se);
		System.out.println("n_se_neg: " + n_se_neg);
		System.out.println("n_se_pos: " + n_se_pos);
		System.out.println("n_se_unknown: " + n_se_unknown);
		System.out.println("n_se_with_shifter:_" + n_se_with_shifter);

		System.out.println("n_shifter: " + n_shifter);
		System.out.println("n_shifter_g: " + n_shifter_g);
		System.out.println("n_shifter_n: " + n_shifter_n);
		System.out.println("n_shifter_p: " + n_shifter_p);
		System.out.println("n_shifter_unknown: " + n_shifter_unknown);
		System.out.println("n_shifter_left_of_se: " + n_shifter_left_of_se);
		System.out.println("n_shifter_right_of_se: " + n_shifter_right_of_se);

		p_sentence_with_shifter_mod_compared_to_all_s = (n_sentences_total / n_sentence_with_shifter_modification);
		p_se_with_shifter_compared_to_se_without = (n_se / n_se_with_shifter);

		System.out
				.println("p_sentence_with_shifter_mod_compared_to_all_s: " + p_sentence_with_shifter_mod_compared_to_all_s);
		System.out.println("p_se_with_shifter_compared_to_se_without: " + p_se_with_shifter_compared_to_se_without);
	}
}
