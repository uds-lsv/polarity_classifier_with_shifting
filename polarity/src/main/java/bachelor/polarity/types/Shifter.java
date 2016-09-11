

/* First created by JCasGen Tue Aug 30 19:21:27 CEST 2016 */
package bachelor.polarity.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.StringList;
import org.apache.uima.jcas.tcas.Annotation;


/** A shifter "shifts" sentiments.
Examples:
"kein, weniger, nicht, ..."
 * Updated by JCasGen Sun Sep 11 15:00:25 CEST 2016
 * XML source: C:/Users/m_s_w_000/Desktop/BachelorSystem/bachelor_sentiment_german/polarity/src/main/resources/desc/type/typeSystemDescriptor.xml
 * @generated */
public class Shifter extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Shifter.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Shifter() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Shifter(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Shifter(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Shifter(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
  //*--------------*
  //* Feature: shifter_type

  /** getter for shifter_type - gets The shifter type.
There are three different types:
g -- general shifter
p -- positive shifter
n -- negative shifter 
Examples:
beenden [g]
vermasseln [p]
leugnen [n]
   * @generated
   * @return value of the feature 
   */
  public String getShifter_type() {
    if (Shifter_Type.featOkTst && ((Shifter_Type)jcasType).casFeat_shifter_type == null)
      jcasType.jcas.throwFeatMissing("shifter_type", "bachelor.polarity.types.Shifter");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Shifter_Type)jcasType).casFeatCode_shifter_type);}
    
  /** setter for shifter_type - sets The shifter type.
There are three different types:
g -- general shifter
p -- positive shifter
n -- negative shifter 
Examples:
beenden [g]
vermasseln [p]
leugnen [n] 
   * @generated
   * @param v value to set into the feature 
   */
  public void setShifter_type(String v) {
    if (Shifter_Type.featOkTst && ((Shifter_Type)jcasType).casFeat_shifter_type == null)
      jcasType.jcas.throwFeatMissing("shifter_type", "bachelor.polarity.types.Shifter");
    jcasType.ll_cas.ll_setStringValue(addr, ((Shifter_Type)jcasType).casFeatCode_shifter_type, v);}    
   
    
  //*--------------*
  //* Feature: shifter_scope

  /** getter for shifter_scope - gets Specifies the scope of the shifter, determined by its pos tag.
The scope is listed as dependency relations.
Examples:
<shifterVerb> [<type>][<scope>] <pos>
beenden [g][objd,obja,objc,obji,s,obj] verben
korrigierbar [g][subj] adj
kein [g][attr-rev] adj
   * @generated
   * @return value of the feature 
   */
  public String getShifter_scope() {
    if (Shifter_Type.featOkTst && ((Shifter_Type)jcasType).casFeat_shifter_scope == null)
      jcasType.jcas.throwFeatMissing("shifter_scope", "bachelor.polarity.types.Shifter");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Shifter_Type)jcasType).casFeatCode_shifter_scope);}
    
  /** setter for shifter_scope - sets Specifies the scope of the shifter, determined by its pos tag.
The scope is listed as dependency relations.
Examples:
<shifterVerb> [<type>][<scope>] <pos>
beenden [g][objd,obja,objc,obji,s,obj] verben
korrigierbar [g][subj] adj
kein [g][attr-rev] adj 
   * @generated
   * @param v value to set into the feature 
   */
  public void setShifter_scope(String v) {
    if (Shifter_Type.featOkTst && ((Shifter_Type)jcasType).casFeat_shifter_scope == null)
      jcasType.jcas.throwFeatMissing("shifter_scope", "bachelor.polarity.types.Shifter");
    jcasType.ll_cas.ll_setStringValue(addr, ((Shifter_Type)jcasType).casFeatCode_shifter_scope, v);}    
   
    
  //*--------------*
  //* Feature: shifter_pos

  /** getter for shifter_pos - gets The pos tag of the shifter.
   * @generated
   * @return value of the feature 
   */
  public String getShifter_pos() {
    if (Shifter_Type.featOkTst && ((Shifter_Type)jcasType).casFeat_shifter_pos == null)
      jcasType.jcas.throwFeatMissing("shifter_pos", "bachelor.polarity.types.Shifter");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Shifter_Type)jcasType).casFeatCode_shifter_pos);}
    
  /** setter for shifter_pos - sets The pos tag of the shifter. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setShifter_pos(String v) {
    if (Shifter_Type.featOkTst && ((Shifter_Type)jcasType).casFeat_shifter_pos == null)
      jcasType.jcas.throwFeatMissing("shifter_pos", "bachelor.polarity.types.Shifter");
    jcasType.ll_cas.ll_setStringValue(addr, ((Shifter_Type)jcasType).casFeatCode_shifter_pos, v);}    
   
    
  //*--------------*
  //* Feature: mwe

  /** getter for mwe - gets True if the shifter is a multi word expression (mwe), false otherwise.
   * @generated
   * @return value of the feature 
   */
  public boolean getMwe() {
    if (Shifter_Type.featOkTst && ((Shifter_Type)jcasType).casFeat_mwe == null)
      jcasType.jcas.throwFeatMissing("mwe", "bachelor.polarity.types.Shifter");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((Shifter_Type)jcasType).casFeatCode_mwe);}
    
  /** setter for mwe - sets True if the shifter is a multi word expression (mwe), false otherwise. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setMwe(boolean v) {
    if (Shifter_Type.featOkTst && ((Shifter_Type)jcasType).casFeat_mwe == null)
      jcasType.jcas.throwFeatMissing("mwe", "bachelor.polarity.types.Shifter");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((Shifter_Type)jcasType).casFeatCode_mwe, v);}    
  }

    