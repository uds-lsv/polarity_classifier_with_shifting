

/* First created by JCasGen Sun Jul 24 15:18:54 CEST 2016 */
package bachelor.polarity.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** A polar expression.
E.g. "moegen, hassen, gut, schlecht, ..."
 * Updated by JCasGen Sun Sep 11 15:00:25 CEST 2016
 * XML pos: C:/Users/m_s_w_000/Desktop/BachelorSystem/bachelor_sentiment_german/polarity/src/main/resources/desc/type/typeSystemDescriptor.xml
 * @generated */
public class PolarExpression extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(PolarExpression.class);
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
  protected PolarExpression() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public PolarExpression(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public PolarExpression(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public PolarExpression(JCas jcas, int begin, int end) {
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
  //* Feature: value

  /** getter for value - gets The numeric value of the polar expression.
Positive: 1.0
Neutral: 0
Negative: -1.0
   * @generated
   * @return value of the feature 
   */
  public double getValue() {
    if (PolarExpression_Type.featOkTst && ((PolarExpression_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "bachelor.polarity.types.PolarExpression");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((PolarExpression_Type)jcasType).casFeatCode_value);}
    
  /** setter for value - sets The numeric value of the polar expression.
Positive: 1.0
Neutral: 0
Negative: -1.0 
   * @generated
   * @param v value to set into the feature 
   */
  public void setValue(double v) {
    if (PolarExpression_Type.featOkTst && ((PolarExpression_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "bachelor.polarity.types.PolarExpression");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((PolarExpression_Type)jcasType).casFeatCode_value, v);}    
   
    
  //*--------------*
  //* Feature: pos

  /** getter for pos - gets Part of speech tag.
   * @generated
   * @return value of the feature 
   */
  public String getPos() {
    if (PolarExpression_Type.featOkTst && ((PolarExpression_Type)jcasType).casFeat_pos == null)
      jcasType.jcas.throwFeatMissing("pos", "bachelor.polarity.types.PolarExpression");
    return jcasType.ll_cas.ll_getStringValue(addr, ((PolarExpression_Type)jcasType).casFeatCode_pos);}
    
  /** setter for pos - sets Part of speech tag. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setPos(String v) {
    if (PolarExpression_Type.featOkTst && ((PolarExpression_Type)jcasType).casFeat_pos == null)
      jcasType.jcas.throwFeatMissing("pos", "bachelor.polarity.types.PolarExpression");
    jcasType.ll_cas.ll_setStringValue(addr, ((PolarExpression_Type)jcasType).casFeatCode_pos, v);}    
   
    
  //*--------------*
  //* Feature: category

  /** getter for category - gets Category is the second token of a line in the lexicon input.
Possible categories:
NEG, POS, INT, SHI

Example:
"fehlschlagen NEG=0.7 verben "
category= NEG
   * @generated
   * @return value of the feature 
   */
  public String getCategory() {
    if (PolarExpression_Type.featOkTst && ((PolarExpression_Type)jcasType).casFeat_category == null)
      jcasType.jcas.throwFeatMissing("category", "bachelor.polarity.types.PolarExpression");
    return jcasType.ll_cas.ll_getStringValue(addr, ((PolarExpression_Type)jcasType).casFeatCode_category);}
    
  /** setter for category - sets Category is the second token of a line in the lexicon input.
Possible categories:
NEG, POS, INT, SHI

Example:
"fehlschlagen NEG=0.7 verben "
category= NEG 
   * @generated
   * @param v value to set into the feature 
   */
  public void setCategory(String v) {
    if (PolarExpression_Type.featOkTst && ((PolarExpression_Type)jcasType).casFeat_category == null)
      jcasType.jcas.throwFeatMissing("category", "bachelor.polarity.types.PolarExpression");
    jcasType.ll_cas.ll_setStringValue(addr, ((PolarExpression_Type)jcasType).casFeatCode_category, v);}    
   
    
  //*--------------*
  //* Feature: mwe

  /** getter for mwe - gets True if the expression is a multi word expression, false otherwise.
   * @generated
   * @return value of the feature 
   */
  public boolean getMwe() {
    if (PolarExpression_Type.featOkTst && ((PolarExpression_Type)jcasType).casFeat_mwe == null)
      jcasType.jcas.throwFeatMissing("mwe", "bachelor.polarity.types.PolarExpression");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((PolarExpression_Type)jcasType).casFeatCode_mwe);}
    
  /** setter for mwe - sets True if the expression is a multi word expression, false otherwise. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setMwe(boolean v) {
    if (PolarExpression_Type.featOkTst && ((PolarExpression_Type)jcasType).casFeat_mwe == null)
      jcasType.jcas.throwFeatMissing("mwe", "bachelor.polarity.types.PolarExpression");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((PolarExpression_Type)jcasType).casFeatCode_mwe, v);}    
  }

    