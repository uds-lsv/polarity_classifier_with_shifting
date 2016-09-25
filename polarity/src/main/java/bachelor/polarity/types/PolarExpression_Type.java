
/* First created by JCasGen Sun Jul 24 15:18:54 CEST 2016 */
package bachelor.polarity.types;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** A polar expression.
E.g. "moegen, hassen, gut, schlecht, ..."
 * Updated by JCasGen Sun Sep 11 15:00:25 CEST 2016
 * @generated */
public class PolarExpression_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (PolarExpression_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = PolarExpression_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new PolarExpression(addr, PolarExpression_Type.this);
  			   PolarExpression_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new PolarExpression(addr, PolarExpression_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = PolarExpression.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("bachelor.polarity.types.PolarExpression");
 
  /** @generated */
  final Feature casFeat_value;
  /** @generated */
  final int     casFeatCode_value;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public double getValue(int addr) {
        if (featOkTst && casFeat_value == null)
      jcas.throwFeatMissing("value", "bachelor.polarity.types.PolarExpression");
    return ll_cas.ll_getDoubleValue(addr, casFeatCode_value);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setValue(int addr, double v) {
        if (featOkTst && casFeat_value == null)
      jcas.throwFeatMissing("value", "bachelor.polarity.types.PolarExpression");
    ll_cas.ll_setDoubleValue(addr, casFeatCode_value, v);}
    
  
 
  /** @generated */
  final Feature casFeat_pos;
  /** @generated */
  final int     casFeatCode_pos;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getPos(int addr) {
        if (featOkTst && casFeat_pos == null)
      jcas.throwFeatMissing("pos", "bachelor.polarity.types.PolarExpression");
    return ll_cas.ll_getStringValue(addr, casFeatCode_pos);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPos(int addr, String v) {
        if (featOkTst && casFeat_pos == null)
      jcas.throwFeatMissing("pos", "bachelor.polarity.types.PolarExpression");
    ll_cas.ll_setStringValue(addr, casFeatCode_pos, v);}
    
  
 
  /** @generated */
  final Feature casFeat_category;
  /** @generated */
  final int     casFeatCode_category;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getCategory(int addr) {
        if (featOkTst && casFeat_category == null)
      jcas.throwFeatMissing("category", "bachelor.polarity.types.PolarExpression");
    return ll_cas.ll_getStringValue(addr, casFeatCode_category);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setCategory(int addr, String v) {
        if (featOkTst && casFeat_category == null)
      jcas.throwFeatMissing("category", "bachelor.polarity.types.PolarExpression");
    ll_cas.ll_setStringValue(addr, casFeatCode_category, v);}
    
  
 
  /** @generated */
  final Feature casFeat_mwe;
  /** @generated */
  final int     casFeatCode_mwe;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public boolean getMwe(int addr) {
        if (featOkTst && casFeat_mwe == null)
      jcas.throwFeatMissing("mwe", "bachelor.polarity.types.PolarExpression");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_mwe);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setMwe(int addr, boolean v) {
        if (featOkTst && casFeat_mwe == null)
      jcas.throwFeatMissing("mwe", "bachelor.polarity.types.PolarExpression");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_mwe, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public PolarExpression_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_value = jcas.getRequiredFeatureDE(casType, "value", "uima.cas.Double", featOkTst);
    casFeatCode_value  = (null == casFeat_value) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_value).getCode();

 
    casFeat_pos = jcas.getRequiredFeatureDE(casType, "pos", "uima.cas.String", featOkTst);
    casFeatCode_pos  = (null == casFeat_pos) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_pos).getCode();

 
    casFeat_category = jcas.getRequiredFeatureDE(casType, "category", "uima.cas.String", featOkTst);
    casFeatCode_category  = (null == casFeat_category) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_category).getCode();

 
    casFeat_mwe = jcas.getRequiredFeatureDE(casType, "mwe", "uima.cas.Boolean", featOkTst);
    casFeatCode_mwe  = (null == casFeat_mwe) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_mwe).getCode();

  }
}



    