package InferenceRules;

import org.semanticweb.owlapi.model.ClassExpressionType;

public class AtomicCls extends ClsExpStr {

	private String placeholder;
	
	public AtomicCls(String value) {
		super(null);
		this.placeholder = value;
	}
	
	public String getPlaceholder() { return placeholder; }
}
