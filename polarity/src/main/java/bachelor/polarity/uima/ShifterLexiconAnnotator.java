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
package bachelor.polarity.uima;

import static org.apache.uima.fit.util.CasUtil.getType;
import static org.apache.uima.fit.util.JCasUtil.select;
import static org.apache.uima.fit.util.JCasUtil.selectCovered;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
public class ShifterLexiconAnnotator extends JCasAnnotator_ImplBase {
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
		String shifterStr = new String();
		String shifter_type = new String();
		String shifter_scope = new String();
		String shifter_pos = new String();
		String mwe = "false";

		// Read the lexicon.
		try {
			URL phraseFileUrl = ResourceUtils.resolveLocation(phraseFile, aContext);
			is = phraseFileUrl.openStream();
			for (String inputLine : IOUtils.readLines(is, modelEncoding)) {
				List<String> typeScopePosMweList = new ArrayList<String>();

				// Ignore lines starting with "%%" (comments).
				if (!(inputLine.startsWith("%%"))) {

					// Ignore lines without the correct form.
					// Correct form example: Fehlschlag p [subj] nomen
					if (inputLine.matches("[\\w+[-_äöüÄÖÜß]*\\w*]+\\s\\w\\s\\[[\\w+[-\\*,\"]*\\s*]+\\]\\s\\w+")) {

						// The relevant part for phrases is part 0, the word itself.
						shifterStr = inputLine.substring(0, inputLine.indexOf(" "));
						String[] phraseSplit = shifterStr.split(" ");
						phrases.addPhrase(phraseSplit);
//						System.out.println("shifter: " + shifterStr);

						shifter_type = inputLine.substring(inputLine.indexOf(" ") + 1, inputLine.indexOf(" ") + 2);
//						System.out.println("shifter_type: " + shifter_type);

						shifter_scope = inputLine.substring(inputLine.lastIndexOf("[") + 1, inputLine.lastIndexOf("]"));
//						System.out.println("shifter_scope: " + shifter_scope);

						shifter_pos = inputLine.substring(inputLine.lastIndexOf(" ") + 1, inputLine.length());
//						System.out.println("shifter_pos: " + shifter_pos);

						mwe = String.valueOf(shifterStr.contains("_"));

						if (shifter_type != null && shifter_scope != null && shifter_pos != null) {
							typeScopePosMweList.add(shifter_type);
							typeScopePosMweList.add(shifter_scope.toString());
							typeScopePosMweList.add(shifter_pos);
							typeScopePosMweList.add(mwe);

							lexiconEntries.put(shifterStr, typeScopePosMweList);
						} else {
							System.err.println("Shifter-Lexicon entry for " + shifterStr + " is incomplete!");
						}

					} else {
						System.err.println("Line with wrong format in Shifter-Lexicon, will be ignored: ");
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
//					System.out.println("match: " + longestMatch[0]);

					Token beginToken = tokens.get(i);
					Token endToken = tokens.get(i + longestMatch.length - 1);

					String shifter_type = lexiconEntries.get(longestMatch[0]).get(0);
					String shifter_scope = lexiconEntries.get(longestMatch[0]).get(1);
					String shifter_pos = lexiconEntries.get(longestMatch[0]).get(2);
					String shifter_mwe = lexiconEntries.get(longestMatch[0]).get(3);

					Type type = getType(jcas.getCas(), Shifter.class.getName());
					
					Feature featureType = type.getFeatureByBaseName("shifter_type");
					Feature featureScope = type.getFeatureByBaseName("shifter_scope");
					Feature featurePos = type.getFeatureByBaseName("shifter_pos");
					Feature featureMwe = type.getFeatureByBaseName("mwe");

					AnnotationFS newFound = jcas.getCas().createAnnotation(type, beginToken.getBegin(), endToken.getEnd());
					
					newFound.setFeatureValueFromString(featureType, shifter_type);
					newFound.setFeatureValueFromString(featureScope, shifter_scope);
					newFound.setFeatureValueFromString(featurePos, shifter_pos);
					newFound.setFeatureValueFromString(featureMwe, shifter_mwe);
					
					jcas.getCas().addFsToIndexes(newFound);

				}
			}
		}
	}
}
