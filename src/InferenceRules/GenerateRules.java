package InferenceRules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.semanticweb.owlapi.model.AxiomType;

import CorpusAnalysis.TopBottomEntityCounter;
import OWLExpressionTemplates.AtomicCls;
import OWLExpressionTemplates.CardExpGen;
import OWLExpressionTemplates.ClsExpStr;
import OWLExpressionTemplates.ComplementCls;
import OWLExpressionTemplates.TemplateDataRange;
import OWLExpressionTemplates.TemplateLiteral;
import OWLExpressionTemplates.TemplateObjectProperty;
import OWLExpressionTemplates.ExistsOrForAll;
import OWLExpressionTemplates.ExpressionGroup;
import OWLExpressionTemplates.InterUnion;
import OWLExpressionTemplates.OWLAxiomStr;
import OWLExpressionTemplates.SubClassStr;
import OWLExpressionTemplates.TemplateDataProperty;
import RuleRestrictions.AbsCardinalityRestriction;
import RuleRestrictions.CardinalitySign;
import RuleRestrictions.DisjointDatatypesRestriction;
import RuleRestrictions.GroupContainsRestriction;
import RuleRestrictions.RelCardinalityRestriction;
import RuleRestrictions.RuleRestriction;
import RuleRestrictions.RuleRestrictions;
import RuleRestrictions.SubSetRestriction;
import RuleRestrictions.UnequalLiteralsRestriction;

public class GenerateRules {

	private static Map<Integer, List<RuleString>> rules = null;

	public static Map<Integer, List<RuleString>> getRules() {

		if (rules == null) {
			generateRules();
		} 
		return rules;
	}
	
	
	public static RuleString getRule(String ruleID) {
		
		getRules();
		
		for (Integer i : rules.keySet()) {
			for (RuleString rule : rules.get(i)) {
				if (rule.getRuleID().equals(ruleID)) {
					return rule;
				}
			}
		}		
		return null;
	}


	private static void generateRules() {
		
		rules = new HashMap<Integer, List<RuleString>>();

		for (int i = 1; i <= 4; i++) {			
			rules.put(i, new ArrayList<RuleString>());			
		}
		generateOnePremiseRules();
		generateTwoPremiseRules();
		generateThreePremiseRules();
		generateFourPremiseRules();
	}

	
	
	private static OWLAxiomStr createPrimitiveDataRangeProp(String property, String dataRange) {		
		return new OWLAxiomStr(AxiomType.DATA_PROPERTY_RANGE, 
				new TemplateDataProperty(property), 
				new TemplateDataRange(dataRange));
	}

	
	private static OWLAxiomStr createPrimitiveTransObjProp(String property) {
		return new OWLAxiomStr(AxiomType.TRANSITIVE_OBJECT_PROPERTY, new TemplateObjectProperty(property));
	}
	
	private static OWLAxiomStr createPrimitiveObjDomain(String property, String clsExp) {	
		return new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_DOMAIN, new TemplateObjectProperty(property), new AtomicCls(clsExp));		
	}
	private static OWLAxiomStr getSubObjectPropertyOf(String property1, String property2) {		
		return new OWLAxiomStr(AxiomType.SUB_OBJECT_PROPERTY, new TemplateObjectProperty(property1), new TemplateObjectProperty(property2));	
	}
	



	
	
	private static void generateOnePremiseRules() {
		
		// Rule 1
		ExpressionGroup tmpGroup1 = new ExpressionGroup("C1", new ClsExpStr[] { new AtomicCls("X"), new AtomicCls("Y")}, "Z");
		OWLAxiomStr premise1 = new OWLAxiomStr(AxiomType.EQUIVALENT_CLASSES, tmpGroup1);
		OWLAxiomStr conclusion = new SubClassStr("X", "Y");
		RuleString rule1 = new RuleString("1", "EquCls", conclusion, premise1);

		
	
		// Rule 2		
		// 2.1
		ExpressionGroup anonDisj1 = new ExpressionGroup("C1", new ClsExpStr[] {}, "Y1");	
		ExpressionGroup anonDisj2 = new ExpressionGroup("C2", new ClsExpStr[] {}, "Y2");	
		
		ClsExpStr tmp = InterUnion.createIntersectionExpression(anonDisj1);						
		premise1 = new OWLAxiomStr(AxiomType.EQUIVALENT_CLASSES, new ExpressionGroup("C3", new ClsExpStr[] { new AtomicCls("X"), tmp}));
		conclusion =  new SubClassStr("X", InterUnion.createIntersectionExpression(anonDisj2));
		RuleRestrictions restrictions = new RuleRestrictions(new RuleRestriction[] {}, new RuleRestriction[] {new SubSetRestriction("Y2", "Y1")});		
		RuleString rule2_1 = new RuleString("2.1", "ObjInt-1", restrictions, conclusion, premise1);

		// 2.2 - Extra rule to cater for single-class case.
		conclusion =  new SubClassStr("X", "Y");
		restrictions = new RuleRestrictions(new RuleRestriction[] {}, new RuleRestriction[] {new GroupContainsRestriction("Y", "Y1")});		
		RuleString rule2_2 = new RuleString("2.2", "ObjInt-1", restrictions, conclusion, premise1);

		

		// 2.3
		anonDisj1 = new ExpressionGroup("C1", new ClsExpStr[] {}, "Y1");	
		anonDisj2 = new ExpressionGroup("C2", new ClsExpStr[] {}, "Y2");	
		
		tmp = ExistsOrForAll.createObjSomeValFrom("Ro", InterUnion.createIntersectionExpression(anonDisj1));						
		tmpGroup1 = new ExpressionGroup("C3", new ClsExpStr[] { new AtomicCls("X"), tmp});
		premise1 = new OWLAxiomStr(AxiomType.EQUIVALENT_CLASSES, tmpGroup1);
		
		tmp = ExistsOrForAll.createObjSomeValFrom("Ro", InterUnion.createIntersectionExpression( anonDisj2));						
		conclusion = new SubClassStr("X", (ClsExpStr) tmp);
		restrictions = new RuleRestrictions(new RuleRestriction[] {}, new RuleRestriction[] {new SubSetRestriction("Y2", "Y1")});		
		RuleString rule2_3 = new RuleString("2.3", "ObjInt-1", restrictions, conclusion, premise1);

		
		// 2.4 - Extra rule to cater for single-class case.
		tmp = ExistsOrForAll.createObjSomeValFrom("Ro", new AtomicCls("Y"));						
		conclusion = new SubClassStr("X", (ClsExpStr) tmp);
		restrictions = new RuleRestrictions(new RuleRestriction[] {}, new RuleRestriction[] {new GroupContainsRestriction("Y", "Y1")});		
		RuleString rule2_4 = new RuleString("2.4", "ObjInt-1", restrictions, conclusion, premise1);

		

		
		// Rule 3
		
		// 3.1
		ExpressionGroup anonConj = new ExpressionGroup("C1", new ClsExpStr[] {}, "Y1");	
		premise1 = new SubClassStr("X", InterUnion.createIntersectionExpression(anonConj));							
		anonConj = new ExpressionGroup("C2", new ClsExpStr[] {}, "Y2");				
		conclusion =  new SubClassStr("X", InterUnion.createIntersectionExpression(anonConj));
		restrictions = new RuleRestrictions(new RuleRestriction[] {}, new RuleRestriction[] {new SubSetRestriction("Y2", "Y1")});		
		RuleString rule3_1 = new RuleString("3.1", "ObjInt-2", restrictions, conclusion, premise1);
	
		// 3.2 - Extra rule to cater for single-class case.			
		conclusion =  new SubClassStr("X", "Y");
		restrictions = new RuleRestrictions(new RuleRestriction[] {}, new RuleRestriction[] {new GroupContainsRestriction("Y", "Y1")});		
		RuleString rule3_2 = new RuleString("3.2", "ObjInt-2", restrictions, conclusion, premise1);

		// 3.3
		anonConj = new ExpressionGroup("C1", new ClsExpStr[] {}, "Y1");	
		tmp = ExistsOrForAll.createObjSomeValFrom("Ro", InterUnion.createIntersectionExpression(anonConj));
		premise1 = new SubClassStr("X", (ClsExpStr) tmp);
		
		anonConj = new ExpressionGroup("C2", new ClsExpStr[] {}, "Y2");
		tmp = ExistsOrForAll.createObjSomeValFrom("Ro", InterUnion.createIntersectionExpression(anonConj));
		conclusion =  new SubClassStr("X", (ClsExpStr) tmp);
		restrictions = new RuleRestrictions(new RuleRestriction[] {}, new RuleRestriction[] {new SubSetRestriction("Y2", "Y1")});		
		RuleString rule3_3 = new RuleString("3.3", "ObjInt-2", restrictions, conclusion, premise1);
	
		// 3.4 - Extra rule to cater for single-class case.
		tmp = ExistsOrForAll.createObjSomeValFrom("Ro", new AtomicCls("Y"));						
		conclusion = new SubClassStr("X", (ClsExpStr) tmp);
		restrictions = new RuleRestrictions(new RuleRestriction[] {}, new RuleRestriction[] {new GroupContainsRestriction("Y", "Y1")});		
		RuleString rule3_4 = new RuleString("3.4", "ObjInt-1", restrictions, conclusion, premise1);
		
		

		// Rule 4
		
		// 4.1
		anonDisj1 = new ExpressionGroup("C1", new ClsExpStr[] {}, "Y1");	
		anonDisj2 = new ExpressionGroup("C2", new ClsExpStr[] {}, "Y2");	
		
		tmp = InterUnion.createUnionExpression( anonDisj1);							
		tmpGroup1 = new ExpressionGroup("C3", new ClsExpStr[] { new AtomicCls("X"), tmp});
		premise1 = new OWLAxiomStr(AxiomType.EQUIVALENT_CLASSES, tmpGroup1);
		
		conclusion =  new SubClassStr(InterUnion.createUnionExpression(anonDisj2), "X");
		restrictions = new RuleRestrictions(new RuleRestriction[] {}, new RuleRestriction[] {new SubSetRestriction("Y2", "Y1")});		
		RuleString rule4_1 = new RuleString("4.1", "ObjUni-1", restrictions, conclusion, premise1);

		
		// 4.2 - Extra rule to cater for single-class case.
		conclusion =  new SubClassStr("Y", "X");
		restrictions = new RuleRestrictions(new RuleRestriction[] {}, new RuleRestriction[] {new GroupContainsRestriction("Y", "Y1")});		
		RuleString rule4_2 = new RuleString("4.2", "ObjUni-1", restrictions, conclusion, premise1);

		
		
		// 4.3
		anonDisj1 = new ExpressionGroup("C1", new ClsExpStr[] {}, "Y1");	
		anonDisj2 = new ExpressionGroup("C2", new ClsExpStr[] {}, "Y2");	
		
		tmp = ExistsOrForAll.createObjSomeValFrom("Ro", InterUnion.createUnionExpression(anonDisj1));						
		tmpGroup1 = new ExpressionGroup("C3", new ClsExpStr[] { new AtomicCls("X"), tmp});
		premise1 = new OWLAxiomStr(AxiomType.EQUIVALENT_CLASSES, tmpGroup1);
		
		tmp = ExistsOrForAll.createObjSomeValFrom("Ro", InterUnion.createUnionExpression(anonDisj2));						
		conclusion = new SubClassStr((ClsExpStr) tmp, "X");
		restrictions = new RuleRestrictions(new RuleRestriction[] {}, new RuleRestriction[] {new SubSetRestriction("Y2", "Y1")});		
		RuleString rule4_3 = new RuleString("4.3", "ObjUni-1", restrictions, conclusion, premise1);

		
		// 4.4 - Extra rule to cater for single-class case.
		tmp = ExistsOrForAll.createObjSomeValFrom("Ro", new AtomicCls("Y"));						
		conclusion = new SubClassStr((ClsExpStr) tmp, "X");
		restrictions = new RuleRestrictions(new RuleRestriction[] {}, new RuleRestriction[] {new GroupContainsRestriction("Y", "Y1")});		
		RuleString rule4_4 = new RuleString("4.4", "ObjUni-1", restrictions, conclusion, premise1);


		
		// Rule 5
		
		// 5.1
		anonDisj1 = new ExpressionGroup("C1", new ClsExpStr[] {}, "Y1");
		anonDisj2 = new ExpressionGroup("C2", new ClsExpStr[] {}, "Y2");
		premise1 = new SubClassStr(InterUnion.createUnionExpression(anonDisj1), "X");			
		conclusion =  new SubClassStr(InterUnion.createUnionExpression(anonDisj2), "X");
		restrictions = new RuleRestrictions(new RuleRestriction[] {}, new RuleRestriction[] {new SubSetRestriction("Y2", "Y1")});		
		RuleString rule5_1 = new RuleString("5.1", "ObjUni-2", restrictions, conclusion, premise1);
	
		// 5.2 - Extra rule to cater for single-class case.
		conclusion =  new SubClassStr("Y", "X");
		restrictions = new RuleRestrictions(new RuleRestriction[] {}, new RuleRestriction[] {new GroupContainsRestriction("Y", "Y1")});		
		RuleString rule5_2 = new RuleString("5.2", "ObjUni-1", restrictions, conclusion, premise1);

	
		// 5.3
		tmp = ExistsOrForAll.createObjSomeValFrom("Ro", InterUnion.createUnionExpression(anonDisj1));		
		premise1 = new SubClassStr((ClsExpStr) tmp, "X");

		tmp = ExistsOrForAll.createObjSomeValFrom("Ro", InterUnion.createUnionExpression(anonDisj2));
		conclusion =  new SubClassStr((ClsExpStr) tmp, "X");
		restrictions = new RuleRestrictions(new RuleRestriction[] {}, new RuleRestriction[] {new SubSetRestriction("Y2", "Y1")});		
		RuleString rule5_3 = new RuleString("5.3", "ObjUni-2", restrictions, conclusion, premise1);
	
		// 5.4 - Extra rule to cater for single-class case.
		tmp = ExistsOrForAll.createObjSomeValFrom("Ro", new AtomicCls("Y"));						
		conclusion = new SubClassStr((ClsExpStr) tmp, "X");
		restrictions = new RuleRestrictions(new RuleRestriction[] {}, new RuleRestriction[] {new GroupContainsRestriction("Y", "Y1")});		
		RuleString rule5_4 = new RuleString("5.4", "ObjUni-1", restrictions, conclusion, premise1);


		
		// Rule 6
		
		// 6.1
		premise1 = new SubClassStr("X", CardExpGen.createObjExactCard("n1", "Ro", "Y"));
		conclusion = new SubClassStr("X", CardExpGen.createObjMinCard("n2", "Ro", "Y"));
		restrictions = new RuleRestrictions(
				new RuleRestriction[] { new AbsCardinalityRestriction("n1", CardinalitySign.GEQ, 0) },
				new RuleRestriction[] { new AbsCardinalityRestriction("n2", CardinalitySign.GEQ, 0), 
									    new RelCardinalityRestriction("n2", CardinalitySign.L, "n1") });
		RuleString rule6_1 = new RuleString("6.1", "ObjExt", restrictions, conclusion, premise1);

		// 6.2
		premise1 = new SubClassStr("X", CardExpGen.createObjExactCard("n", "Ro", "Y"));
		conclusion = new SubClassStr("X", CardExpGen.createObjMaxCard("n", "Ro", "Y"));
		restrictions = new RuleRestrictions(new AbsCardinalityRestriction("n", CardinalitySign.GEQ, 0));
		RuleString rule6_2 = new RuleString("6.2", "ObjExt", restrictions, conclusion, premise1);
		
		// 6.3
		premise1 = new SubClassStr("X", CardExpGen.createObjMinCard("n1", "Ro", "Y"));
		conclusion = new SubClassStr("X", CardExpGen.createObjMinCard("n2", "Ro", "Y"));
		restrictions = new RuleRestrictions(
				new RuleRestriction[] { new AbsCardinalityRestriction("n1", CardinalitySign.GEQ, 0) },
				new RuleRestriction[] { new AbsCardinalityRestriction("n2", CardinalitySign.GEQ, 0), 
										new RelCardinalityRestriction("n2",CardinalitySign.L, "n1") });
		RuleString rule6_3 = new RuleString("6.3", "ObjExt", restrictions, conclusion, premise1);
	

		
		// Rule 7
		tmpGroup1 = new ExpressionGroup("C1", new ClsExpStr[] { new AtomicCls("X"), ExistsOrForAll.createObjAllValFrom("Ro", "Y")});
		premise1 = new OWLAxiomStr(AxiomType.EQUIVALENT_CLASSES, tmpGroup1);
		conclusion = new SubClassStr(ExistsOrForAll.createObjAllValFrom("Ro", "F"), "X");
		RuleString rule7 = new RuleString("7", "ObjAll", conclusion, premise1);
		
		
		// Rule 8
		premise1 = new SubClassStr("T", "X");
		conclusion = new SubClassStr("Y", "X");
		RuleString rule8 = new RuleString("8", "Top", conclusion, premise1);
		
				
		// Rule 9
		premise1 = new SubClassStr("X", "F");
		conclusion = new SubClassStr("X", "Y");
		RuleString rule9 = new RuleString("9", "Bot", conclusion, premise1);

				
		// Rule 10
		premise1 = new SubClassStr("X", new ComplementCls(new AtomicCls("X")));
		conclusion = new SubClassStr("X", "F");
		RuleString rule10 = new RuleString("10", "ObjCom-1", conclusion, premise1);

		
		// Rule 11
		premise1 = new SubClassStr(new ComplementCls(new AtomicCls("X")), "Y");	
		tmp = InterUnion.createUnionExpression(
				new ExpressionGroup("C1", new ClsExpStr[] { new AtomicCls("X"), new AtomicCls("Y")}));	
		
		conclusion = new SubClassStr("T", (ClsExpStr) tmp);
		RuleString rule11 = new RuleString("11", "ObjCom-2", conclusion, premise1);

		
		
	
		rules.get(1).add(rule1);
		rules.get(1).add(rule2_1);
		rules.get(1).add(rule2_2);
		rules.get(1).add(rule2_3);
		rules.get(1).add(rule2_4);
		rules.get(1).add(rule3_1);
		rules.get(1).add(rule3_2);	
		rules.get(1).add(rule3_3);
		rules.get(1).add(rule3_4);
		rules.get(1).add(rule4_1);
		rules.get(1).add(rule4_2);
		rules.get(1).add(rule4_3);
		rules.get(1).add(rule4_4);
		rules.get(1).add(rule5_1);
		rules.get(1).add(rule5_2);
		rules.get(1).add(rule5_3);
		rules.get(1).add(rule5_4);
		rules.get(1).add(rule6_1);
		rules.get(1).add(rule6_2);
		rules.get(1).add(rule6_3);
		rules.get(1).add(rule7);
		rules.get(1).add(rule8);
		rules.get(1).add(rule9);
		rules.get(1).add(rule10);
		rules.get(1).add(rule11);
	}
	
	
	
	private static void generateTwoPremiseRules() {
		
		// Rule 12		
		// 12.1
		OWLAxiomStr premise1 = new SubClassStr("X", ExistsOrForAll.createDataSomeValFrom("Rd", "Dr0"));
		OWLAxiomStr premise2 = createPrimitiveDataRangeProp("Rd", "Dr1");
		RuleRestrictions restrictions = new RuleRestrictions(new DisjointDatatypesRestriction("Dr0", "Dr1"));
		OWLAxiomStr conclusion = new SubClassStr("X", "F");
		RuleString rule12_1 = new RuleString("12.1", "DatSom-DatRng", restrictions, conclusion, premise1, premise2);
		
		
		// 12.2
		ClsExpStr tmp = ExistsOrForAll.createObjSomeValFrom("Ro", ExistsOrForAll.createDataSomeValFrom("Rd", "Dr0"));
		premise1 = new SubClassStr("X", (ClsExpStr) tmp);		
		restrictions = new RuleRestrictions(new DisjointDatatypesRestriction("Dr0", "Dr1"));
		RuleString rule12_2 = new RuleString("12.2", "DatSom-DatRng", restrictions, conclusion, premise1, premise2);
		
				
		// Rule 13		
		// 13.1
		premise1 = new SubClassStr("X", CardExpGen.createDataMinCard("n", "Rd", "Dr0"));	
		premise2 = createPrimitiveDataRangeProp("Rd", "Dr1");
		restrictions = new RuleRestrictions(new DisjointDatatypesRestriction("Dr0", "Dr1"),
				      new AbsCardinalityRestriction("n", CardinalitySign.G, 0));
		conclusion = new SubClassStr("X", "F");
		RuleString rule13_1 = new RuleString("13.1", "DatMin-DatRng", restrictions, conclusion, premise1, premise2);
		
		// 13.2
		tmp = ExistsOrForAll.createObjSomeValFrom("Ro", CardExpGen.createDataMinCard("n", "Rd", "Dr0"));	
		premise1 = new SubClassStr("X", (ClsExpStr) tmp);		
		RuleString rule13_2 = new RuleString("13.2", "DatMin-DatRng", restrictions, conclusion, premise1, premise2);
		
		
		// Rule 14		
		// 14.1
		premise1 = new SubClassStr("X", ExistsOrForAll.createLiteralSomeValFrom("Rd", "l0"));
		premise2 = createPrimitiveDataRangeProp("Rd", "Dr");
		restrictions = new RuleRestrictions(new DisjointDatatypesRestriction("l0", "Dr"));
		conclusion = new SubClassStr("X", "F");
		RuleString rule14_1 = new RuleString("14.1", "DatVal-DatRng", restrictions, conclusion, premise1, premise2);
		
		
		// 14.2
		tmp = ExistsOrForAll.createObjSomeValFrom("Ro", ExistsOrForAll.createLiteralSomeValFrom("Rd", "l0"));
		premise1 = new SubClassStr("X", (ClsExpStr) tmp);		
		RuleString rule14_2 = new RuleString("14.2", "DatVal-DatRng", restrictions, conclusion, premise1, premise2);


		// Rule 15
		ExpressionGroup tmpGroup1 = new ExpressionGroup("C1", new ClsExpStr[] { new AtomicCls("X"), new AtomicCls("Y") }, "Z");
		premise1 = new SubClassStr("X", "Y");
		premise2 = new OWLAxiomStr(AxiomType.DISJOINT_CLASSES, tmpGroup1);
		conclusion = new SubClassStr("X", "F");
		RuleString rule15 = new RuleString("15", "SubCls-DisCls", conclusion, premise1, premise2);

				
		// Rule 16
		tmpGroup1 = new ExpressionGroup("C1", new ClsExpStr[] { new AtomicCls("X"), new AtomicCls("Y") }, "Z");
		premise1 = new SubClassStr("T", "Y");
		premise2 = new OWLAxiomStr(AxiomType.DISJOINT_CLASSES, tmpGroup1);
		conclusion = new SubClassStr("X", "F");
		RuleString rule16 = new RuleString("16", "Top-DisCls", conclusion, premise1, premise2);

	
		// Rule 17		
		// 17.1
		premise1 = new SubClassStr("X", CardExpGen.createObjMinCard("n1", "Ro", "Y"));
		premise2 = new SubClassStr("X", CardExpGen.createObjMaxCard("n2", "Ro", "Y"));
		restrictions = new RuleRestrictions( 
				new RelCardinalityRestriction("n1", CardinalitySign.G, "n2"), 
				new AbsCardinalityRestriction("n2", CardinalitySign.GEQ,  0));
		conclusion = new SubClassStr("X", "F");
		RuleString rule17_1 = new RuleString("17.1", "ObjMin-ObjMax", restrictions, conclusion, premise1, premise2);
				
		// 17.2
		premise1 = new SubClassStr("X", CardExpGen.createObjExactCard("n1", "Ro", "Y"));
		RuleString rule17_2 = new RuleString("17.2", "ObjMin-ObjMax", restrictions, conclusion, premise1, premise2);
		
				
		// Rule 18
		
		// 18.1
		premise1 = new SubClassStr("X", CardExpGen.createObjMinCard("n", "Ro", "Y"));
		premise2 = new OWLAxiomStr(AxiomType.FUNCTIONAL_OBJECT_PROPERTY, new TemplateObjectProperty("Ro"));
		restrictions = new RuleRestrictions(new AbsCardinalityRestriction("n", CardinalitySign.G, 1));	
		conclusion = new SubClassStr("X", "F");
		RuleString rule18_1 = new RuleString("18.1", "ObjMin-ObjFun", restrictions, conclusion, premise1, premise2);

		// 18.2
		premise1 = new SubClassStr("X", CardExpGen.createObjExactCard("n", "Ro", "Y"));
		RuleString rule18_2 = new RuleString("18.2", "ObjMin-ObjFun", restrictions, conclusion, premise1, premise2);

		
		
		// Rule 19
		
		// 19.1
		premise1 = new SubClassStr("X", CardExpGen.createDataMinCard("n", "Rd", "Dr"));
		premise2 = new OWLAxiomStr(AxiomType.FUNCTIONAL_DATA_PROPERTY, new TemplateDataRange("Rd"));
		restrictions = new RuleRestrictions(new AbsCardinalityRestriction("n", CardinalitySign.G, 1));	
		conclusion = new SubClassStr("X", "F");
		RuleString rule19_1 = new RuleString("19.1", "DatMin-DatFun", restrictions, conclusion, premise1, premise2);

		// 19.2
		premise1 = new SubClassStr("X", CardExpGen.createDataExactCard("n", "Rd", "Dr"));
		RuleString rule19_2 = new RuleString("19.2", "DatMin-DatFun", restrictions, conclusion, premise1, premise2);

		
		// Rule 20
		
		// 20.1
		premise1 = new SubClassStr("X", ExistsOrForAll.createObjSomeValFrom("Ro", "Y"));
		premise2 = new SubClassStr("Y", "F");
		conclusion = new SubClassStr("X", "F");
		RuleString rule20_1 = new RuleString("20.1", "ObjSom-Bot-1", conclusion, premise1, premise2);
		
		// 20.2
		premise1 = new SubClassStr("X", CardExpGen.createObjMinCard("n", "Ro", "Y"));
		restrictions = new RuleRestrictions(new AbsCardinalityRestriction("n", CardinalitySign.G, 0));	
		RuleString rule20_2 = new RuleString("20.2", "ObjSom-Bot-1", restrictions, conclusion, premise1, premise2);

		// 20.3
		premise1 = new SubClassStr("X", CardExpGen.createObjExactCard("n", "Ro", "Y"));
		RuleString rule20_3 = new RuleString("20.3", "ObjSom-Bot-1", restrictions, conclusion, premise1, premise2);

			
		/// Rule 21		
		// 21.1
		tmpGroup1 = new ExpressionGroup("C1", new ClsExpStr[] { new AtomicCls("Y") }, "Z");
		tmp = InterUnion.createIntersectionExpression( tmpGroup1);		
		premise1 = new SubClassStr("X", ExistsOrForAll.createObjSomeValFrom("Ro", (ClsExpStr) tmp));
		premise2 = new SubClassStr("Y", "F");
		conclusion = new SubClassStr("X", "F");
		RuleString rule21_1 = new RuleString("21.1", "ObjSom-Bot-2", conclusion, premise2, premise1);
		
		// 21.2
		premise1 = new SubClassStr("X", CardExpGen.createObjMinCard("n", "Ro", (ClsExpStr) tmp));
		restrictions = new RuleRestrictions(new AbsCardinalityRestriction("n", CardinalitySign.G, 0));	
		RuleString rule21_2 = new RuleString("21.2", "ObjSom-Bot-2", restrictions, conclusion, premise2, premise1);
		
		// 21.3
		premise1 = new SubClassStr("X", CardExpGen.createObjExactCard("n", "Ro", (ClsExpStr) tmp));
		RuleString rule21_3 = new RuleString("21.3", "ObjSom-Bot-2", restrictions, conclusion, premise2, premise1);

				
		// Rule 22
		
		// 22.1
		tmpGroup1 = new ExpressionGroup("C1", new ClsExpStr[] {}, "Y1");
		premise1 = new SubClassStr("X", ExistsOrForAll.createObjSomeValFrom("Ro", InterUnion.createIntersectionExpression( tmpGroup1)));
		ExpressionGroup tmpGroup2 = new ExpressionGroup("C2", new ClsExpStr[] {}, "Y2");
		premise2 = new OWLAxiomStr(AxiomType.DISJOINT_CLASSES, tmpGroup2);
		restrictions = new RuleRestrictions(new SubSetRestriction("Y1", "Y2"));	
		conclusion = new SubClassStr("X", "F");
		RuleString rule22_1 = new RuleString("22.1", "ObjInt-DisCls", restrictions, conclusion, premise1, premise2);
		
				
		// 22.2
		premise1 = new SubClassStr("X", CardExpGen.createObjMinCard("n", "Ro", InterUnion.createIntersectionExpression( tmpGroup1)));		
		restrictions = new RuleRestrictions(new SubSetRestriction("Y1", "Y2"), new AbsCardinalityRestriction("n", CardinalitySign.G, 0));	
		RuleString rule22_2 = new RuleString("22.2", "ObjInt-DisCls", restrictions, conclusion, premise1, premise2);

		
		// 22.3
		premise1 = new SubClassStr("X", CardExpGen.createObjExactCard("n", "Ro", InterUnion.createIntersectionExpression( tmpGroup1)));	
		RuleString rule22_3 = new RuleString("22.3", "ObjInt-DisCls", restrictions, conclusion, premise1, premise2);

		
		// Rule 23
		premise1 = new SubClassStr("X", "Y");
		premise2 = new SubClassStr("X", new ComplementCls(new AtomicCls("Y")));
		conclusion =  new SubClassStr("X", "F");
		RuleString rule23 = new RuleString("23", "SubCls-ObjCom-1", conclusion, premise1, premise2);
		
			
		// Rule 24
		premise1 = new SubClassStr("X", "Y");
		premise2 = new SubClassStr(new ComplementCls(new AtomicCls("X")), "Y");
		conclusion =  new SubClassStr("T", "Y");
		RuleString rule24 = new RuleString("24", "SubCls-ObjCom-2", conclusion, premise1, premise2);
		
		
		// Rule 25		
		// 25.1
		premise1 = createPrimitiveObjDomain("Ro", "X");
		premise2 = new SubClassStr(ExistsOrForAll.createObjAllValFrom("Ro", "F"), "X");
		conclusion =  new SubClassStr("T", "X");
		RuleString rule25_1 = new RuleString("25.1", "ObjDom-ObjAll", conclusion, premise1, premise2);
		
		// 25.2
		premise1 = new SubClassStr(ExistsOrForAll.createObjSomeValFrom("Ro", "T"), "X");		
		RuleString rule25_2 = new RuleString("25.2", "ObjDom-ObjAll", conclusion, premise1, premise2);

	
		
		// Rule 26
		premise1 = new OWLAxiomStr(AxiomType.SUB_OBJECT_PROPERTY, 
				new TemplateObjectProperty("Ro"), 
				new TemplateObjectProperty("So"));

		premise2 = new OWLAxiomStr(AxiomType.SUB_OBJECT_PROPERTY, 
				new TemplateObjectProperty("So"), 
				new TemplateObjectProperty("To"));

		conclusion = new OWLAxiomStr(AxiomType.SUB_OBJECT_PROPERTY, 
				new TemplateObjectProperty("Ro"), 
				new TemplateObjectProperty("To"));

		RuleString rule26 = new RuleString("26", "SubObj-SubObj", conclusion, premise1, premise2);


		// Rule 27
		premise1 = new OWLAxiomStr(AxiomType.TRANSITIVE_OBJECT_PROPERTY, new TemplateObjectProperty("Ro"));
				
		premise2 = new OWLAxiomStr(AxiomType.INVERSE_OBJECT_PROPERTIES, 
				new TemplateObjectProperty("Ro"), 
				new TemplateObjectProperty("So"));

		conclusion = new OWLAxiomStr(AxiomType.TRANSITIVE_OBJECT_PROPERTY, 
				new TemplateObjectProperty("So"));

		RuleString rule27 = new RuleString("27", "ObjTra-ObjInv", conclusion, premise1, premise2);


		// Rule 28
		premise1 = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_DOMAIN, new TemplateObjectProperty("Ro"), new AtomicCls("X"));
		premise2 = new SubClassStr("X", "Y");
		conclusion = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_DOMAIN, new TemplateObjectProperty("Ro"), new AtomicCls("Y"));				
		RuleString rule28 = new RuleString("28", "ObjDom-SubCls", conclusion, premise1, premise2);


		// Rule 29
		premise1 = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_DOMAIN, new TemplateObjectProperty("Ro"), new AtomicCls("X"));				
		premise2 = new OWLAxiomStr(AxiomType.SUB_OBJECT_PROPERTY, new TemplateObjectProperty("So"), new TemplateObjectProperty("Ro"));
		conclusion = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_DOMAIN, new TemplateObjectProperty("So"), new AtomicCls("X"));
		RuleString rule29 = new RuleString("29", "ObjDom-SubObj", conclusion, premise1, premise2);

		
		// Rule 30
		premise1 = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_RANGE, new TemplateObjectProperty("Ro"), new AtomicCls("X"));				
		premise2 = new OWLAxiomStr(AxiomType.INVERSE_OBJECT_PROPERTIES, new TemplateObjectProperty("Ro"), new TemplateObjectProperty("So"));
		conclusion = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_DOMAIN, new TemplateObjectProperty("So"), new AtomicCls("X"));		
		RuleString rule30 = new RuleString("30", "ObjRng-ObjInv", conclusion, premise1, premise2);
		
	
		// Rule 31
		premise1 = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_RANGE, new TemplateObjectProperty("Ro"), new AtomicCls("X"));
		premise2 = new OWLAxiomStr(AxiomType.SYMMETRIC_OBJECT_PROPERTY, new TemplateObjectProperty("Ro")); 
		conclusion = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_DOMAIN, new TemplateObjectProperty("Ro"), new AtomicCls("X"));
		RuleString rule31 = new RuleString("31", "ObjRng-ObjSym", conclusion, premise1, premise2);


		// Rule 32
		premise1 = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_RANGE, new TemplateObjectProperty("Ro"), new AtomicCls("X"));
		premise2 = new SubClassStr("X", "Y");
		conclusion = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_RANGE, new TemplateObjectProperty("Ro"), new AtomicCls("Y"));
		RuleString rule32 = new RuleString("32", "ObjRng-SubCls", conclusion, premise1, premise2);

		
		// Rule 33
		premise1 = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_RANGE, new TemplateObjectProperty("Ro"), new AtomicCls("X")); 
		premise2 = new OWLAxiomStr(AxiomType.SUB_OBJECT_PROPERTY, new TemplateObjectProperty("So"), new TemplateObjectProperty("Ro"));
		conclusion = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_RANGE, new TemplateObjectProperty("So"), new AtomicCls("X"));
		RuleString rule33 = new RuleString("33", "ObjRng-SubObj", conclusion, premise1, premise2);

				
		// Rule 34
		premise1 = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_DOMAIN, new TemplateObjectProperty("Ro"), new AtomicCls("X"));
		premise2 = new OWLAxiomStr(AxiomType.INVERSE_OBJECT_PROPERTIES, new TemplateObjectProperty("Ro"), new TemplateObjectProperty("So"));							
		conclusion = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_RANGE, new TemplateObjectProperty("So"), new AtomicCls("X"));
		RuleString rule34 = new RuleString("34", "ObjDom-ObjInv", conclusion, premise1, premise2);
		
	
		// Rule 35
		premise1 = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_DOMAIN, new TemplateObjectProperty("Ro"), new AtomicCls("X"));
		premise2 = new OWLAxiomStr(AxiomType.SYMMETRIC_OBJECT_PROPERTY, new TemplateObjectProperty("Ro"));
		conclusion = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_RANGE, new TemplateObjectProperty("Ro"), new AtomicCls("X"));
		RuleString rule35 = new RuleString("35", "ObjDom-ObjSym", conclusion, premise1, premise2);

	
		// Rule 36		
		// 36.1
		premise1 = new SubClassStr("X", ExistsOrForAll.createObjSomeValFrom("Ro", "Z"));			
		premise2 = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_DOMAIN, new TemplateObjectProperty("Ro"), new AtomicCls("Y"));				
		conclusion =  new SubClassStr("X", "Y");
		RuleString rule36_1 = new RuleString("36.1", "ObjSom-ObjDom", conclusion, premise1, premise2);
		
		// 36.2
		restrictions = new RuleRestrictions(new AbsCardinalityRestriction("n", CardinalitySign.G, 0));	
		premise1 = new SubClassStr("X", CardExpGen.createObjMinCard("n", "Ro", "Z"));
		RuleString rule36_2 = new RuleString("36.2", "ObjSom-ObjDom", restrictions, conclusion, premise1, premise2);
		
		// 36.3
		premise1 = new SubClassStr("X", CardExpGen.createObjExactCard("n", "Ro", "Z"));				
		RuleString rule36_3 = new RuleString("36.3", "ObjSom-ObjDom", restrictions, conclusion, premise1, premise2);
		
		
		
		// Rule 37
		
		// 37.1		
		premise1 = new SubClassStr("X", ExistsOrForAll.createDataSomeValFrom("Rd", "Dr"));				
		premise2 = new OWLAxiomStr(AxiomType.DATA_PROPERTY_DOMAIN, new TemplateDataProperty("Rd"), new AtomicCls("Y"));
		conclusion =  new SubClassStr("X", "Y");
		RuleString rule37_1 = new RuleString("37.1", "DatSom-DatDom", conclusion, premise1, premise2);
		
		// 37.2
		tmp = CardExpGen.createDataMinCard("n", "Rd", "Dr");		
		restrictions = new RuleRestrictions(new AbsCardinalityRestriction("n", CardinalitySign.G, 0));
		premise1 = new SubClassStr(new AtomicCls("X"), tmp);
		RuleString rule37_2 = new RuleString("37.2", "DatSom-DatDom", restrictions, conclusion, premise1, premise2);

		
		// 37.3
		tmp = CardExpGen.createDataExactCard("n", "Rd", "Dr");		
		restrictions = new RuleRestrictions(new AbsCardinalityRestriction("n", CardinalitySign.G, 0));				
		premise1 = new SubClassStr(new AtomicCls("X"), tmp);				
		RuleString rule37_3 = new RuleString("37.3", "DatSom-DatDom", conclusion, premise1, premise2);


		
		// Rule 38		
		// 38.1
		tmpGroup1 = new ExpressionGroup("C1", new ClsExpStr[] { new AtomicCls("Y"), new AtomicCls("Z") });
		premise1 = new SubClassStr("X", ExistsOrForAll.createObjSomeValFrom("Ro", "Y"));
		premise2 = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_RANGE, new TemplateObjectProperty("Ro"), new AtomicCls("Z"));
		conclusion = new SubClassStr("X", ExistsOrForAll.createObjSomeValFrom("Ro", InterUnion.createIntersectionExpression( tmpGroup1)));
		RuleString rule38_1 = new RuleString("38.1", "ObjSom-ObjRng", conclusion, premise1, premise2);
		
			
		// 38.2
		premise1 = new SubClassStr("X", CardExpGen.createObjMinCard("n", "Ro", "Y"));
		restrictions = new RuleRestrictions(new AbsCardinalityRestriction("n", CardinalitySign.G, 0));
		conclusion = new SubClassStr("X", CardExpGen.createObjMinCard("n", "Ro", InterUnion.createIntersectionExpression( tmpGroup1)));				
		RuleString rule38_2 = new RuleString("38.2", "ObjSom-ObjRng", restrictions, conclusion, premise1, premise2);
		
		
		// 38.3
		premise1 = new SubClassStr("X", CardExpGen.createObjExactCard("n", "Ro", "Y"));
		conclusion = new SubClassStr("X", CardExpGen.createObjExactCard("n", "Ro", InterUnion.createIntersectionExpression( tmpGroup1)));			
		RuleString rule38_3 = new RuleString("38.3", "ObjSom-ObjRng", restrictions, conclusion, premise1, premise2);

		
				
		// Rule 39		
		premise1 = new SubClassStr("X", "Y");
		premise2 = new SubClassStr("Y", "Z");	
		conclusion = new SubClassStr("X", "Z");
		RuleString rule39 = new RuleString("39", "SubCls-SubCls-1", conclusion, premise1, premise2);

	
		// Rule 40
		tmpGroup1 = new ExpressionGroup("C1", new ClsExpStr[] { new AtomicCls("Y"), new AtomicCls("Z") });
		premise1 =  new SubClassStr("X", "Y");
		premise2 =  new SubClassStr("X", "Z");
		conclusion = new SubClassStr("X", InterUnion.createIntersectionExpression( tmpGroup1));
		RuleString rule40 = new RuleString("40", "SubCls-SubCls-2", conclusion, premise1, premise2);

		
		
		
		// Rule 41		
		// 41.1
		tmp = ExistsOrForAll.createObjSomeValFrom("Ro", "Y");
		premise1 = new SubClassStr(new AtomicCls("X"), tmp);

		tmp = CardExpGen.createObjMinCard("n", "Ro", "Y");
		premise2 = new SubClassStr(tmp, new AtomicCls("Z"));
		restrictions = new RuleRestrictions(new AbsCardinalityRestriction("n", CardinalitySign.EQ, 1));
		conclusion = new SubClassStr(new AtomicCls("X"), new AtomicCls("Z"));
		RuleString rule41_1 = new RuleString("41.1", "ObjSom-ObjMin", restrictions, conclusion, premise1, premise2);

		
		// 41.2
		tmp = CardExpGen.createObjMinCard("n", "Ro", "Y");
		premise1 = new SubClassStr(new AtomicCls("X"), tmp);
		restrictions = new RuleRestrictions(new AbsCardinalityRestriction("n", CardinalitySign.G, 0));
		tmp = ExistsOrForAll.createObjSomeValFrom("Ro", "Y");
		premise2 = new SubClassStr(tmp, new AtomicCls("Z"));
		RuleString rule41_2 = new RuleString("41.2", "ObjSom-ObjMin", restrictions, conclusion, premise1, premise2);

		
		// 41.3
		tmp = CardExpGen.createObjExactCard("n", "Ro", "Y");	
		premise1 = new SubClassStr(new AtomicCls("X"), tmp);
		RuleString rule41_3 = new RuleString("41.3", "ObjSom-ObjMin", restrictions, conclusion, premise1, premise2);

		
		
		// Rule 42
		// 42.1
		tmp = ExistsOrForAll.createDataSomeValFrom("Rd", "Dr");		
		premise1 = new SubClassStr(new AtomicCls("X"), tmp);

		tmp = CardExpGen.createDataMinCard("n", "Rd", "Dr");
		premise2 = new SubClassStr(tmp, new AtomicCls("Z"));
		restrictions = new RuleRestrictions(new AbsCardinalityRestriction("n", CardinalitySign.EQ, 1));
		conclusion = new SubClassStr(new AtomicCls("X"), new AtomicCls("Z"));
		RuleString rule42_1 = new RuleString("42.1", "DatSom-DatMin", restrictions, conclusion, premise1, premise2);


		// 42.2
		tmp = CardExpGen.createDataMinCard("n", "Rd", "Dr");		
		premise1 = new SubClassStr(new AtomicCls("X"), tmp);
		tmp = ExistsOrForAll.createDataSomeValFrom("Rd", "Dr");	
		premise2 = new SubClassStr(tmp, new AtomicCls("Z"));
		restrictions = new RuleRestrictions(new AbsCardinalityRestriction("n", CardinalitySign.G, 0));
		conclusion = new SubClassStr(new AtomicCls("X"), new AtomicCls("Z"));
		RuleString rule42_2 = new RuleString("42.2", "DatSom-DatMin", restrictions, conclusion, premise1, premise2);


		// 42.3
		tmp = CardExpGen.createDataExactCard("n", "Rd", "Dr");
		premise1 = new SubClassStr(new AtomicCls("X"), tmp);
		RuleString rule42_3 = new RuleString("42.3", "DatSom-DatMin", restrictions, conclusion, premise1, premise2);

				
			
		// Rule 43		
		// 43.1
		premise1 = new SubClassStr("X", ExistsOrForAll.createObjSomeValFrom("Ro", "Y"));
		premise2 = new SubClassStr(new AtomicCls("Y"), new AtomicCls("Z"));
		conclusion = new SubClassStr("X", ExistsOrForAll.createObjSomeValFrom("Ro", "Z"));
		RuleString rule43_1 = new RuleString("43.1", "ObjSom-SubCls", conclusion, premise1, premise2);


		// 43.2
		premise1 = new SubClassStr(new AtomicCls("X"), CardExpGen.createObjMinCard("n", "Ro", "Y"));
		premise2 = new SubClassStr(new AtomicCls("Y"), new AtomicCls("Z"));
		restrictions = new RuleRestrictions(new AbsCardinalityRestriction("n", CardinalitySign.GEQ, 0));
		conclusion = new SubClassStr(new AtomicCls("X"), CardExpGen.createObjMinCard("n", "Ro", "Z"));
		RuleString rule43_2 = new RuleString("43.2", "ObjSom-SubCls", restrictions, conclusion, premise1, premise2);

		
		// 43.3
		premise1 = new SubClassStr(new AtomicCls("X"), CardExpGen.createObjExactCard("n", "Ro", "Y"));
		premise2 = new SubClassStr(new AtomicCls("Y"), new AtomicCls("Z"));
		conclusion = new SubClassStr(new AtomicCls("X"), CardExpGen.createObjExactCard("n", "Ro", "Z"));
		RuleString rule43_3 = new RuleString("43.3", "ObjSom-SubCls", restrictions, conclusion, premise1, premise2);

		
			
		// Rule 44
		// 44.1
		premise1 = new SubClassStr("X", ExistsOrForAll.createObjSomeValFrom("Ro", "Y"));
		premise2 = getSubObjectPropertyOf("Ro", "So");
		conclusion = new SubClassStr("X", ExistsOrForAll.createObjSomeValFrom("So", "Y"));
		RuleString rule44_1 = new RuleString("44.1", "ObjSom-SubObj", conclusion, premise1, premise2);
		
		// 44.2
		premise1 = new SubClassStr("X", CardExpGen.createObjMinCard("n", "Ro", "Y"));
		restrictions = new RuleRestrictions(new AbsCardinalityRestriction("n", CardinalitySign.GEQ, 0));
		conclusion = new SubClassStr("X", CardExpGen.createObjMinCard("n", "So", "Y"));
		RuleString rule44_2 = new RuleString("44.2", "ObjSom-SubObj", restrictions, conclusion, premise1, premise2);

		// 44.3
		premise1 = new SubClassStr("X", CardExpGen.createObjExactCard("n", "Ro", "Y"));
		conclusion = new SubClassStr("X", CardExpGen.createObjExactCard("n", "So", "Y"));
		RuleString rule44_3 = new RuleString("44.3", "ObjSom-SubObj", restrictions, conclusion, premise1, premise2);

	
		
		// Rule 45
		tmpGroup1 = new ExpressionGroup("C1", new ClsExpStr[] { new AtomicCls("Y"), new AtomicCls("Z") });
		premise1 = new SubClassStr("Y", "Z");
		premise2 = new SubClassStr("X", InterUnion.createUnionExpression( tmpGroup1));
		conclusion = new SubClassStr("X", "Z");
		RuleString rule45 = new RuleString("45", "ObjUni-SubCls", conclusion, premise1, premise2);

		
		// Rule 46
		premise1 = new SubClassStr(new AtomicCls("X"), ExistsOrForAll.createObjAllValFrom("Ro", "Y"));
		premise2 = new OWLAxiomStr(AxiomType.INVERSE_OBJECT_PROPERTIES, new TemplateObjectProperty("Ro"), new TemplateObjectProperty("So"));
		conclusion = new SubClassStr(ExistsOrForAll.createObjSomeValFrom("So", "X"), new AtomicCls("Y"));
		RuleString rule46 = new RuleString("46", "ObjAll-ObjInv", conclusion, premise1, premise2);
		

		
		// Rule 47
		premise1 = new SubClassStr(ExistsOrForAll.createObjSomeValFrom("Ro", "Y"), new AtomicCls("X"));
		premise2 = new SubClassStr(ExistsOrForAll.createObjAllValFrom("Ro", "F"), new AtomicCls("X"));
		conclusion = new SubClassStr(ExistsOrForAll.createObjAllValFrom("Ro", "Y"), new AtomicCls("X"));
		RuleString rule47 = new RuleString("47", "ObjSom-ObjAll-1", conclusion, premise1, premise2);


		
		// Rule 48
		premise1 = new SubClassStr(new AtomicCls("X"), ExistsOrForAll.createObjSomeValFrom("Ro", "T"));
		premise2 = new SubClassStr(new AtomicCls("X"), ExistsOrForAll.createObjAllValFrom("Ro", "Y"));
		conclusion = new SubClassStr(new AtomicCls("X"), ExistsOrForAll.createObjSomeValFrom("Ro", "Y"));
		RuleString rule48 = new RuleString("48", "ObjSom-ObjAll-2", conclusion, premise1, premise2);
		
		
				
		// Rule 49
		// 49.1
		tmp = ExistsOrForAll.createObjSomeValFrom("Ro", ExistsOrForAll.createObjSomeValFrom("Ro", "Y"));
		premise1 = new SubClassStr(new AtomicCls("X"), tmp);
		premise2 = new OWLAxiomStr(AxiomType.TRANSITIVE_OBJECT_PROPERTY, new TemplateObjectProperty("Ro"));
		conclusion = new SubClassStr(new AtomicCls("X"), ExistsOrForAll.createObjSomeValFrom("Ro", "Y"));
		RuleString rule49_1 = new RuleString("49.1", "ObjSom-ObjTra", conclusion, premise1, premise2);

		
		// 49.2		
		tmp = CardExpGen.createObjMinCard("n", "Ro", CardExpGen.createObjMinCard("n", "Ro", "Y"));
		premise1 = new SubClassStr("X", (ClsExpStr) tmp);
		premise2 = createPrimitiveTransObjProp("Ro");
		restrictions = new RuleRestrictions(new AbsCardinalityRestriction("n", CardinalitySign.G, 0));
		conclusion = new SubClassStr("X", CardExpGen.createObjMinCard("n", "Ro", "Y"));
		RuleString rule49_2 = new RuleString("49.2", "ObjSom-ObjTra", restrictions, conclusion, premise1, premise2);

		
		
		// Rule 50
		premise1 = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_DOMAIN, new TemplateObjectProperty("Ro"), new AtomicCls("X"));
		premise2 = new SubClassStr(new AtomicCls("X"), new AtomicCls("F"));
		conclusion = new SubClassStr(new AtomicCls("T"), ExistsOrForAll.createObjAllValFrom("Ro", "F"));
		RuleString rule50 = new RuleString("50", "ObjDom-Bot", conclusion, premise1, premise2);


		
		// Rule 51
		premise1 = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_RANGE, new TemplateObjectProperty("Ro"), new AtomicCls("X")); 
		premise2 = new SubClassStr(new AtomicCls("X"), new AtomicCls("F"));
		conclusion = new SubClassStr(new AtomicCls("T"), ExistsOrForAll.createObjAllValFrom("Ro", "F"));
		RuleString rule51 = new RuleString("51", "ObjRng-Bot", conclusion, premise1, premise2);

				
		rules.get(2).add(rule12_1);
		rules.get(2).add(rule12_2);
		rules.get(2).add(rule13_1);
		rules.get(2).add(rule13_2);
		rules.get(2).add(rule14_1);
		rules.get(2).add(rule14_2);
		rules.get(2).add(rule15);
		rules.get(2).add(rule16);
		rules.get(2).add(rule17_1);
		rules.get(2).add(rule17_2);
		rules.get(2).add(rule18_1);
		rules.get(2).add(rule18_2);
		rules.get(2).add(rule19_1);
		rules.get(2).add(rule19_2);
		rules.get(2).add(rule20_1);
		rules.get(2).add(rule20_2);
		rules.get(2).add(rule20_3);
		rules.get(2).add(rule21_1);
		rules.get(2).add(rule21_2);
		rules.get(2).add(rule21_3);
		rules.get(2).add(rule22_1);
		rules.get(2).add(rule22_2);
		rules.get(2).add(rule22_3);
		rules.get(2).add(rule23);
		rules.get(2).add(rule24);
		rules.get(2).add(rule25_1);
		rules.get(2).add(rule25_2);
		rules.get(2).add(rule26);
		rules.get(2).add(rule27);
		rules.get(2).add(rule28);
		rules.get(2).add(rule29);
		rules.get(2).add(rule30);
		rules.get(2).add(rule31);
		rules.get(2).add(rule32);
		rules.get(2).add(rule33);
		rules.get(2).add(rule34);
		rules.get(2).add(rule35);
		rules.get(2).add(rule36_1);
		rules.get(2).add(rule36_2);
		rules.get(2).add(rule36_3);
		rules.get(2).add(rule37_1);
		rules.get(2).add(rule37_2);
		rules.get(2).add(rule37_3);
		rules.get(2).add(rule38_1);
		rules.get(2).add(rule38_2);
		rules.get(2).add(rule38_3);
		rules.get(2).add(rule39);
		rules.get(2).add(rule40);
		rules.get(2).add(rule41_1);
		rules.get(2).add(rule41_2);
		rules.get(2).add(rule41_3);
		rules.get(2).add(rule42_1);
		rules.get(2).add(rule42_2);
		rules.get(2).add(rule42_3);
		rules.get(2).add(rule43_1);
		rules.get(2).add(rule43_2);
		rules.get(2).add(rule43_3);
		rules.get(2).add(rule44_1);
		rules.get(2).add(rule44_2);
		rules.get(2).add(rule44_3);
		rules.get(2).add(rule45);
		rules.get(2).add(rule46);
		rules.get(2).add(rule47);
		rules.get(2).add(rule48);
		rules.get(2).add(rule49_1);
		rules.get(2).add(rule49_2);
		rules.get(2).add(rule50);
		rules.get(2).add(rule51);
	}
	
	
	
	
	private static void generateThreePremiseRules() {

		// Rule 52
		ExpressionGroup tmpGroup1 = new ExpressionGroup("C1", new ClsExpStr[] { new AtomicCls("X"), new AtomicCls("Y") });
		OWLAxiomStr premise1 = new OWLAxiomStr(AxiomType.DISJOINT_CLASSES, tmpGroup1);
		OWLAxiomStr premise2 = new SubClassStr("U", "X");
		OWLAxiomStr premise3 = new SubClassStr("V", "Y");
		ExpressionGroup tmpGroup2 = new ExpressionGroup("C1", new ClsExpStr[] { new AtomicCls("U"), new AtomicCls("V") });
		OWLAxiomStr conclusion = new OWLAxiomStr(AxiomType.DISJOINT_CLASSES, tmpGroup2);
		RuleString rule52 = new RuleString("52", "DisCls-SubCls-SubCls", conclusion, premise2, premise3, premise1);

		
		
		// Rule 53
		tmpGroup1 = new ExpressionGroup("C1", new ClsExpStr[] { new AtomicCls("Y"), new AtomicCls("Z") });
		premise1 = new SubClassStr("X", "Y");
		premise2 = new SubClassStr("X", "Z");
		premise3 = new OWLAxiomStr(AxiomType.DISJOINT_CLASSES, tmpGroup1);
		conclusion = new SubClassStr("X", "F");
		RuleString rule53 = new RuleString("53", "SubCls-SubCls-DisCls", conclusion, premise1, premise2, premise3);

	
		// Rule 54
		tmpGroup1 = new ExpressionGroup("C1", new ClsExpStr[] { new AtomicCls("U"), new AtomicCls("V") });
		premise1 = new SubClassStr("U", "Z");
		premise2 = new SubClassStr("V", "Z");
		premise3 = new SubClassStr("X", InterUnion.createUnionExpression( tmpGroup1));
		conclusion = new SubClassStr("X", "Z");
		RuleString rule54 = new RuleString("54", "ObjUni-SubCls-SubCls", conclusion, premise1, premise2, premise3);
	
	
		
		// Rule 55
		
		// 55.1
		premise1 = new SubClassStr("X", ExistsOrForAll.createObjSomeValFrom("Ro", "Y"));
		premise2 = new SubClassStr("Y", ExistsOrForAll.createObjSomeValFrom("Ro", "Z"));
		premise3 = createPrimitiveTransObjProp("Ro");
		conclusion = new SubClassStr("X", ExistsOrForAll.createObjSomeValFrom("Ro", "Z"));
		RuleString rule55_1 = new RuleString("55.1", "ObjSom-ObjSom-ObjTra", conclusion, premise1, premise2, premise3);
		
		// 55.2
		premise1 = new SubClassStr("X", CardExpGen.createObjMinCard("n", "Ro", "Y"));
		premise2 = new SubClassStr("Y", CardExpGen.createObjMinCard("n", "Ro", "Z"));
		premise3 = createPrimitiveTransObjProp("Ro");
		RuleRestrictions restrictions = new RuleRestrictions(new AbsCardinalityRestriction("n", CardinalitySign.G, 0));
		conclusion = new SubClassStr("X", CardExpGen.createObjMinCard("n", "Ro", "Z"));
		RuleString rule55_2 = new RuleString("55.2", "ObjSom-ObjSom-ObjTra", restrictions, conclusion, premise1, premise2, premise3);
		

			
		// Rule 56
		
		// 56.1
		premise1 = new SubClassStr("X", ExistsOrForAll.createLiteralSomeValFrom("Rd", "l0"));
		premise2 = new SubClassStr("X", ExistsOrForAll.createLiteralSomeValFrom("Rd", "l1"));
		premise3 = new OWLAxiomStr(AxiomType.FUNCTIONAL_DATA_PROPERTY, new TemplateDataProperty("Rd"));
		restrictions = new RuleRestrictions(new DisjointDatatypesRestriction("l0", "l1"));
		conclusion = new SubClassStr("X", "F");
		RuleString rule56_1 = new RuleString("56.1", "DatVal-DatVal-DatFun", restrictions, conclusion, premise1, premise2, premise3);

		// 56.2 - Handle the case of the other restriction
		restrictions = new RuleRestrictions(new UnequalLiteralsRestriction("l0", "l1"));
		RuleString rule56_2 = new RuleString("56.2", "DatVal-DatVal-DatFun", restrictions, conclusion, premise1, premise2, premise3);
		
			
		rules.get(3).add(rule52);
		rules.get(3).add(rule53);
		rules.get(3).add(rule54);
		rules.get(3).add(rule55_1);
		rules.get(3).add(rule55_2);
		rules.get(3).add(rule56_1);	
		rules.get(3).add(rule56_2);
	}
	
	
	
	private static void generateFourPremiseRules() {
		
		// Rule 57
		OWLAxiomStr premise1 = new SubClassStr("X", (ClsExpStr) ExistsOrForAll.createIndividualSomeValFrom("Ro", "i"));
		OWLAxiomStr premise2 = new SubClassStr("X", (ClsExpStr) ExistsOrForAll.createIndividualSomeValFrom( "Ro", "j"));
		OWLAxiomStr premise3 = new OWLAxiomStr(AxiomType.DIFFERENT_INDIVIDUALS, new TemplateLiteral("i"), new TemplateLiteral("j"));	
		OWLAxiomStr premise4 = new OWLAxiomStr(AxiomType.FUNCTIONAL_OBJECT_PROPERTY, new TemplateObjectProperty("Ro"));
		OWLAxiomStr conclusion = new SubClassStr("X", "F");
		RuleString rule57 = new RuleString("57", "ObjVal-ObjVal-DifInd-ObjFun", conclusion, premise1, premise2, premise3, premise4);
	
		rules.get(4).add(rule57);
	}
	
	
}
