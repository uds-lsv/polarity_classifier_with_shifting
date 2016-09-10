/* Based on the dictionarzAnnotator from TU Darmstadt. See copyright below.
 * 
 * Copyright 2010
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische UniversitÃ¤t Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Modified by mwolf.
 */
package bachelor.polarity;

import static org.apache.uima.fit.util.CasUtil.getType;
import static org.apache.uima.fit.util.JCasUtil.select;
import static org.apache.uima.fit.util.JCasUtil.selectCovered;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import bachelor.polarity.types.PolarExpression;
import bachelor.polarity.types.Shifter;
import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceUtils;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.dictionaryannotator.PhraseTree;

/**
 * Takes a plain text file with phrases as input and annotates the phrases in
 * the CAS file.
 *
 * The component requires that {@link Token}s and {@link Sentence}es are
 * annotated in the CAS.
 *
 * The format of the phrase file is one phrase per line, tokens are separated by
 * space:
 *
 * <pre>
 * this is a phrase
 * another phrase
 * </pre>
 *
 */
@TypeCapability(inputs = { "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token",
		"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence" })
public class SentimentLexiconAnnotator extends JCasAnnotator_ImplBase {
	/**
	 * The file must contain one phrase per line - phrases will be split at " "
	 */
	public static final String PARAM_MODEL_LOCATION = ComponentParameters.PARAM_MODEL_LOCATION;
	@ConfigurationParameter(name = PARAM_MODEL_LOCATION, mandatory = true)
	private String phraseFile;

	/**
	 * The character encoding used by the model.
	 */
	public static final String PARAM_MODEL_ENCODING = ComponentParameters.PARAM_MODEL_ENCODING;
	@ConfigurationParameter(name = PARAM_MODEL_ENCODING, mandatory = true, defaultValue = "UTF-8")
	private String modelEncoding;

	private PhraseTree phrases;

	private HashMap<String, List<String>> lexiconEntries = new HashMap<String, List<String>>();

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);

		phrases = new PhraseTree();

		InputStream is = null;
		String wordFromInput = new String();
		String category = new String();
		Double value = 0.0;
		String pos = new String();

		// Read the lexicon.
		try {
			URL phraseFileUrl = ResourceUtils.resolveLocation(phraseFile, aContext);
			is = phraseFileUrl.openStream();
			for (String inputLine : IOUtils.readLines(is, modelEncoding)) {
				List<String> catValuePosList = new ArrayList<String>();

				// Ignore lines starting with "%%" (comments).
				if (!(inputLine.startsWith("%%"))) {

					// Ignore lines without the correct form.
					// Correct form example: fehlschlagen NEG=0.7 verben
					if (inputLine.matches("[\\w+[-äöüÄÖÜß]*\\w*]+\\s\\w\\w\\w=\\d.?\\d?\\s\\w+")) {

						// The relevant part for phrases is part 0, the word itself.
						wordFromInput = inputLine.substring(0, inputLine.indexOf(" "));
						String[] phraseSplit = wordFromInput.split(" ");
						phrases.addPhrase(phraseSplit);
						System.out.println("word: " + wordFromInput);

						category = inputLine.substring(inputLine.indexOf(" ") + 1, inputLine.indexOf("="));
						System.out.println("category: " + category);

						// TODO make this work for "0.7" as well as "0,7".
						// Locale original = Locale.getDefault();
						// Locale.setDefault(new Locale("de", "DE"));
						Scanner doubleScanner = new Scanner(inputLine.substring(inputLine.indexOf("=") + 1).replace('.', ','));
						if (doubleScanner.hasNextDouble()) {
							value = doubleScanner.nextDouble();
							System.out.println("valueToSetFeatureTo: " + value);
						} else {
							System.out.println("no valueToSetFeatureTo has been found for: " + wordFromInput);
						}
						doubleScanner.close();
						// Locale.setDefault(original);

						pos = inputLine.substring(inputLine.lastIndexOf(" ") + 1, inputLine.length());
						System.out.println("pos: " + pos);

						if (category != null && value != null && pos != null) {
							catValuePosList.add(category);
							catValuePosList.add(value.toString());
							catValuePosList.add(pos);

							lexiconEntries.put(wordFromInput, catValuePosList);
						} else {
							System.err.println("Lexicon entry for " + wordFromInput + " is incomplete!");
						}

					} else {
						System.err.println("Line with wrong format in Lexicon, will be ignored: ");
						System.err.println("\"" + inputLine + "\"");
					}
				}
			}
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		for (Sentence currSentence : select(jcas, Sentence.class)) {
			ArrayList<Token> tokens = new ArrayList<Token>(selectCovered(Token.class, currSentence));
			// System.out.println(currSentence);
			// System.out.println(tokens);

			for (int i = 0; i < tokens.size(); i++) {
				List<Token> tokensToSentenceEnd = tokens.subList(i, tokens.size() - 1);
				String[] sentenceToEnd = new String[tokens.size()];

				for (int j = 0; j < tokensToSentenceEnd.size(); j++) {
					sentenceToEnd[j] = tokensToSentenceEnd.get(j).getCoveredText();
				}

				String[] longestMatch = phrases.getLongestMatch(sentenceToEnd);

				if (longestMatch != null) {
					System.out.println("match: " + longestMatch[0]);

					Token beginToken = tokens.get(i);
					Token endToken = tokens.get(i + longestMatch.length - 1);

					String cat = lexiconEntries.get(longestMatch[0]).get(0);
					String value = lexiconEntries.get(longestMatch[0]).get(1);
					String pos = lexiconEntries.get(longestMatch[0]).get(2);

					if (!cat.equals("SHI")) {
						Type type = getType(jcas.getCas(), PolarExpression.class.getName());

						Feature featurePos = type.getFeatureByBaseName("pos");
						Feature featureValue = type.getFeatureByBaseName("value");
						Feature featureCategory = type.getFeatureByBaseName("category");

						AnnotationFS newFound = jcas.getCas().createAnnotation(type, beginToken.getBegin(), endToken.getEnd());

						newFound.setFeatureValueFromString(featureCategory, cat);
						newFound.setFeatureValueFromString(featureValue, value);
						newFound.setFeatureValueFromString(featurePos, pos);

						jcas.getCas().addFsToIndexes(newFound);
					} else {
						// Case of shifter in the sentiment lex --> ignore those lines because
						// shifters are read in from seperate file.
						/*
						Type type = getType(jcas.getCas(), Shifter.class.getName());

						AnnotationFS newFound = jcas.getCas().createAnnotation(type, beginToken.getBegin(), endToken.getEnd());
						jcas.getCas().addFsToIndexes(newFound);
						*/
					}
				}
			}
		}
	}
}
