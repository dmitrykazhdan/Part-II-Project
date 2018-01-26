package OWLExpressionTemplates;

import org.semanticweb.owlapi.model.AxiomType;

public class SubClassStr extends OWLAxiomStr {

	private ClsExpStr subClassStr;
	private ClsExpStr superClassStr;
	
	public SubClassStr(ClsExpStr subClassStr, ClsExpStr superClassStr) {
		super(AxiomType.SUBCLASS_OF);
		this.subClassStr = subClassStr;
		this.superClassStr = superClassStr;
	}
	
	public SubClassStr(String subCls, String superCls) {
		this(new AtomicCls(subCls), new AtomicCls(superCls));
	}
	
	public SubClassStr(ClsExpStr subCls, String superCls) {
		this(subCls, new AtomicCls(superCls));
	}
	
	public SubClassStr(String subCls, ClsExpStr superCls) {
		this(new AtomicCls(subCls), superCls);
	}
	
	
	public ClsExpStr getSubClassStr() { return subClassStr; }
	
	public ClsExpStr getSuperClassStr() { return superClassStr; }
	
}
