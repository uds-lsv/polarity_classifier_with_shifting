package bachelor.polarity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import bachelor.polarity.soPro.Module;
import bachelor.polarity.soPro.SalsaAPIConnective;
import bachelor.polarity.soPro.SentenceList;
import bachelor.polarity.soPro.SentimentChecker;
import bachelor.polarity.soPro.SentimentLex;
import bachelor.polarity.soPro.ShifterLex;
import bachelor.polarity.soPro.SubjectiveExpressionModule;

public class Main {

	public static void main(String[] args) {
		// Read config
		Properties prop = new Properties();
		java.nio.file.Path configPath = java.nio.file.Paths.get("config", "config.properties");
		System.out.println("Reading config from " + configPath.toString());
		try {
			InputStream configInput = new FileInputStream(configPath.toString());
			prop.load(configInput);

			String text_input = prop.getProperty("TEXT_INPUT");
			String dependency_input = prop.getProperty("DEPENDENCY_INPUT");
			String constituency_input = prop.getProperty("CONSTITUENCY_INPUT");
			String sentiment_lexicon_input = prop.getProperty("SENTIMENT_LEXICON_INPUT");
			String shifter_lexicon_input = prop.getProperty("SHIFTER_LEXICON_INPUT");
			Boolean normalize = Boolean.valueOf(prop.getProperty("NORMALIZE"));

			// Read in raw input text and create SentenceList based on it.
			System.out.println("Reading rawText from : " + text_input);
			SentenceList sentences = new SentenceList();
			sentences.rawToSentenceList(text_input);

			// Read in dependency parse file and create a DependencyGraph object for
			// each sentence.
			System.out.println("Reading dependency data from " + dependency_input + "...");
			System.out.println("Creating dependency graph...");
			sentences.readDependencyParse(dependency_input);

			// Normalize DependencyGraph objects.
			if (normalize) {
				System.out.println("Normalizing dependency graph...");
				sentences.normalizeDependencyGraphs();
			} else {
				System.out.println("Normalizing of dependency graph set to FALSE.");
			}

			// Read in Salsa / Tiger XML file and create a ConstituencyTree object for
			// every sentence.
			System.out.println("Reading constituency data from " + constituency_input + "...");
			System.out.println("Creating constituency tree...");
			SalsaAPIConnective salsa = new SalsaAPIConnective(constituency_input, sentences);

			// Read in sentiment lexicon.
			System.out.println("Reading sentiment lexicon from " + sentiment_lexicon_input + "...");
			SentimentLex sentimentLex = new SentimentLex(true);
			sentimentLex.fileToLex(sentiment_lexicon_input);

			// Read in shifter lexicon.
			System.out.println("Reading shifter lexicon from " + shifter_lexicon_input + "...");
			ShifterLex shifterLex = new ShifterLex(true);
			shifterLex.fileToLex(shifter_lexicon_input);

			// Look for subjective expressions and shifters using the according
			// modules.
			final Set<Module> modules = new HashSet<Module>();

			final SubjectiveExpressionModule subjectiveExpressionModule;

			subjectiveExpressionModule = new SubjectiveExpressionModule(sentimentLex, shifterLex);

			modules.add(subjectiveExpressionModule);

			// search for sentiment expressions and write results to the output file
			// specified in the configuration file
			final SentimentChecker sentcheck = new SentimentChecker(salsa, sentences, modules);
			System.out.println("Looking for sentiment expressions...");
			String outputPath = "output/salsaResult.xml";

			sentcheck.findSentiments(outputPath);
		} catch (FileNotFoundException e) {
			System.out.println("No config found at this config path: " + configPath);
			return;
		} catch (IOException e) {
			System.out.println("Could not read config file from " + configPath);
			return;
		}

	}
}
