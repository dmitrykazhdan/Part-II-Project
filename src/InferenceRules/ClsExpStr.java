package InferenceRules;

import org.semanticweb.owlapi.model.ClassExpressionType;

public abstract class ClsExpStr implements GenericExpStr {
	
	private ClassExpressionType expType;
	
	public ClsExpStr(ClassExpressionType expType) {
		this.expType = expType;
	}
	
	public ClassExpressionType getExpressionType() {
		return expType;
	}
}
