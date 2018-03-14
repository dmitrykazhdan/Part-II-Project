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
import ProofTreeComputation.ProofTree;
import ProofTreeComputation.ProofTreeGenerator;
import RuleRestrictions.RuleRestriction;
import RuleRestrictions.RuleRestrictions;
import RuleRestrictions.SubSetRestriction;

public class TopBottomEntityCounter {

	
	public static void main(String args[]) throws IOException {

		Path justificationFolderPath = Paths.get("/Users/AdminDK/Desktop/Refined (06.03.2018)/FailedExplanations");
		File justificationFiles = new File(justificationFolderPath.toString());

		File[] allFiles = justificationFiles.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".xml");
			}
		});	
		
		int cmpTed = 0;
		int edgeEntityCount = 0;
		List<OWLAxiom> expressions = new ArrayList<OWLAxiom>();
		
		for (File explanationFile : allFiles) {
			
			InputStream fileInputStream;
			fileInputStream = new FileInputStream(explanationFile.getAbsolutePath());
			Explanation<OWLAxiom> explanation = Explanation.load(fileInputStream);
			Set<OWLAxiom> justification = explanation.getAxioms();

			
//			addRules();
			boolean has = extraRule(justification);
//			boolean has = processJustification(justification, expressions);

			if (has) {
//				List<ProofTree> trees = ProofTreeGenerator.generateProofTrees(explanation);
//				
//				if (trees != null && trees.size() > 0) {
//					cmpTed++;
//					System.out.println(cmpTed);
//				}	
				edgeEntityCount++;
			}
					
			fileInputStream.close();
		}		
		System.out.println(edgeEntityCount);
		
//		Collections.shuffle(expressions);
//		for (OWLAxiom exp : expressions.subList(0, 200)) {
//			System.out.println(exp.toString());
//		}
	}

	
	private static void addRules() {
		
		Map<Integer, List<RuleString>> rules = GenerateRules.getRules();
		
		OWLAxiomStr premise1 = new SubClassStr("X", ExistsOrForAll.createObjSomeValFrom("Ro", "Z"));
		OWLAxiomStr conclusion = new SubClassStr("X", ExistsOrForAll.createObjSomeValFrom("Ro", "T"));
		RuleString rule43_1 = new RuleString("ext1", "ObjSom-SubCls", conclusion, premise1);
		rules.get(1).add(rule43_1);
	}
	
	
	private static boolean extraRule(Set<OWLAxiom> justification) {
		
		for (OWLAxiom axiom : justification) {
			
			if (matchesRule(axiom)) {
				return true;				
			} 
		}
		return false;
	}
	
	
	private static boolean processJustification(Set<OWLAxiom> justification, List<OWLAxiom> expressionStrings) {
		
		for (OWLAxiom axiom : justification) {
			
			if (axiom.isLogicalAxiom()) {
				Set<OWLClass> expressions = axiom.getClassesInSignature();	
				
				for (OWLClass cls : expressions) {
					if (cls.isOWLThing()) {
						expressionStrings.add(axiom);
						return true;
					}
				}				
			} 
		}
		return false;
	}
	
	
	private static boolean matchesRule(OWLAxiom axiom) {
		
		List<RuleString> rules = new ArrayList<RuleString>();
		ExpressionGroup tmpGroup1;
		OWLAxiomStr premise1;
		RuleString r1;
		
		tmpGroup1 = new ExpressionGroup("C1", new ClsExpStr[] { new AtomicCls("T")}, "Z" );
		premise1 = new OWLAxiomStr(AxiomType.DISJOINT_CLASSES, tmpGroup1);
		r1 = new RuleString("X", "", premise1, premise1);
		rules.add(r1);

		premise1 = new SubClassStr(ExistsOrForAll.createObjSomeValFrom("Ro", "T"), "X");
		r1 = new RuleString("X", "", premise1, premise1);
		rules.add(r1);
			
		premise1 = new SubClassStr("X", CardExpGen.createObjExactCard("n", "Ro", "T"));
		r1 = new RuleString("X", "", premise1, premise1);
		rules.add(r1);
		
		premise1 = new SubClassStr("X", CardExpGen.createObjMaxCard("n", "Ro", "T"));
		r1 = new RuleString("X", "", premise1, premise1);
		rules.add(r1);
		
		premise1 = new SubClassStr("X", CardExpGen.createObjMinCard("n", "Ro", "T"));
		r1 = new RuleString("X", "", premise1, premise1);
		rules.add(r1);
		
		
		for (RuleString r : rules) {
			if (r.matchPremises(Arrays.asList(axiom))) {
				return true;
			}
		}	
		return false;
	}
}
