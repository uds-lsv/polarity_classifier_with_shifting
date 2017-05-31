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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * IntensifierLex object contains all informations from a given intensifier
 * lexicon
 *
 */
public class IntensifierLex {

  private final static Logger log = Logger.getLogger(IntensifierLex.class.getName());

  List<IntensifierUnit> intensifierList = new ArrayList<IntensifierUnit>();
  Map<String, IntensifierUnit> intensifierMap = new HashMap<String, IntensifierUnit>();
  boolean flexibleMWEs = false;
  String[] collectSubjectiveExpressions = {""};

  /**
   * Constructs a new IntensifierLex
   *
   * @param flexMWE indicates if the multi word expressions in the lexicon
   * should be interpreted in a flexible way (more info see
   * {@link #mweFlexibility(IntensifierUnit)}).
   */
  public IntensifierLex(boolean flexMWE) {
    log.setLevel(Level.FINE);
    this.flexibleMWEs = flexMWE;
  }

  /**
   * Returns the IntensifierUnit corresponding to the given name or null.
   *
   * @param name The name of the intensifier to look for.
   * @return IntensifierUnit for a given name or null if no IntensifierUnit with
   * the given name exists
   */
  public IntensifierUnit getIntensifier(String name) {
    for (IntensifierUnit intensifierList1 : intensifierList) {
      IntensifierUnit tmp = (IntensifierUnit) intensifierList1;
      if (tmp.name.equals(name)) {
        return tmp;
      }
    }
    return null;
  }

  /**
   * Returns the IntensifierUnits (more than one if there is more than one
   * lexicon entry) corresponding to the given name or null. Up to 50 entries
   * possible for one intensifier.
   *
   * @param name The name of the intensifier to look for.
   * @return IntensifierUnit for a given name or null if no IntensifierUnit with
   * the given name exists
   */
  public ArrayList<IntensifierUnit> getAllIntensifiers(String name) {
    ArrayList<IntensifierUnit> intensifiers = new ArrayList<IntensifierUnit>();
    for (IntensifierUnit intensifierList1 : intensifierList) {
      IntensifierUnit tmp = (IntensifierUnit) intensifierList1;
      if (tmp.name.equals(name)) {
        intensifiers.add(tmp);
      }
    }
    if (!intensifiers.isEmpty()) {
      return intensifiers;
    } else {
      return null;
    }
  }

  /**
   * Adds an intensifier expression to the internal lexicon.
   *
   * @param intensifier is added to Intensifier
   */
  public void addIntensifier(IntensifierUnit intensifier) {
    intensifierList.add(intensifier);
    addToMap(intensifier.name, intensifier);
  }

  /**
   * @param intensifier is removed from Intensifier
   */
  public void removeIntensifier(IntensifierUnit intensifier) {
    intensifierList.remove(intensifier);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    String printer = "Name[Category][Value][Pos]";
    for (IntensifierUnit intensifierList1 : intensifierList) {
      printer = printer + "\n";
      printer = printer + intensifierList1;
    }
    return printer;
  }

  /**
   * Reads in intensifier lexicon from filename
   *
   * @param filename
   */
  public void fileToLex(String filename) {
    String wordFromInput = new String();
    String category = new String();
    Double value = 0.0;
    String pos = new String();
//    Boolean mwe = Boolean.FALSE;
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

            category = inputLine.substring(inputLine.indexOf(" ") + 1, inputLine.indexOf("="));

            Locale original = Locale.getDefault();
            Locale.setDefault(new Locale("de", "DE"));
            try (Scanner doubleScanner = new Scanner(inputLine.substring(inputLine.indexOf("=") + 1).replace('.', ','))) {
              if (doubleScanner.hasNextDouble()) {
                value = doubleScanner.nextDouble();
                // System.out.println("valueToSetFeatureTo: " + value);
              } else {
                //System.out.println("no valueToSetFeatureTo has been found for: " + wordFromInput);
                log.log(Level.FINE, "no valueToSetFeatureTo has been found for: {0}", wordFromInput);
              }
            } finally {
              Locale.setDefault(original);
            }

            pos = inputLine.substring(inputLine.lastIndexOf(" ") + 1, inputLine.length());
            // System.out.println("pos: " + pos);

//            mwe = wordFromInput.contains("_");

            if (category != null && pos != null) {
              // Ignore INT (intensifier) and Shifter
              if (category.equals("INT")) {
                IntensifierUnit unit = new IntensifierUnit(wordFromInput, category, value.toString(), pos);
                this.addIntensifier(unit);
              }
            } else {
              System.err.println("Intensifier-Lexicon entry for " + wordFromInput + " is incomplete!");
              log.log(Level.WARNING, "Intensifier-Lexicon entry for {0} is incomplete!", wordFromInput);
            }

          } else {
            System.err.println("Line with wrong format in Intensifier-Lexicon, will be ignored: ");
            System.err.println("\"" + inputLine + "\"");
            log.log(Level.WARNING, "Line with wrong format in Intensifier-Lexicon, will be ignored:\n\"{0}\"", inputLine);
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Builds a {@link #intensifierMap} from a given {@link #intensifierList}
   *
   * @param key
   * @param value
   */
  public void addToMap(String key, IntensifierUnit value) {
    if (this.intensifierMap.containsKey(key)) {
      key = key + "+";
      addToMap(key, value);
    } else {
      this.intensifierMap.put(key, value);
    }
  }
}
