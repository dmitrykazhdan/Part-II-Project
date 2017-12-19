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

	
	
	public static void main (String args[]) throws IOException {
		
		List<GenericExpStr> premiseChildren = new ArrayList<GenericExpStr>();
		List<GenericExpStr> leaves = new ArrayList<GenericExpStr>();
		
		
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
		
		
		String explanationDirName = "/Users/AdminDK/Desktop/TestExplanations/";
		File explanationDir = new File(explanationDirName);
		
		File[] explanationFiles = explanationDir.listFiles(new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		        return name.endsWith(".xml");
		    }
		});
		
		
		for (int i = 0; i < explanationFiles.length; i++) {
			
			String explanationFilename = explanationFiles[i].getAbsolutePath();		
			InputStream fileInputStream = new FileInputStream(explanationFilename);

			Explanation<OWLAxiom> explanation = Explanation.load(fileInputStream);			
			
			Set<OWLAxiom> justification = explanation.getAxioms();
			List<OWLAxiom> justsAsList = new ArrayList<OWLAxiom>(justification);	
			
			OWLAxiom conc = rule39.generate(justsAsList);
			System.out.println("hey");
					
		}	

		
		
		// Sample rule: rule 18	
//		List<GenericExpStr> leaves = new ArrayList<GenericExpStr>();
//		leaves.add(new ClsExpStr("n"));
//		leaves.add(new ClsExpStr("R_0"));
//		leaves.add(new ClsExpStr("Y"));
//		GenericExpStr premise1Child = new ClsExpStr(ClassExpressionType.DATA_MIN_CARDINALITY, leaves);
//		GenericExpStr premise2Child = new ClsExpStr("X");
//		premiseChildren.add(premise2Child);
//		premiseChildren.add(premise1Child);
//		OWLAxiomStr premise1 = new OWLAxiomStr(AxiomType.SUBCLASS_OF, premiseChildren);
//		
//		
//		premise2Child = new EntityStr("R_0", EntityType.OBJECT_PROPERTY);
//		premiseChildren = new ArrayList<GenericExpStr>();
//		premiseChildren.add(premise2Child);
//		OWLAxiomStr premise2 = new OWLAxiomStr(AxiomType.FUNCTIONAL_OBJECT_PROPERTY, premiseChildren);
//
//		
//		List<OWLAxiomStr> premises = new ArrayList<OWLAxiomStr>();
//		premises.add(premise1);
//		premises.add(premise2);
//		
//		RuleString rule18 = new RuleString(premises, 2);
//		
	}
}
