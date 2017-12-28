package InferenceRules;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.OWLAxiom;

import InfRuleAsAnonClass.InferenceRule;
import ProofTreeComputation.ProofTree;
import ProofTreeComputation.ProofTreeGenerator;

public class GenerateRules {

	/*
	 ToDo:

	 Implement exception Case1, subCase1.
	 Implement rule application for the string rules.
	 */
	
	private static Map<Integer, List<RuleString>> rules = null;

	public static Map<Integer, List<RuleString>> getRules() {

		if (rules == null) {
			generateRules();
		} 

		return rules;
	}
	


	public static void generateRules() {

		
		rules = new HashMap<Integer, List<RuleString>>();

		for (int i = 1; i <= 4; i++) {			
			rules.put(i, new ArrayList<RuleString>());			
		}


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

		RuleString rule26 = new RuleString("26", "SubObj-SubObj", conclusion, premise1, premise2);



		// Rule 27
		premise1 = new OWLAxiomStr(AxiomType.TRANSITIVE_OBJECT_PROPERTY, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY));

		premise2 = new OWLAxiomStr(AxiomType.INVERSE_OBJECT_PROPERTIES, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), 
				new EntityStr("So", EntityType.OBJECT_PROPERTY));

		conclusion = new OWLAxiomStr(AxiomType.TRANSITIVE_OBJECT_PROPERTY, 
				new EntityStr("So", EntityType.OBJECT_PROPERTY));

		RuleString rule27 = new RuleString("27", "ObjTra-ObjInv", conclusion, premise1, premise2);





		// Rule 28
		premise1 = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_DOMAIN, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new AtomicCls("X"));

		premise2 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), new AtomicCls("Y"));

		conclusion = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_DOMAIN, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new AtomicCls("Y"));

		RuleString rule28 = new RuleString("28", "ObjDom-SubCls", conclusion, premise1, premise2);




		// Rule 29
		premise1 = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_DOMAIN, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new AtomicCls("X"));

		premise2 = new OWLAxiomStr(AxiomType.SUB_OBJECT_PROPERTY, 
				new EntityStr("So", EntityType.OBJECT_PROPERTY),
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY));

		conclusion = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_DOMAIN, 
				new EntityStr("So", EntityType.OBJECT_PROPERTY), new AtomicCls("Y"));

		RuleString rule29 = new RuleString("29", "ObjDom-SubObj", conclusion, premise1, premise2);


		
		// Rule 30
		premise1 = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_RANGE, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new AtomicCls("X"));

		premise2 = new OWLAxiomStr(AxiomType.INVERSE_OBJECT_PROPERTIES, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY),
				new EntityStr("So", EntityType.OBJECT_PROPERTY));

		conclusion = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_DOMAIN, 
				new EntityStr("So", EntityType.OBJECT_PROPERTY), new AtomicCls("X"));

		RuleString rule30 = new RuleString("30", "ObjRng-ObjInv", conclusion, premise1, premise2);
		

	
		
		// Rule 31
		premise1 = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_RANGE, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new AtomicCls("X"));

		premise2 = new OWLAxiomStr(AxiomType.SYMMETRIC_OBJECT_PROPERTY, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY));

		conclusion = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_DOMAIN, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new AtomicCls("X"));

		RuleString rule31 = new RuleString("31", "ObjRng-ObjSym", conclusion, premise1, premise2);


		

		// Rule 32
		premise1 = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_RANGE, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new AtomicCls("X"));

		premise2 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), new AtomicCls("Y"));

		conclusion = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_RANGE, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new AtomicCls("Y"));

		RuleString rule32 = new RuleString("32", "ObjRng-SubCls", conclusion, premise1, premise2);


		
		
		// Rule 33
		premise1 = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_RANGE, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new AtomicCls("X"));

		premise2 = new OWLAxiomStr(AxiomType.SUB_OBJECT_PROPERTY, 
				new EntityStr("So", EntityType.OBJECT_PROPERTY),
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY));

		conclusion = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_RANGE, 
				new EntityStr("So", EntityType.OBJECT_PROPERTY), new AtomicCls("X"));

		RuleString rule33 = new RuleString("33", "ObjRng-SubObj", conclusion, premise1, premise2);

		
		
		
		// Rule 34
		premise1 = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_DOMAIN, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new AtomicCls("X"));

		premise2 = new OWLAxiomStr(AxiomType.INVERSE_OBJECT_PROPERTIES, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY),
				new EntityStr("So", EntityType.OBJECT_PROPERTY));

		conclusion = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_RANGE, 
				new EntityStr("So", EntityType.OBJECT_PROPERTY), new AtomicCls("X"));

		RuleString rule34 = new RuleString("34", "ObjDom-ObjInv", conclusion, premise1, premise2);
		

		
		// Rule 35
		premise1 = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_DOMAIN, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new AtomicCls("X"));

		premise2 = new OWLAxiomStr(AxiomType.SYMMETRIC_OBJECT_PROPERTY, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY));

		conclusion = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_RANGE, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new AtomicCls("X"));

		RuleString rule35 = new RuleString("35", "ObjDom-ObjSym", conclusion, premise1, premise2);


		
		
		
		
		

		// Rule 39
		premiseChildren.add(new AtomicCls("X"));
		premiseChildren.add(new AtomicCls("Y"));
		premise1 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, premiseChildren);

		premiseChildren = new ArrayList<GenericExpStr>();
		premiseChildren.add(new AtomicCls("Y"));
		premiseChildren.add(new AtomicCls("Z"));
		premise2 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, premiseChildren);		

		premiseChildren = new ArrayList<GenericExpStr>();
		premiseChildren.add(new AtomicCls("X"));
		premiseChildren.add(new AtomicCls("Z"));
		conclusion = new OWLAxiomStr(AxiomType.SUBCLASS_OF, premiseChildren);		

		List<OWLAxiomStr> premises = new ArrayList<OWLAxiomStr>();
		premises.add(premise1);
		premises.add(premise2);
		RuleString rule39 = new RuleString("39", "SubCls-SubCls-1", conclusion, premise1, premise2);



		// Rule 40
		premise1 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), new AtomicCls("Y"));
		premise2 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), new AtomicCls("Z"));
		GenericExpStr tmp = new InterUnionComp(ClassExpressionType.OBJECT_INTERSECTION_OF, new AtomicCls("Y"), new AtomicCls("Z"));		
		conclusion = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), tmp);
		RuleString rule40 = new RuleString("40", "SubCls-SubCls-2", conclusion, premise1, premise2);



		// Rule 43
		tmp = new ExistsOrForAll(ClassExpressionType.OBJECT_SOME_VALUES_FROM, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), 
				new AtomicCls("Y"));

		premise1 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), tmp);
		premise2 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("Y"), new AtomicCls("Z"));

		tmp = new ExistsOrForAll(ClassExpressionType.OBJECT_SOME_VALUES_FROM, new EntityStr("Ro", EntityType.OBJECT_PROPERTY), 
				new AtomicCls("Z"));

		conclusion = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), tmp);
		RuleString rule43_1 = new RuleString("43.1", "ObjSom-SubCls", conclusion, premise1, premise2);


		
		
		// Rule 46
		tmp = new ExistsOrForAll(ClassExpressionType.OBJECT_ALL_VALUES_FROM, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new AtomicCls("Y"));

		premise1 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), tmp);

		premise2 = new OWLAxiomStr(AxiomType.INVERSE_OBJECT_PROPERTIES, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY),
				new EntityStr("So", EntityType.OBJECT_PROPERTY));

		tmp = new ExistsOrForAll(ClassExpressionType.OBJECT_ALL_VALUES_FROM, 
				new EntityStr("So", EntityType.OBJECT_PROPERTY), new AtomicCls("X"));

		conclusion = new OWLAxiomStr(AxiomType.SUBCLASS_OF, tmp, new AtomicCls("Y"));
		RuleString rule46 = new RuleString("46", "ObjAll-ObjInv", conclusion, premise1, premise2);
		

		
		// Rule 47
		tmp = new ExistsOrForAll(ClassExpressionType.OBJECT_SOME_VALUES_FROM, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new AtomicCls("Y"));

		premise1 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, tmp, new AtomicCls("X"));

		tmp = new ExistsOrForAll(ClassExpressionType.OBJECT_ALL_VALUES_FROM, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new AtomicCls("F"));

		premise2 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, tmp, new AtomicCls("X"));

		tmp = new ExistsOrForAll(ClassExpressionType.OBJECT_ALL_VALUES_FROM, 
				new EntityStr("So", EntityType.OBJECT_PROPERTY), new AtomicCls("Y"));

		conclusion = new OWLAxiomStr(AxiomType.SUBCLASS_OF, tmp, new AtomicCls("X"));
		RuleString rule47 = new RuleString("47", "ObjSom-ObjAll-1", conclusion, premise1, premise2);


		
		// Rule 48
		tmp = new ExistsOrForAll(ClassExpressionType.OBJECT_SOME_VALUES_FROM, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new AtomicCls("T"));

		premise1 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), tmp);

		tmp = new ExistsOrForAll(ClassExpressionType.OBJECT_ALL_VALUES_FROM, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new AtomicCls("Y"));

		premise2 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), tmp);

		tmp = new ExistsOrForAll(ClassExpressionType.OBJECT_SOME_VALUES_FROM, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new AtomicCls("Y"));

		conclusion = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), tmp);
		RuleString rule48 = new RuleString("48", "ObjSom-ObjAll-2", conclusion, premise1, premise2);
		
		
		
		
		// Rule 49
		tmp = new ExistsOrForAll(ClassExpressionType.OBJECT_SOME_VALUES_FROM, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), 
				new ExistsOrForAll(ClassExpressionType.OBJECT_SOME_VALUES_FROM, 
						new EntityStr("Ro", EntityType.OBJECT_PROPERTY), 
						new AtomicCls("Y")));

		premise1 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), tmp);

		premise2 = new OWLAxiomStr(AxiomType.TRANSITIVE_OBJECT_PROPERTY, new EntityStr("Ro", EntityType.OBJECT_PROPERTY));

		tmp = new ExistsOrForAll(ClassExpressionType.OBJECT_SOME_VALUES_FROM, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new AtomicCls("Y"));

		conclusion = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), tmp);
		RuleString rule49_1 = new RuleString("49.1", "ObjSom-ObjTra", conclusion, premise1, premise2);

		

		// Rule 50
		premise1 = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_DOMAIN, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new AtomicCls("X"));

		premise2 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), new AtomicCls("F"));

		tmp = new ExistsOrForAll(ClassExpressionType.OBJECT_ALL_VALUES_FROM, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new AtomicCls("F"));

		conclusion = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("T"), tmp);
		RuleString rule50 = new RuleString("50", "ObjDom-Bot", conclusion, premise1, premise2);


		
		// Rule 51
		premise1 = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_RANGE, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new AtomicCls("X"));

		premise2 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), new AtomicCls("F"));

		tmp = new ExistsOrForAll(ClassExpressionType.OBJECT_ALL_VALUES_FROM, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new AtomicCls("F"));

		conclusion = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("T"), tmp);
		RuleString rule51 = new RuleString("51", "ObjRng-Bot", conclusion, premise1, premise2);

		
		
		rules.get(2).add(rule51);

	}

	
	private static void getDomainAxiomStr() {
		// Fill in and change return type
	}
	
	

	private static GenericExpStr createExpression(GenericExpStr...expStrs) {


		return null;
	}
}
