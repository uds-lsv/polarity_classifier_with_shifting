package bachelor.polarity.soPro;

import java.util.Arrays;

/**
 * ShifterUnit object contains the informations of one shifter expression
 * 
 * Example: Verbesserung g [gmod, objp-*] nomen 
 * name = Verbesserung 
 * shifter_type
 * = g 
 * shifter_scope = [gmod, objp-*] 
 * shifter_pos = nomen 
 * mwe = false
 * collocations = []
 *
 */
public class ShifterUnit {
	String name;
	String shifter_type;
	String[] shifter_scope;
	String shifter_pos;
	Boolean mwe;
	String[] collocations;

	/**
	 * 
	 * @param name
	 * @param shifter_type
	 * @param shifter_scope
	 * @param shifter_pos
	 * @param mwe
	 */
	public ShifterUnit(String name, String shifter_type, String[] shifter_scope, String shifter_pos, Boolean mwe) {
		if (mwe) {
			String[] parts = name.split("_");
			this.name = parts[parts.length - 1];
			this.collocations = Arrays.copyOfRange(parts, 0, parts.length - 1);
		} else {
			this.name = name;
			this.collocations = new String[0];
		}
		this.shifter_type = shifter_type;
		this.shifter_scope = shifter_scope;
		this.shifter_pos = shifter_pos;
		this.mwe = mwe;
	}

	/**
	 * @param word
	 * @return true if the {@link #name} of the ShifterUnit equals the lemma of a
	 *         given Word Object
	 */
	public boolean equals(WordObj word) {
		if (this.name.equals(word.getLemma())) {
			return true;
		}
		return false;
	}

	/**
	 * @return {@link #shifter_type} of the ShifterUnit
	 */
	public String getTyp() {
		return this.shifter_type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */

	public String toString() {
		StringBuffer printer = new StringBuffer();
		printer.append(this.name);
		for (String col : this.collocations) {
			printer.append("_" + col);
		}
		printer.append(" " + this.shifter_type);
		printer.append(" " + this.shifter_scope.toString());
		printer.append(" " + this.shifter_pos);
		return printer.toString();
	}
}
