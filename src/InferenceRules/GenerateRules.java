package InferenceRules;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.EntityType;

public class GenerateRules {

	
	
	public static void main (String args[]) {
		
		List<GenericExpStr> premiseChildren = new ArrayList<GenericExpStr>();
		
		
		// Sample rule: rule 18	
		List<ClsExpStr> leaves = new ArrayList<ClsExpStr>();
		leaves.add(new ClsExpStr("n"));
		leaves.add(new ClsExpStr("R_0"));
		leaves.add(new ClsExpStr("Y"));
		GenericExpStr premise1Child = new ClsExpStr(ClassExpressionType.DATA_MIN_CARDINALITY, leaves);
		GenericExpStr premise2Child = new ClsExpStr("X");
		premiseChildren.add(premise2Child);
		premiseChildren.add(premise1Child);
		OWLAxiomStr premise1 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, premiseChildren);
		
		
		premise2Child = new EntityStr("R_0", EntityType.OBJECT_PROPERTY);
		premiseChildren = new ArrayList<GenericExpStr>();
		premiseChildren.add(premise2Child);
		OWLAxiomStr premise2 = new OWLAxiomStr(AxiomType.FUNCTIONAL_OBJECT_PROPERTY, premiseChildren);

		
		List<OWLAxiomStr> premises = new ArrayList<OWLAxiomStr>();
		premises.add(premise1);
		premises.add(premise2);
		
		RuleString rule18 = new RuleString(premises, 2);
		
	}
}
