package InferenceRules;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.OWLAxiom;

import ProofTreeComputation.ProofTree;
import ProofTreeComputation.ProofTreeGenerator;

public class GenerateRules {

	/*
	 ToDo:
	 
	 Implement exception Case1, subCase1.
	 Implement rule application for the string rules.
	 */
	
	
	public static void main (String args[]) throws IOException {
		
		List<GenericExpStr> premiseChildren = new ArrayList<GenericExpStr>();
		List<GenericExpStr> leaves = new ArrayList<GenericExpStr>();
		
		
		// Rule 39
		premiseChildren.add(new ClsExpStr("X"));
		premiseChildren.add(new ClsExpStr("Y"));
		OWLAxiomStr premise1 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, premiseChildren);

		premiseChildren = new ArrayList<GenericExpStr>();
		premiseChildren.add(new ClsExpStr("Y"));
		premiseChildren.add(new ClsExpStr("Z"));
		OWLAxiomStr premise2 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, premiseChildren);		

		premiseChildren = new ArrayList<GenericExpStr>();
		premiseChildren.add(new ClsExpStr("X"));
		premiseChildren.add(new ClsExpStr("Z"));
		OWLAxiomStr conclusion = new OWLAxiomStr(AxiomType.SUBCLASS_OF, premiseChildren);		
		
		List<OWLAxiomStr> premises = new ArrayList<OWLAxiomStr>();
		premises.add(premise1);
		premises.add(premise2);
		RuleString rule39 = new RuleString(premises, conclusion, 2);
		
		

		// Rule 43
		GenericExpStr tmp = new ClsExpStr(ClassExpressionType.OBJECT_SOME_VALUES_FROM, 
										new EntityStr("Ro", EntityType.OBJECT_PROPERTY), 
										new ClsExpStr("Y"));
		
		premise1 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new ClsExpStr("X"), tmp);
		premise2 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new ClsExpStr("Y"), new ClsExpStr("Z"));

		tmp = new ClsExpStr(ClassExpressionType.OBJECT_SOME_VALUES_FROM, new EntityStr("Ro", EntityType.OBJECT_PROPERTY), 
																		new ClsExpStr("Z"));
		
		conclusion = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new ClsExpStr("X"), tmp);
		RuleString rule43 = new RuleString(conclusion, premise1, premise2);
		
		
		
		// Rule 40
		premise1 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new ClsExpStr("X"), new ClsExpStr("Y"));
		premise2 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new ClsExpStr("X"), new ClsExpStr("Z"));
		tmp = new ClsExpStr(ClassExpressionType.OBJECT_INTERSECTION_OF, new ClsExpStr("Y"), new ClsExpStr("Z"));		
		conclusion = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new ClsExpStr("X"), tmp);
		RuleString rule40 = new RuleString(conclusion, premise1, premise2);

		
	}
	
	
	private static GenericExpStr createExpression(GenericExpStr...expStrs) {
		
		
		return null;
	}
}
