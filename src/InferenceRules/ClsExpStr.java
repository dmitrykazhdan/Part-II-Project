package InferenceRules;

import java.util.List;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.OWLClassExpression;

public class ClsExpStr implements GenericExpStr {

	private String atomic;
	private ClassExpressionType constructor;
	private List<GenericExpStr> children;
	boolean isAtomic;
	
	public ClsExpStr(String atomic) {
		this.atomic = atomic;
		isAtomic = true;
	}
	
	public ClsExpStr (ClassExpressionType constructor, List<GenericExpStr> children) {
		
		this.constructor = constructor;
		this.children = children;
		isAtomic = false;
	}
	
	public ClassExpressionType getConstructor() {
		return constructor;
	}
	
	public String getAtomic() {
		return atomic;
	}
	
	public List<GenericExpStr> getChildren() {
		return children;
	}
	
}
