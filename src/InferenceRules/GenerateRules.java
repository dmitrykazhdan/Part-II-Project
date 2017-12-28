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

import OWLExpressionTemplates.AtomicCls;
import OWLExpressionTemplates.CardExpStr;
import OWLExpressionTemplates.ClsExpStr;
import OWLExpressionTemplates.DataRangeTemplatePrimitive;
import OWLExpressionTemplates.EntityStr;
import OWLExpressionTemplates.ExistsOrForAll;
import OWLExpressionTemplates.GenericExpStr;
import OWLExpressionTemplates.InterUnionComp;
import OWLExpressionTemplates.OWLAxiomStr;
import ProofTreeComputation.ProofTree;
import ProofTreeComputation.ProofTreeGenerator;

public class GenerateRules {

	/*
	 ToDo:

	 - Implement exception Case1, subCase1.
	 - Implement rule application for the string rules.
	 
	 - Need to fixup the way cardinality is currently implemented.
	   Strings are too ugly to use as integer representation.
	   
	 - Methods at the bottom are indeed convenient, but instead integrate them
	   into constructors of the classes. That way can only have a limited amount of classes
	   and you promote type-safety and eliminate sources of bugs.

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
		GenericExpStr tmp;

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


		
		
		// Rule 36
		tmp = new ExistsOrForAll(ClassExpressionType.OBJECT_SOME_VALUES_FROM, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new AtomicCls("Z"));

		premise1 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), tmp);
				
		premise2 = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_DOMAIN, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new AtomicCls("Y"));

		conclusion =  new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), new AtomicCls("Y"));
		RuleString rule36_1 = new RuleString("36.1", "ObjSom-ObjDom", conclusion, premise1, premise2);
		

		tmp = new CardExpStr(ClassExpressionType.OBJECT_MIN_CARDINALITY, "n", false, "0", 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new AtomicCls("Z"));
				
		premise1 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), tmp);
		RuleString rule36_2 = new RuleString("36.2", "ObjSom-ObjDom", conclusion, premise1, premise2);

		
		tmp = new CardExpStr(ClassExpressionType.OBJECT_EXACT_CARDINALITY, "n", false, "0", 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new AtomicCls("Z"));
				
		premise1 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), tmp);				
		RuleString rule36_3 = new RuleString("36.3", "ObjSom-ObjDom", conclusion, premise1, premise2);
		
		
		
		
		// Rule 37
		tmp = new ExistsOrForAll(ClassExpressionType.DATA_SOME_VALUES_FROM, 
				new EntityStr("Rd", EntityType.DATA_PROPERTY), new AtomicCls("Dr"));
				
		premise1 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), tmp);
				
		premise2 = new OWLAxiomStr(AxiomType.DATA_PROPERTY_DOMAIN, 
				new EntityStr("Rd", EntityType.DATA_PROPERTY), new AtomicCls("Y"));

		conclusion =  new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), new AtomicCls("Y"));
		RuleString rule37_1 = new RuleString("37.1", "DatSom-DatDom", conclusion, premise1, premise2);
		

		tmp = new CardExpStr(ClassExpressionType.DATA_MIN_CARDINALITY, "n", false, "0", 
				new EntityStr("Rd", EntityType.DATA_PROPERTY), new AtomicCls("Dr"));
				
		premise1 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), tmp);
		RuleString rule37_2 = new RuleString("37.2", "DatSom-DatDom", conclusion, premise1, premise2);

		
		tmp = new CardExpStr(ClassExpressionType.DATA_EXACT_CARDINALITY, "n", false, "0", 
				new EntityStr("Rd", EntityType.DATA_PROPERTY), new AtomicCls("Dr"));
				
		premise1 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), tmp);				
		RuleString rule37_3 = new RuleString("37.3", "DatSom-DatDom", conclusion, premise1, premise2);
		
		
		

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
		tmp = new InterUnionComp(ClassExpressionType.OBJECT_INTERSECTION_OF, new AtomicCls("Y"), new AtomicCls("Z"));		
		conclusion = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), tmp);
		RuleString rule40 = new RuleString("40", "SubCls-SubCls-2", conclusion, premise1, premise2);


		
		// Rule 41
		tmp = new ExistsOrForAll(ClassExpressionType.OBJECT_SOME_VALUES_FROM, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new AtomicCls("Y"));
	
		premise1 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), tmp);

		tmp = new CardExpStr(ClassExpressionType.OBJECT_MIN_CARDINALITY, "n", false, "1", 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new AtomicCls("Y"));

		premise2 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, tmp, new AtomicCls("Z"));

		conclusion = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), new AtomicCls("Z"));
		RuleString rule41_1 = new RuleString("41.1", "ObjSom-ObjMin", conclusion, premise1, premise2);

		
		
		tmp = new CardExpStr(ClassExpressionType.OBJECT_MIN_CARDINALITY, "n", false, "0", 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new AtomicCls("Y"));
	
		premise1 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), tmp);

		tmp = new ExistsOrForAll(ClassExpressionType.OBJECT_SOME_VALUES_FROM, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new AtomicCls("Y"));

		premise2 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, tmp, new AtomicCls("Z"));
		RuleString rule41_2 = new RuleString("41.2", "ObjSom-ObjMin", conclusion, premise1, premise2);


		
		tmp = new CardExpStr(ClassExpressionType.OBJECT_EXACT_CARDINALITY, "n", false, "0", 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new AtomicCls("Y"));
	
		premise1 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), tmp);
		RuleString rule41_3 = new RuleString("41.3", "ObjSom-ObjMin", conclusion, premise1, premise2);

		
		
		
		// Rule 42
		
		// 42.1
		tmp = new ExistsOrForAll(ClassExpressionType.DATA_SOME_VALUES_FROM, 
				new EntityStr("Rd", EntityType.DATA_PROPERTY), new DataRangeTemplatePrimitive("Dr"));
		
		premise1 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), tmp);

		tmp = new CardExpStr(ClassExpressionType.DATA_MIN_CARDINALITY, "n", false, "1", 
				new EntityStr("Rd", EntityType.DATA_PROPERTY), new DataRangeTemplatePrimitive("Dr"));

		premise2 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, tmp, new AtomicCls("Z"));

		conclusion = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), new AtomicCls("Z"));
		RuleString rule42_1 = new RuleString("42.1", "DatSom-DatMin", conclusion, premise1, premise2);


		// 42.2
		tmp = new CardExpStr(ClassExpressionType.DATA_MIN_CARDINALITY, "n", false, "0", 
				new EntityStr("Rd", EntityType.DATA_PROPERTY), new DataRangeTemplatePrimitive("Dr"));
		
		premise1 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), tmp);

		tmp = new ExistsOrForAll(ClassExpressionType.DATA_SOME_VALUES_FROM, 
				new EntityStr("Rd", EntityType.DATA_PROPERTY), new DataRangeTemplatePrimitive("Dr"));
	
		premise2 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, tmp, new AtomicCls("Z"));

		conclusion = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), new AtomicCls("Z"));
		RuleString rule42_2 = new RuleString("42.2", "DatSom-DatMin", conclusion, premise1, premise2);


		// 42.3
		tmp = new CardExpStr(ClassExpressionType.DATA_EXACT_CARDINALITY, "n", false, "0", 
				new EntityStr("Rd", EntityType.DATA_PROPERTY), new DataRangeTemplatePrimitive("Dr"));
		
		premise1 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), tmp);
		RuleString rule42_3 = new RuleString("42.3", "DatSom-DatMin", conclusion, premise1, premise2);

		
		
			
		// Rule 43
		
		// 43.1
		tmp = new ExistsOrForAll(ClassExpressionType.OBJECT_SOME_VALUES_FROM, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), 
				new AtomicCls("Y"));

		premise1 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), tmp);
		premise2 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("Y"), new AtomicCls("Z"));

		tmp = new ExistsOrForAll(ClassExpressionType.OBJECT_SOME_VALUES_FROM, new EntityStr("Ro", EntityType.OBJECT_PROPERTY), 
				new AtomicCls("Z"));

		conclusion = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), tmp);
		RuleString rule43_1 = new RuleString("43.1", "ObjSom-SubCls", conclusion, premise1, premise2);


		// 43.2
		tmp = new CardExpStr(ClassExpressionType.OBJECT_MIN_CARDINALITY, "n", false, "0", 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new AtomicCls("Y"));

		premise1 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), tmp);
		premise2 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("Y"), new AtomicCls("Z"));

		tmp = new CardExpStr(ClassExpressionType.OBJECT_MIN_CARDINALITY, "n", false, "0", 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new AtomicCls("Z"));

		conclusion = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), tmp);
		RuleString rule43_2 = new RuleString("43.2", "ObjSom-SubCls", conclusion, premise1, premise2);

		
		// 43.3
		tmp = new CardExpStr(ClassExpressionType.OBJECT_EXACT_CARDINALITY, "n", false, "0", 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new AtomicCls("Y"));

		premise1 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), tmp);
		premise2 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("Y"), new AtomicCls("Z"));

		tmp = new CardExpStr(ClassExpressionType.OBJECT_EXACT_CARDINALITY, "n", false, "0", 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), new AtomicCls("Z"));

		conclusion = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), tmp);
		RuleString rule43_3 = new RuleString("43.3", "ObjSom-SubCls", conclusion, premise1, premise2);

		
		
		
		
		// Rule 44
		
		// 44.1
		premise1 = getSubClassOfAxiom("X", getPrimitiveObjSomeValFrom("Ro", "Y"));
		premise2 = getSubObjectPropertyOf("Ro", "So");
		conclusion = getSubClassOfAxiom("X", getPrimitiveObjSomeValFrom("So", "Y"));
		RuleString rule44_1 = new RuleString("44.1", "ObjSom-SubObj", conclusion, premise1, premise2);
				
		premise1 = getSubClassOfAxiom("X", createPrimitiveObjMinCard("n", false, "0", "Ro", "Y"));
		conclusion = getSubClassOfAxiom("X", createPrimitiveObjMinCard("n", false, "0", "So", "Y"));
		RuleString rule44_2 = new RuleString("44.2", "ObjSom-SubObj", conclusion, premise1, premise2);

		premise1 = getSubClassOfAxiom("X", createPrimitiveObjExactCard("n", false, "0", "Ro", "Y"));
		conclusion = getSubClassOfAxiom("X", createPrimitiveObjExactCard("n", false, "0", "So", "Y"));
		RuleString rule44_3 = new RuleString("44.3", "ObjSom-SubObj", conclusion, premise1, premise2);

		
		
		
		// Rule 45
		premise1 = getSubClassOfAxiom("X", new InterUnionComp(ClassExpressionType.OBJECT_UNION_OF, 
															new AtomicCls("Y"), new AtomicCls("Z")));
		premise2 = getSubClassOfAxiom("Y", "Z");
		conclusion = getSubClassOfAxiom("X", "Z");
		RuleString rule45 = new RuleString("45", "ObjUni-SubCls", conclusion, premise1, premise2);

// 	private static CardExpStr createPrimitiveObjMinCard(String cardinality, boolean isRelativeBound, String lowerBound, String property, String expression) {
		
		
		
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

	
	private static OWLAxiomStr getSubObjectPropertyOf(String property1, String property2) {		
		return new OWLAxiomStr(AxiomType.SUB_OBJECT_PROPERTY, new AtomicCls(property1), new AtomicCls(property2));	
	}
	
	private static ExistsOrForAll getPrimitiveObjSomeValFrom(String property, String cls) {
		return new ExistsOrForAll(ClassExpressionType.OBJECT_SOME_VALUES_FROM, 
				new EntityStr(property, EntityType.OBJECT_PROPERTY), new AtomicCls(cls));
	}
	
	
	private static OWLAxiomStr getSubClassOfAxiom(String subCls, String superCls) {
		return getSubClassOfAxiom(new AtomicCls(subCls), new AtomicCls(subCls));
	}
	
	private static OWLAxiomStr getSubClassOfAxiom(ClsExpStr subCls, String superCls) {
		return getSubClassOfAxiom(subCls, new AtomicCls(superCls));
	}
	
	private static OWLAxiomStr getSubClassOfAxiom(String subCls, ClsExpStr superCls) {
		return getSubClassOfAxiom(new AtomicCls(subCls), superCls);
	}
	
	private static OWLAxiomStr getSubClassOfAxiom(ClsExpStr subCls, ClsExpStr superCls) {
		return new OWLAxiomStr(AxiomType.SUBCLASS_OF, subCls, superCls);
	}
	
	
	private static CardExpStr createPrimitiveObjExactCard(String cardinality, boolean isRelativeBound, String lowerBound, String property, String expression) {
		return createObjCardExp(ClassExpressionType.OBJECT_EXACT_CARDINALITY, cardinality, 
				isRelativeBound, lowerBound, property, expression);
	}
	
	
	private static CardExpStr createPrimitiveObjMaxCard(String cardinality, boolean isRelativeBound, String lowerBound, String property, String expression) {
		return createObjCardExp(ClassExpressionType.OBJECT_MAX_CARDINALITY, cardinality, 
				isRelativeBound, lowerBound, property, expression);
	}
	
	private static CardExpStr createPrimitiveObjMinCard(String cardinality, boolean isRelativeBound, String lowerBound, String property, String expression) {
		return createObjCardExp(ClassExpressionType.OBJECT_MIN_CARDINALITY, cardinality, 
				isRelativeBound, lowerBound, property, expression);
	}
	
	private static CardExpStr createObjCardExp(ClassExpressionType expType, String cardinality, boolean isRelativeBound, String lowerBound, String property, String expression) {
		return new CardExpStr(expType, cardinality, 
				isRelativeBound, lowerBound, new EntityStr(property, EntityType.OBJECT_PROPERTY), new AtomicCls(expression));
	}
	

	
	private static void getDomainAxiomStr() {
		// Fill in and change return type
	}
	
	

	private static GenericExpStr createExpression(GenericExpStr...expStrs) {


		return null;
	}
	

}
