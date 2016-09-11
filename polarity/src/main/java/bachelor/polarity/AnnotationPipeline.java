/*******************************************************************************
 * Copyright 2016
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package bachelor.polarity;

import de.tudarmstadt.ukp.dkpro.core.io.text.TextReader;
import de.tudarmstadt.ukp.dkpro.core.testing.dumper.CasDumpWriter;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.pipeline.SimplePipeline;

import bachelor.polarity.types.PolarExpression;

import java.io.IOException;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;

/**
 * This pipeline uses the custom annotation type {@link Name} to annotate names in the input text.
 * The {@link DictionaryAnnotator} looks up names from a static names file and annotates them in the
 * documents.
 * <p>
 * The output is written to the target directory. Use {@code NameAnnotationPipelineTest} in the test directory to check
 * the output.
 * </p>
 */
public class AnnotationPipeline
{
    public static void main(String[] args)
            throws IOException, UIMAException
    {
    	//Text reader. Reads text input.
        CollectionReaderDescription reader = createReaderDescription(
                TextReader.class,
                TextReader.PARAM_SOURCE_LOCATION, "src/main/resources/textInput",
                TextReader.PARAM_PATTERNS, "[+]*.txt");

        //Tokenizer
        AnalysisEngineDescription tokenizer = createEngineDescription(
                BreakIteratorSegmenter.class);

        
        //PolarExpression annotator
        AnalysisEngineDescription polarExpressionFinder = createEngineDescription(
            SentimentLexiconAnnotator.class,
            SentimentLexiconAnnotator.PARAM_MODEL_LOCATION,
            "src/main/resources/dictionaries/germanlex.txt");
        
      //Shifter annotator
        AnalysisEngineDescription shifterFinder = createEngineDescription(
            ShifterLexiconAnnotator.class,
            ShifterLexiconAnnotator.PARAM_MODEL_LOCATION,
            "src/main/resources/dictionaries/shifter_lex_german.txt");
        
        /*
        AnalysisEngineDescription writer = createEngineDescription(
                CasDumpWriter.class,
                CasDumpWriter.PARAM_TARGET_LOCATION, "target/PolarAnnotationPipeline.txt");
                */
        
        AnalysisEngineDescription xmiWriter = createEngineDescription(
        		XmiWriter.class, XmiWriter.PARAM_OUTPUT_DIRECTORY, "target");

        SimplePipeline.runPipeline(reader, tokenizer, polarExpressionFinder, shifterFinder, xmiWriter);
    }
}
