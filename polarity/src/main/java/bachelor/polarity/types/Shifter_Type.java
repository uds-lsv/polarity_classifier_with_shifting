
/* First created by JCasGen Tue Aug 30 19:21:27 CEST 2016 */
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

/** A shifter "shifts" sentiments.
Examples:
"kein, weniger, nicht, ..."
 * Updated by JCasGen Sun Sep 11 15:00:25 CEST 2016
 * @generated */
public class Shifter_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Shifter_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Shifter_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Shifter(addr, Shifter_Type.this);
  			   Shifter_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Shifter(addr, Shifter_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Shifter.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("bachelor.polarity.types.Shifter");



  /** @generated */
  final Feature casFeat_shifter_type;
  /** @generated */
  final int     casFeatCode_shifter_type;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getShifter_type(int addr) {
        if (featOkTst && casFeat_shifter_type == null)
      jcas.throwFeatMissing("shifter_type", "bachelor.polarity.types.Shifter");
    return ll_cas.ll_getStringValue(addr, casFeatCode_shifter_type);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setShifter_type(int addr, String v) {
        if (featOkTst && casFeat_shifter_type == null)
      jcas.throwFeatMissing("shifter_type", "bachelor.polarity.types.Shifter");
    ll_cas.ll_setStringValue(addr, casFeatCode_shifter_type, v);}
    
  
 
  /** @generated */
  final Feature casFeat_shifter_scope;
  /** @generated */
  final int     casFeatCode_shifter_scope;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getShifter_scope(int addr) {
        if (featOkTst && casFeat_shifter_scope == null)
      jcas.throwFeatMissing("shifter_scope", "bachelor.polarity.types.Shifter");
    return ll_cas.ll_getStringValue(addr, casFeatCode_shifter_scope);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setShifter_scope(int addr, String v) {
        if (featOkTst && casFeat_shifter_scope == null)
      jcas.throwFeatMissing("shifter_scope", "bachelor.polarity.types.Shifter");
    ll_cas.ll_setStringValue(addr, casFeatCode_shifter_scope, v);}
    
  
 
  /** @generated */
  final Feature casFeat_shifter_pos;
  /** @generated */
  final int     casFeatCode_shifter_pos;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getShifter_pos(int addr) {
        if (featOkTst && casFeat_shifter_pos == null)
      jcas.throwFeatMissing("shifter_pos", "bachelor.polarity.types.Shifter");
    return ll_cas.ll_getStringValue(addr, casFeatCode_shifter_pos);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setShifter_pos(int addr, String v) {
        if (featOkTst && casFeat_shifter_pos == null)
      jcas.throwFeatMissing("shifter_pos", "bachelor.polarity.types.Shifter");
    ll_cas.ll_setStringValue(addr, casFeatCode_shifter_pos, v);}
    
  
 
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
      jcas.throwFeatMissing("mwe", "bachelor.polarity.types.Shifter");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_mwe);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setMwe(int addr, boolean v) {
        if (featOkTst && casFeat_mwe == null)
      jcas.throwFeatMissing("mwe", "bachelor.polarity.types.Shifter");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_mwe, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Shifter_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_shifter_type = jcas.getRequiredFeatureDE(casType, "shifter_type", "uima.cas.String", featOkTst);
    casFeatCode_shifter_type  = (null == casFeat_shifter_type) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_shifter_type).getCode();

 
    casFeat_shifter_scope = jcas.getRequiredFeatureDE(casType, "shifter_scope", "uima.cas.String", featOkTst);
    casFeatCode_shifter_scope  = (null == casFeat_shifter_scope) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_shifter_scope).getCode();

 
    casFeat_shifter_pos = jcas.getRequiredFeatureDE(casType, "shifter_pos", "uima.cas.String", featOkTst);
    casFeatCode_shifter_pos  = (null == casFeat_shifter_pos) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_shifter_pos).getCode();

 
    casFeat_mwe = jcas.getRequiredFeatureDE(casType, "mwe", "uima.cas.Boolean", featOkTst);
    casFeatCode_mwe  = (null == casFeat_mwe) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_mwe).getCode();

  }
}



    