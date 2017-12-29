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
import OWLExpressionTemplates.TemplateDataRange;
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

	 - Add a check for disjointness of data ranges, as given in the rules (e.g. Dr0 & Dr1 are disjoint)
	 
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
				new EntityStr("Rd", EntityType.DATA_PROPERTY), new TemplateDataRange("Dr"));
		
		premise1 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), tmp);

		tmp = new CardExpStr(ClassExpressionType.DATA_MIN_CARDINALITY, "n", false, "1", 
				new EntityStr("Rd", EntityType.DATA_PROPERTY), new TemplateDataRange("Dr"));

		premise2 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, tmp, new AtomicCls("Z"));

		conclusion = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), new AtomicCls("Z"));
		RuleString rule42_1 = new RuleString("42.1", "DatSom-DatMin", conclusion, premise1, premise2);


		// 42.2
		tmp = new CardExpStr(ClassExpressionType.DATA_MIN_CARDINALITY, "n", false, "0", 
				new EntityStr("Rd", EntityType.DATA_PROPERTY), new TemplateDataRange("Dr"));
		
		premise1 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), tmp);

		tmp = new ExistsOrForAll(ClassExpressionType.DATA_SOME_VALUES_FROM, 
				new EntityStr("Rd", EntityType.DATA_PROPERTY), new TemplateDataRange("Dr"));
	
		premise2 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, tmp, new AtomicCls("Z"));

		conclusion = new OWLAxiomStr(AxiomType.SUBCLASS_OF, new AtomicCls("X"), new AtomicCls("Z"));
		RuleString rule42_2 = new RuleString("42.2", "DatSom-DatMin", conclusion, premise1, premise2);


		// 42.3
		tmp = new CardExpStr(ClassExpressionType.DATA_EXACT_CARDINALITY, "n", false, "0", 
				new EntityStr("Rd", EntityType.DATA_PROPERTY), new TemplateDataRange("Dr"));
		
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
		
		// 49.1
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

		
		// 49.2		
		tmp = new CardExpStr(ClassExpressionType.OBJECT_MIN_CARDINALITY, "n", false, "0", 
							new EntityStr("Ro", EntityType.OBJECT_PROPERTY), 
							createPrimitiveObjMinCard("n", false, "0", "Ro", "Y"));

		premise1 = getSubClassOfAxiom("X", (ClsExpStr) tmp);
		premise2 = createPrimitiveTransObjProp("Ro");
		conclusion = getSubClassOfAxiom("X", createPrimitiveObjMinCard("n", false, "0", "Ro", "Y"));
		RuleString rule49_2 = new RuleString("49.2", "ObjSom-ObjTra", conclusion, premise1, premise2);

		
		
		
		

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

		
		
		
		
		
		// Rule 17
		
		// 17.1
		premise1 = getSubClassOfAxiom("X", createPrimitiveObjMinCard("n1", true, "n2", "Ro", "Y"));
		premise2 = getSubClassOfAxiom("X", createPrimitiveObjMaxCard("n2", false, "0", "Ro", "Y"));
	    conclusion = getSubClassOfAxiom("X", "F");
		RuleString rule17_1 = new RuleString("17.1", "ObjMin-ObjMax", conclusion, premise1, premise2);
		
		
		// 17.2
		premise1 = getSubClassOfAxiom("X", createPrimitiveObjExactCard("n1", true, "n2", "Ro", "Y"));
		RuleString rule17_2 = new RuleString("17.2", "ObjMin-ObjMax", conclusion, premise1, premise2);
		
		
		
		// Rule 18
		
		// 18.1
		premise1 = getSubClassOfAxiom("X", createPrimitiveObjMinCard("n", false, "1", "Ro", "Y"));
		premise2 = new OWLAxiomStr(AxiomType.FUNCTIONAL_OBJECT_PROPERTY, new AtomicCls("Ro"));
	    conclusion = getSubClassOfAxiom("X", "F");
		RuleString rule18_1 = new RuleString("18.1", "ObjMin-ObjFun", conclusion, premise1, premise2);

		// 18.2
		premise1 = getSubClassOfAxiom("X", createPrimitiveObjExactCard("n", false, "1", "Ro", "Y"));
		RuleString rule18_2 = new RuleString("18.2", "ObjMin-ObjFun", conclusion, premise1, premise2);

		
		
		// Rule 19
		
		// 19.1
		premise1 = getSubClassOfAxiom("X", createPrimitiveDataMinCard("n", false, "1", "Ro", "Y"));
		premise2 = new OWLAxiomStr(AxiomType.FUNCTIONAL_DATA_PROPERTY, new AtomicCls("Rd"));
	    conclusion = getSubClassOfAxiom("X", "F");
		RuleString rule19_1 = new RuleString("19.1", "DatMin-DatFun", conclusion, premise1, premise2);

		// 19.2
		premise1 = getSubClassOfAxiom("X", createPrimitiveDataExactCard("n", false, "1", "Rd", "Y"));
		RuleString rule19_2 = new RuleString("19.2", "DatMin-DatFun", conclusion, premise1, premise2);

		
		
		// Rule 20
		
		// 20.1
		premise1 = getSubClassOfAxiom("X", getPrimitiveObjSomeValFrom("Ro", "Y"));
		premise2 = getSubClassOfAxiom("Y", "F");
		conclusion = getSubClassOfAxiom("X", "F");
		RuleString rule20_1 = new RuleString("20.1", "ObjSom-Bot-1", conclusion, premise1, premise2);
		
		// 20.2
		premise1 = getSubClassOfAxiom("X", createPrimitiveObjMinCard("n", false, "0", "Ro", "Y"));
		RuleString rule20_2 = new RuleString("20.2", "ObjSom-Bot-1", conclusion, premise1, premise2);

		// 20.3
		premise1 = getSubClassOfAxiom("X", createPrimitiveObjExactCard("n", false, "0", "Ro", "Y"));
		RuleString rule20_3 = new RuleString("20.3", "ObjSom-Bot-1", conclusion, premise1, premise2);
		
		
		
		
		// Rule 23
		premise1 = getSubClassOfAxiom("X", "Y");
		premise2 = getSubClassOfAxiom("X", new InterUnionComp(ClassExpressionType.OBJECT_COMPLEMENT_OF, new AtomicCls("Y")));
		conclusion =  getSubClassOfAxiom("X", "F");
		RuleString rule23 = new RuleString("23", "SubCls-ObjCom-1", conclusion, premise1, premise2);
		
	
		
		// Rule 24
		premise1 = getSubClassOfAxiom("X", "Y");
		premise2 = getSubClassOfAxiom(new InterUnionComp(ClassExpressionType.OBJECT_COMPLEMENT_OF, new AtomicCls("X")), "Y");
		conclusion =  getSubClassOfAxiom("T", "Y");
		RuleString rule24 = new RuleString("24", "SubCls-ObjCom-2", conclusion, premise1, premise2);
		
	
		
		// Rule 25
		
		// 25.1
		premise1 = createPrimitiveObjDomain("Ro", "X");
		premise2 = getSubClassOfAxiom(createPrimitiveObjAllValFrom("Ro", "F"), "X");
		conclusion =  getSubClassOfAxiom("T", "X");
		RuleString rule25_1 = new RuleString("25.1", "ObjDom-ObjAll", conclusion, premise1, premise2);
		
		// 25.2
		premise1 = getSubClassOfAxiom(getPrimitiveObjSomeValFrom("Ro", "F"), "X");		
		RuleString rule25_2 = new RuleString("25.2", "ObjDom-ObjAll", conclusion, premise1, premise2);
		
		
		
		OWLAxiomStr premise3;
		
		
		

		
		// Rule 55
		
		// 55.1
		premise1 = getSubClassOfAxiom("X", getPrimitiveObjSomeValFrom("Ro", "Y"));
		premise2 = getSubClassOfAxiom("Y", getPrimitiveObjSomeValFrom("Ro", "Z"));
		premise3 = createPrimitiveTransObjProp("Ro");
		conclusion = getSubClassOfAxiom("X", getPrimitiveObjSomeValFrom("Ro", "Z"));
		RuleString rule55_1 = new RuleString("55.1", "ObjSom-ObjSom-ObjTra", conclusion, premise1, premise2, premise3);
		
		// 55.2
		premise1 = getSubClassOfAxiom("X", createPrimitiveObjMinCard("n", false, "0", "Ro", "Y"));
		premise2 = getSubClassOfAxiom("Y", createPrimitiveObjMinCard("n", false, "0", "Ro", "Z"));
		premise3 = createPrimitiveTransObjProp("Ro");
		conclusion = getSubClassOfAxiom("X", createPrimitiveObjMinCard("n", false, "0", "Ro", "Z"));
		RuleString rule55_2 = new RuleString("55.2", "ObjSom-ObjSom-ObjTra", conclusion, premise1, premise2, premise3);
		
		
		
		
		// Rule 6
		
		// 6.1
		premise1 = getSubClassOfAxiom("X", createPrimitiveObjExactCard("n1", true, "n2", "Ro", "Y"));
		conclusion = getSubClassOfAxiom("X", createPrimitiveObjMinCard("n2", false, "0", "Ro", "Y"));
		RuleString rule6_1 = new RuleString("6.1", "ObjExt", conclusion, premise1);

		// 6.2
		premise1 = getSubClassOfAxiom("X", createPrimitiveObjMinCard("n1", true, "n2", "Ro", "Y"));
		RuleString rule6_2 = new RuleString("6.2", "ObjExt", conclusion, premise1);
		
		// 6.3
		premise1 = getSubClassOfAxiom("X", createPrimitiveObjExactCard("n", false, "0", "Ro", "Y"));
		conclusion = getSubClassOfAxiom("X", createPrimitiveObjMaxCard("n", false, "0", "Ro", "Y"));
		RuleString rule6_3 = new RuleString("6.3", "ObjExt", conclusion, premise1);

		
		
		
		// Rule 8
		premise1 = getSubClassOfAxiom("T", "X");
		conclusion = getSubClassOfAxiom("Y", "X");
		RuleString rule8 = new RuleString("8", "Top", conclusion, premise1);
		
		
		
		// Rule 9
		premise1 = getSubClassOfAxiom("X", "F");
		conclusion = getSubClassOfAxiom("X", "Y");
		RuleString rule9 = new RuleString("9", "Bot", conclusion, premise1);

		
		
		// Rule 10
		premise1 = getSubClassOfAxiom("X", new InterUnionComp(ClassExpressionType.OBJECT_INTERSECTION_OF, new AtomicCls("X")));
		conclusion = getSubClassOfAxiom("X", "F");
		RuleString rule10 = new RuleString("10", "ObjCom-1", conclusion, premise1);

		
		
		
		// Rule 12
		
		// 12.1
		premise1 = getSubClassOfAxiom("X", getPrimitiveDataSomeValFrom("Rd", "Dr0"));
		premise2 = createPrimitiveDataRangeProp("Rd", "Dr1");
		conclusion = getSubClassOfAxiom("X", "F");
		RuleString rule12_1 = new RuleString("12.1", "DatSom-DatRng", conclusion, premise1, premise2);
		
		
		// 12.2
		tmp = new ExistsOrForAll(ClassExpressionType.OBJECT_SOME_VALUES_FROM, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), getPrimitiveDataSomeValFrom("Rd", "Dr0"));
		premise1 = getSubClassOfAxiom("X", (ClsExpStr) tmp);		
		RuleString rule12_2 = new RuleString("12.2", "DatSom-DatRng", conclusion, premise1, premise2);
		
		
		
		// Rule 13
		
		// 13.1
		premise1 = getSubClassOfAxiom("X", createPrimitiveDataMinCard("n", false, "0", "Rd", "Dr0"));	
		premise2 = createPrimitiveDataRangeProp("Rd", "Dr1");
		conclusion = getSubClassOfAxiom("X", "F");
		RuleString rule13_1 = new RuleString("13.1", "DatMin-DatRng", conclusion, premise1, premise2);

		
		// 13.2
		tmp = new ExistsOrForAll(ClassExpressionType.OBJECT_SOME_VALUES_FROM, 
				new EntityStr("Ro", EntityType.OBJECT_PROPERTY), 
				createPrimitiveDataMinCard("n", false, "0", "Rd", "Dr0"));
		
		premise1 = getSubClassOfAxiom("X", (ClsExpStr) tmp);		
		RuleString rule13_2 = new RuleString("13.2", "DatMin-DatRng", conclusion, premise1, premise2);
		
		
		
		
		
		
		
		// Add all of the rules.
		rules.get(2).add(rule51);

	}

	
	
	private static OWLAxiomStr createPrimitiveDataRangeProp(String property, String dataRange) {		
		return new OWLAxiomStr(AxiomType.DATA_PROPERTY_RANGE, 
				new EntityStr(property, EntityType.DATA_PROPERTY), 
				new TemplateDataRange(dataRange));
	}
	
	private static OWLAxiomStr createPrimitiveTransObjProp(String property) {
		return new OWLAxiomStr(AxiomType.TRANSITIVE_OBJECT_PROPERTY, new EntityStr(property, EntityType.OBJECT_PROPERTY));
	}
	
	private static OWLAxiomStr createPrimitiveObjDomain(String property, String clsExp) {	
		return new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_DOMAIN, new EntityStr(property, EntityType.OBJECT_PROPERTY), new AtomicCls(clsExp));		
	}
	private static OWLAxiomStr getSubObjectPropertyOf(String property1, String property2) {		
		return new OWLAxiomStr(AxiomType.SUB_OBJECT_PROPERTY, new AtomicCls(property1), new AtomicCls(property2));	
	}
	
	private static ExistsOrForAll getPrimitiveDataSomeValFrom(String property, String dataRange) {
		return new ExistsOrForAll(ClassExpressionType.DATA_SOME_VALUES_FROM, 
				new EntityStr(property, EntityType.DATA_PROPERTY), new TemplateDataRange(dataRange));
	}
	
	
	private static ExistsOrForAll getPrimitiveObjSomeValFrom(String property, String cls) {
		return new ExistsOrForAll(ClassExpressionType.OBJECT_SOME_VALUES_FROM, 
				new EntityStr(property, EntityType.OBJECT_PROPERTY), new AtomicCls(cls));
	}
	
	private static ExistsOrForAll createPrimitiveObjAllValFrom(String property, String cls) {
		return new ExistsOrForAll(ClassExpressionType.OBJECT_ALL_VALUES_FROM, 
				new EntityStr(property, EntityType.OBJECT_PROPERTY), new AtomicCls(cls));
	}
		
	
	
	private static CardExpStr createPrimitiveDataExactCard(String cardinality, boolean isRelativeBound, String lowerBound, String property, String expression) {
		return createObjCardExp(ClassExpressionType.DATA_EXACT_CARDINALITY, cardinality, 
				isRelativeBound, lowerBound, property, expression);
	}
	
	
	private static CardExpStr createPrimitiveDataMaxCard(String cardinality, boolean isRelativeBound, String lowerBound, String property, String expression) {
		return createObjCardExp(ClassExpressionType.DATA_MAX_CARDINALITY, cardinality, 
				isRelativeBound, lowerBound, property, expression);
	}
	
	private static CardExpStr createPrimitiveDataMinCard(String cardinality, boolean isRelativeBound, String lowerBound, String property, String expression) {
		return createDataCardExp(ClassExpressionType.DATA_MIN_CARDINALITY, cardinality, 
				isRelativeBound, lowerBound, property, expression);
	}
	
	
	
	private static CardExpStr createDataCardExp(ClassExpressionType expType, String cardinality, boolean isRelativeBound, String lowerBound, String property, String expression) {
		return new CardExpStr(expType, cardinality, 
				isRelativeBound, lowerBound, new EntityStr(property, EntityType.DATA_PROPERTY), new TemplateDataRange(expression));
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

	
	private static void getDomainAxiomStr() {
		// Fill in and change return type
	}
	
	

	private static GenericExpStr createExpression(GenericExpStr...expStrs) {


		return null;
	}
	

}
