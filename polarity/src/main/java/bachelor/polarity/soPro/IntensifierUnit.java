package bachelor.polarity.soPro;

/**
 * IntensifierUnit object contains the informations of one intensifier
 *
 * Example: wirklich INT=2 adj name = froh category = POS value = 2 pos = adj
 * mwe = true collocations = [gestimmt]
 *
 */
public class IntensifierUnit {

  String name;
  String category;
  String value;
  String pos;

  /**
   *
   * @param name
   * @param category
   * @param value
   * @param pos
   */
  public IntensifierUnit(String name, String category, String value, String pos) {
    this.name = name;

    this.category = category;

    this.pos = pos;

    this.value = value;
  }

  /**
   * Constructs a new SentimentUnit
   *
   * @param name {@link #name} is set
   */
  public IntensifierUnit(String name) {
    this.name = name;

  }

  /**
   * @param word
   * @return true if the {@link #name} of the SentimentUnit equals the lemma of
   * a given Word Object
   */
  public boolean equals(WordObj word) {
    if (this.name.equals(word.getLemma())) {
      return true;
    }
    return false;
  }

  /**
   * @return {@link #category} of the SentimentUnit
   */
  public String getTyp() {
    return this.category;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    StringBuilder printer = new StringBuilder();
    printer.append(this.name);
    printer.append(this.category);
    printer.append("=");
    printer.append(this.value);
    printer.append(" ");
    printer.append(this.pos);
    return printer.toString();
  }
}
