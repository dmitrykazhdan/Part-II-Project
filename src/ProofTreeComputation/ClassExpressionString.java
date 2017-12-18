package ProofTreeComputation;

import java.util.List;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.OWLClassExpression;

public class ClassExpressionString {

	private String atomic;
	private ClassExpressionType constructor;
	private List<ClassExpressionString> children;
	boolean isAtomic;
	
	public ClassExpressionString(String atomic) {
		this.atomic = atomic;
		isAtomic = true;
	}
	
	public ClassExpressionString (ClassExpressionType constructor, List<ClassExpressionString> children) {
		
		this.constructor = constructor;
		this.children = children;
		isAtomic = false;
	}
	
}
