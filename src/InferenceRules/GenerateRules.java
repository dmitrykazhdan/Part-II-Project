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


		// Rule 26
		OWLAxiomStr premise1 = new OWLAxiomStr(AxiomType.SUB_OBJECT_PROPERTY, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), 
				new EntityStr("So", EntityType.OBJECT_PROPERTY));

		OWLAxiomStr premise2 = new OWLAxiomStr(AxiomType.SUB_OBJECT_PROPERTY, 
				new EntityStr("So", EntityType.OBJECT_PROPERTY), 
				new EntityStr("To", EntityType.OBJECT_PROPERTY));

		OWLAxiomStr conclusion = new OWLAxiomStr(AxiomType.SUB_OBJECT_PROPERTY, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), 
				new EntityStr("To", EntityType.OBJECT_PROPERTY));

		RuleString rule26 = new RuleString(conclusion, premise1, premise2);



		// Rule 27
		premise1 = new OWLAxiomStr(AxiomType.TRANSITIVE_OBJECT_PROPERTY, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY));

		premise2 = new OWLAxiomStr(AxiomType.INVERSE_OBJECT_PROPERTIES, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), 
				new EntityStr("So", EntityType.OBJECT_PROPERTY));

		conclusion = new OWLAxiomStr(AxiomType.TRANSITIVE_OBJECT_PROPERTY, 
				new EntityStr("So", EntityType.OBJECT_PROPERTY));

		RuleString rule27 = new RuleString(conclusion, premise1, premise2);





		// Rule 28
		premise1 = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_DOMAIN, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new ClsExpStr("X"));

		premise2 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new ClsExpStr("X"), new ClsExpStr("Y"));

		conclusion = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_DOMAIN, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new ClsExpStr("Y"));

		RuleString rule28 = new RuleString(conclusion, premise1, premise2);




		// Rule 29
		premise1 = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_DOMAIN, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new ClsExpStr("X"));

		premise2 = new OWLAxiomStr(AxiomType.SUB_OBJECT_PROPERTY, 
				new EntityStr("So", EntityType.OBJECT_PROPERTY),
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY));

		conclusion = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_DOMAIN, 
				new EntityStr("So", EntityType.OBJECT_PROPERTY), new ClsExpStr("Y"));

		RuleString rule29 = new RuleString(conclusion, premise1, premise2);


		
		// Rule 30
		premise1 = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_RANGE, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new ClsExpStr("X"));

		premise2 = new OWLAxiomStr(AxiomType.INVERSE_OBJECT_PROPERTIES, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY),
				new EntityStr("So", EntityType.OBJECT_PROPERTY));

		conclusion = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_DOMAIN, 
				new EntityStr("So", EntityType.OBJECT_PROPERTY), new ClsExpStr("X"));

		RuleString rule30 = new RuleString(conclusion, premise1, premise2);
		

	
		
		// Rule 31
		premise1 = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_RANGE, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new ClsExpStr("X"));

		premise2 = new OWLAxiomStr(AxiomType.SYMMETRIC_OBJECT_PROPERTY, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY));

		conclusion = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_DOMAIN, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new ClsExpStr("X"));

		RuleString rule31 = new RuleString(conclusion, premise1, premise2);


		

		// Rule 32
		premise1 = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_RANGE, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new ClsExpStr("X"));

		premise2 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new ClsExpStr("X"), new ClsExpStr("Y"));

		conclusion = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_RANGE, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new ClsExpStr("Y"));

		RuleString rule32 = new RuleString(conclusion, premise1, premise2);


		
		
		// Rule 33
		premise1 = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_RANGE, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new ClsExpStr("X"));

		premise2 = new OWLAxiomStr(AxiomType.SUB_OBJECT_PROPERTY, 
				new EntityStr("So", EntityType.OBJECT_PROPERTY),
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY));

		conclusion = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_RANGE, 
				new EntityStr("So", EntityType.OBJECT_PROPERTY), new ClsExpStr("X"));

		RuleString rule33 = new RuleString(conclusion, premise1, premise2);

		
		
		
		// Rule 34
		
		
		

		// Rule 39
		premiseChildren.add(new ClsExpStr("X"));
		premiseChildren.add(new ClsExpStr("Y"));
		premise1 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, premiseChildren);

		premiseChildren = new ArrayList<GenericExpStr>();
		premiseChildren.add(new ClsExpStr("Y"));
		premiseChildren.add(new ClsExpStr("Z"));
		premise2 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, premiseChildren);		

		premiseChildren = new ArrayList<GenericExpStr>();
		premiseChildren.add(new ClsExpStr("X"));
		premiseChildren.add(new ClsExpStr("Z"));
		conclusion = new OWLAxiomStr(AxiomType.SUBCLASS_OF, premiseChildren);		

		List<OWLAxiomStr> premises = new ArrayList<OWLAxiomStr>();
		premises.add(premise1);
		premises.add(premise2);
		RuleString rule39 = new RuleString(premises, conclusion, 2);



		// Rule 40
		premise1 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new ClsExpStr("X"), new ClsExpStr("Y"));
		premise2 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new ClsExpStr("X"), new ClsExpStr("Z"));
		GenericExpStr tmp = new ClsExpStr(ClassExpressionType.OBJECT_INTERSECTION_OF, new ClsExpStr("Y"), new ClsExpStr("Z"));		
		conclusion = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new ClsExpStr("X"), tmp);
		RuleString rule40 = new RuleString(conclusion, premise1, premise2);



		// Rule 43
		tmp = new ClsExpStr(ClassExpressionType.OBJECT_SOME_VALUES_FROM, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), 
				new ClsExpStr("Y"));

		premise1 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new ClsExpStr("X"), tmp);
		premise2 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new ClsExpStr("Y"), new ClsExpStr("Z"));

		tmp = new ClsExpStr(ClassExpressionType.OBJECT_SOME_VALUES_FROM, new EntityStr("Ro", EntityType.OBJECT_PROPERTY), 
				new ClsExpStr("Z"));

		conclusion = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new ClsExpStr("X"), tmp);
		RuleString rule43 = new RuleString(conclusion, premise1, premise2);





	}


	private static GenericExpStr createExpression(GenericExpStr...expStrs) {


		return null;
	}
}
