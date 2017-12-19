package InferenceRules;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.AxiomType;

public class GenerateRules {

	
	
	public static void main (String args[]) {
		
		
		ClassExpressionString subCls = new ClassExpressionString("T");
		ClassExpressionString superCls = new ClassExpressionString("Y");
		List<ClassExpressionString> children = new ArrayList<ClassExpressionString>();
		children.add(subCls);
		children.add(superCls);	
		OWLAxiomString premise1 = new OWLAxiomString(AxiomType.SUBCLASS_OF, children);
		
	
		OWLAxiomString premise2 = new OWLAxiomString(AxiomType.DISJOINT_CLASSES, children);		
		
		
	}
}
