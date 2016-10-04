package bachelor.polarity;

import java.util.HashSet;
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
		// *************************SoPro Classes*****************************
		// Read in raw input text and create SentenceList based on it.
//		java.nio.file.Path fileRaw = java.nio.file.Paths.get("src", "main", "resources", "textInput",
//				"steps2016-testdaten.raw.txt");
		java.nio.file.Path fileRaw = java.nio.file.Paths.get("src", "main", "resources", "textInput",
				"raw_text.txt");
		System.out.println("Reading rawText from : " + fileRaw.toString());
		SentenceList sentences = new SentenceList();
		sentences.rawToSentenceList(fileRaw.toString());

		// Read in dependency parse file and create a DependencyGraph object for
		// each sentence.
//		java.nio.file.Path dependencyFile = java.nio.file.Paths.get("src", "main", "resources", "textInput",
//				"steps2016-testdaten.parzu.txt");
		java.nio.file.Path dependencyFile = java.nio.file.Paths.get("src", "main", "resources", "textInput",
				"dependency_parse.txt");
		System.out.println("Reading dependency data from " + dependencyFile.toString() + "...");
		System.out.println("Creating dependency graph...");
		sentences.readDependencyParse(dependencyFile.toString());

		// Normalize DependencyGraph objects.
		System.out.println("Normalizing dependency graph...");
		sentences.normalizeDependencyGraphs();

		// Read in Salsa / Tiger XML file and create a ConstituencyTree object for
		// every sentence.
//		java.nio.file.Path constituencyFile = java.nio.file.Paths.get("src", "main", "resources", "textInput",
//				"steps2016-testdaten.UTF8.keineAnnotation(constituency).xml");
		java.nio.file.Path constituencyFile = java.nio.file.Paths.get("src", "main", "resources", "textInput",
				"constituency_parse.xml");
		System.out.println("Reading constituency data from " + constituencyFile.toString() + "...");
		System.out.println("Creating constituency tree...");
		SalsaAPIConnective salsa = new SalsaAPIConnective(constituencyFile.toString(), sentences);

		// Read in sentiment lexicon.
		java.nio.file.Path fileLex = java.nio.file.Paths.get("src", "main", "resources", "dictionaries", "germanlex.txt");
		System.out.println("Reading sentiment lexicon from " + fileLex.toString() + "...");
		SentimentLex sentimentLex = new SentimentLex(true);
		sentimentLex.fileToLex(fileLex.toString());

		// Read in shifter lexicon.
		java.nio.file.Path shifterLexFile = java.nio.file.Paths.get("src", "main", "resources", "dictionaries",
				"shifter_lex_german.txt");
		System.out.println("Reading shifter lexicon from " + shifterLexFile.toString() + "...");
		ShifterLex shifterLex = new ShifterLex(true);
		shifterLex.fileToLex(shifterLexFile.toString());

		// Look for subjective expressions and shifters using the according modules.
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
	}
}
