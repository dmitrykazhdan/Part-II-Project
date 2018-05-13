package CorpusAnalysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import InferenceRules.GenerateRules;
import InferenceRules.RuleString;
import OWLExpressionTemplates.AtomicCls;
import OWLExpressionTemplates.CardExpGen;
import OWLExpressionTemplates.ClsExpStr;
import OWLExpressionTemplates.ExistsOrForAll;
import OWLExpressionTemplates.ExpressionGroup;
import OWLExpressionTemplates.InterUnion;
import OWLExpressionTemplates.OWLAxiomStr;
import OWLExpressionTemplates.SubClassStr;
import OWLExpressionTemplates.TemplateObjectProperty;
import ProofTreeComputation.ProofTree;
import ProofTreeComputation.ProofTreeGenerator;
import RuleRestrictions.AbsCardinalityRestriction;
import RuleRestrictions.CardinalitySign;
import RuleRestrictions.RuleRestriction;
import RuleRestrictions.RuleRestrictions;
import RuleRestrictions.SubSetRestriction;

public class TopBottomEntityCounter {
	
	public static void addRules() {
		
		Map<Integer, List<RuleString>> rules = GenerateRules.getRules();
		
		// Rule 15 extension
		ExpressionGroup tmpGroup1 = new ExpressionGroup("C1", new ClsExpStr[] { new AtomicCls("X"), new AtomicCls("T") }, "Z");
		OWLAxiomStr premise1 = new OWLAxiomStr(AxiomType.DISJOINT_CLASSES, tmpGroup1);
		OWLAxiomStr conclusion = new SubClassStr("X", "F");
		RuleString rule15 = new RuleString("15.top", "SubCls-DisCls", conclusion, premise1);

		
		// Rule 28 extension
		premise1 = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_DOMAIN, new TemplateObjectProperty("Ro"), new AtomicCls("X"));
		conclusion = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_DOMAIN, new TemplateObjectProperty("Ro"), new AtomicCls("T"));				
		RuleString rule28 = new RuleString("28.top", "ObjDom-SubCls", conclusion, premise1);

		
		// Rule 32 extension
		premise1 = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_RANGE, new TemplateObjectProperty("Ro"), new AtomicCls("X"));
		conclusion = new OWLAxiomStr(AxiomType.OBJECT_PROPERTY_RANGE, new TemplateObjectProperty("Ro"), new AtomicCls("T"));
		RuleString rule32 = new RuleString("32.top", "ObjRng-SubCls", conclusion, premise1);
		
		
		
		// Rule 43		
		// 43.1 extension
		premise1 = new SubClassStr("X", ExistsOrForAll.createObjSomeValFrom("Ro", "Y"));
		conclusion = new SubClassStr("X", ExistsOrForAll.createObjSomeValFrom("Ro", "T"));
		RuleString rule43_1 = new RuleString("43.1.top", "ObjSom-SubCls", conclusion, premise1);


		// 43.2 extension
		premise1 = new SubClassStr(new AtomicCls("X"), CardExpGen.createObjMinCard("n", "Ro", "Y"));
		RuleRestrictions restrictions = new RuleRestrictions(new AbsCardinalityRestriction("n", CardinalitySign.GEQ, 0));
		conclusion = new SubClassStr(new AtomicCls("X"), CardExpGen.createObjMinCard("n", "Ro", "T"));
		RuleString rule43_2 = new RuleString("43.2.top", "ObjSom-SubCls", restrictions, conclusion, premise1);

		
		// 43.3 extension
		premise1 = new SubClassStr(new AtomicCls("X"), CardExpGen.createObjExactCard("n", "Ro", "Y"));
		conclusion = new SubClassStr(new AtomicCls("X"), CardExpGen.createObjExactCard("n", "Ro", "T"));
		RuleString rule43_3 = new RuleString("43.3.top", "ObjSom-SubCls", restrictions, conclusion, premise1);

			
		rules.get(1).add(rule15);
		rules.get(1).add(rule28);
		rules.get(1).add(rule32);
		rules.get(1).add(rule43_1);
		rules.get(1).add(rule43_2);
		rules.get(1).add(rule43_3);
	}
}
