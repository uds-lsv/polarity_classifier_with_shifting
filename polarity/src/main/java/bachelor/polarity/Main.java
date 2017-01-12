package bachelor.polarity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import bachelor.polarity.soPro.BaselineModule;
import bachelor.polarity.soPro.BaselineRuleModule;
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
			String preset_se_input = prop.getProperty("PRESET_SE_INPUT");
			Boolean use_preset_se_input = Boolean.valueOf(prop.getProperty("USE_PRESET_SE_INPUT"));
			Boolean normalize = Boolean.valueOf(prop.getProperty("NORMALIZE"));
			Boolean baseline_module = Boolean.valueOf(prop.getProperty("BASELINE_MODULE"));
			Boolean baseline_rule_module = Boolean.valueOf(prop.getProperty("BASELINE_RULE_MODULE"));
			Boolean pos_lookup_sentiment = Boolean.valueOf(prop.getProperty("POS_LOOKUP_SENTIMENT"));
			Boolean pos_lookup_shifter = Boolean.valueOf(prop.getProperty("POS_LOOKUP_SHIFTER"));
			Boolean shifter_orientation_check = Boolean.valueOf(prop.getProperty("SHIFTER_ORIENTATION_CHECK"));
			int baseline_window = Integer.valueOf(prop.getProperty("BASELINE_WINDOW"));
			if (baseline_window < 1) {
				System.err.println("n must be bigger than 0 for the baseline module to work!");
				System.err.println("Entered number: n=" + baseline_window);
				return;
			}
			String output = prop.getProperty("OUTPUT");

			// If no other module is turned on, use the standard
			// subjective_expression_module.
			Boolean subjective_expression_module = (baseline_module.equals(false)
					&& baseline_rule_module.equals(false));
			
			// Only one module should be turned on at the same time.
			if (baseline_module && baseline_rule_module){
				System.err.println("WARNING: Both Baseline Modules are turned on!");
				System.err.println("Check the config file!");
			}

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

			// Read in preset se file
			Boolean got_preset_se_file = false;
			if (!preset_se_input.isEmpty()) {
				if (use_preset_se_input) {
					System.out.println("Reader preset se file from " + preset_se_input + "...");
					SalsaAPIConnective salsa_preset = new SalsaAPIConnective(preset_se_input, sentences);
					got_preset_se_file = true;
					salsa = salsa_preset;
				} else {
					System.err.println("A preset se file path has been specified, but the option to use it is turned off.");
					System.err.println("See USE_PRESET_SE_INPUT in the configuration file.");
				}
			} else if (use_preset_se_input) {
				System.err.println("USE_PRESET_SE_INPUT is set to TRUE but no preset-se-file-path was specified!");
				return;
			}

			// Look for subjective expressions and shifters using the according
			// modules.
			final Set<Module> modules = new HashSet<Module>();

			// Standard Module
			if (subjective_expression_module && got_preset_se_file && use_preset_se_input) {
				final SubjectiveExpressionModule subjectiveExpressionModule;
				subjectiveExpressionModule = new SubjectiveExpressionModule(salsa, sentimentLex, shifterLex,
						pos_lookup_sentiment, pos_lookup_shifter, shifter_orientation_check);
				modules.add(subjectiveExpressionModule);
			} else if (subjective_expression_module) {
				final SubjectiveExpressionModule subjectiveExpressionModule;
				subjectiveExpressionModule = new SubjectiveExpressionModule(sentimentLex, shifterLex, pos_lookup_sentiment,
						pos_lookup_shifter, shifter_orientation_check);
				modules.add(subjectiveExpressionModule);
			} else {
				System.err.println("Warning! Subjective Expression Module turned off!");
			}

			// Baseline Module
			if (baseline_module && got_preset_se_file && use_preset_se_input) {
				final BaselineModule baselineModule;
				baselineModule = new BaselineModule(salsa, sentimentLex, shifterLex, baseline_window, pos_lookup_sentiment,
						pos_lookup_shifter, shifter_orientation_check);
				modules.add(baselineModule);
			} else if (baseline_module) {
				final BaselineModule baselineModule;
				baselineModule = new BaselineModule(sentimentLex, shifterLex, baseline_window, pos_lookup_sentiment,
						pos_lookup_shifter, shifter_orientation_check);
				modules.add(baselineModule);
			}

			// Baseline Rule Module
			if (baseline_rule_module && got_preset_se_file && use_preset_se_input) {
				final BaselineRuleModule baselineRuleModule;
				baselineRuleModule = new BaselineRuleModule(salsa, sentimentLex, shifterLex, baseline_window, pos_lookup_sentiment,
						pos_lookup_shifter, shifter_orientation_check);
				modules.add(baselineRuleModule);
			} else if (baseline_rule_module) {
				final BaselineRuleModule baselineRuleModule;
				baselineRuleModule = new BaselineRuleModule(sentimentLex, shifterLex, baseline_window, pos_lookup_sentiment,
						pos_lookup_shifter, shifter_orientation_check);
				modules.add(baselineRuleModule);
			}
			
			// search for sentiment expressions and write results to the output file
			// specified in the configuration file
			final SentimentChecker sentcheck = new SentimentChecker(salsa, sentences, modules);
			System.out.println("Looking for sentiment expressions...");

			sentcheck.findSentiments(output);
		} catch (FileNotFoundException e) {
			System.out.println("No config found at this config path: " + configPath);
			return;
		} catch (IOException e) {
			System.out.println("Could not read config file from " + configPath);
			return;
		}

	}
}
