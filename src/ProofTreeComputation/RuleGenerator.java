package ProofTreeComputation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

import InferenceRules.ClassExpressionString;
import InferenceRules.OWLAxiomString;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectIntersectionOfImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectSomeValuesFromImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectUnionOfImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubClassOfAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubObjectPropertyOfAxiomImpl;

public class RuleGenerator {

	private static Map<Integer, List<InferenceRule>> rules = null;

	public static Map<Integer, List<InferenceRule>> getRules() {

		if (rules == null) {
			generateRules();
		} 

		return rules;
	}


	private static void generateRules() {

		rules = new HashMap<Integer, List<InferenceRule>>();

		for (int i = 1; i <= 4; i++) {			
			rules.put(i, new ArrayList<InferenceRule>());			
		}
		
		// RULE 1
		InferenceRule rule1 = 
				new InferenceRule("1", "EquCls", 1) {
			
			@Override	
			public boolean matchPremises(List<OWLAxiom> premises) {
				OWLAxiom premise = premises.get(0);
				return premise.isOfType(AxiomType.EQUIVALENT_CLASSES);
			}
			
			@Override	
			public boolean matchPremisesAndConclusion(List<OWLAxiom> premises, OWLAxiom conclusion) {

				OWLAxiom premise = premises.get(0);

				if ((premise.isOfType(AxiomType.EQUIVALENT_CLASSES)) && (conclusion.isOfType(AxiomType.SUBCLASS_OF))			
						&& premise.getClassesInSignature().containsAll(conclusion.getClassesInSignature())) {				
					return  true;								
				} else {
					return false;
				}
			}

			@Override
			public OWLAxiom generateConclusion(List<OWLAxiom> premises) {
				return null;
			}					
		};



		// RULE 6
		InferenceRule rule6 = 
				new InferenceRule("6", "ObjExt", 1) {
			
			@Override	
			public boolean matchPremises(List<OWLAxiom> premises) {
				
				OWLAxiom premise = premises.get(0);

				if (premise.isOfType(AxiomType.SUBCLASS_OF)) {			

					OWLClassExpression axiomSuperCls = ((OWLSubClassOfAxiom) premise).getSuperClass();
					OWLClassExpression axiomSubCls = ((OWLSubClassOfAxiom) premise).getSubClass();

					if (axiomSuperCls.getClassExpressionType().equals(ClassExpressionType.OBJECT_EXACT_CARDINALITY))  {

						int n1 = ((OWLObjectExactCardinality) axiomSuperCls).getCardinality();
						if (n1 >= 0) { return true; }			
					}
				}

				return false;
			}
			
			@Override	
			public boolean matchPremisesAndConclusion(List<OWLAxiom> premises, OWLAxiom conclusion) {

				OWLAxiom premise = premises.get(0);

				if ((premise.isOfType(AxiomType.SUBCLASS_OF)) && (conclusion.isOfType(AxiomType.SUBCLASS_OF))) {			

					OWLClassExpression axiomSuperCls = ((OWLSubClassOfAxiom) premise).getSuperClass();
					OWLClassExpression axiomSubCls = ((OWLSubClassOfAxiom) premise).getSubClass();
					OWLClassExpression entSuperCls = ((OWLSubClassOfAxiom) conclusion).getSuperClass();
					OWLClassExpression entSubCls = ((OWLSubClassOfAxiom) conclusion).getSubClass();


					if ((axiomSubCls.equals(entSubCls)) &&			
							(axiomSuperCls.getClassExpressionType().equals(ClassExpressionType.OBJECT_EXACT_CARDINALITY)) 
							&& (entSuperCls.getClassExpressionType().equals(ClassExpressionType.OBJECT_MIN_CARDINALITY))) {

						int n1 = ((OWLObjectExactCardinality) axiomSuperCls).getCardinality();
						int n2 = ((OWLObjectMinCardinality) entSuperCls).getCardinality();

						if ((n1 >= n2) && (n2 >= 0)) {
							return true;
						}				
					}
				}

				return false;
			}

			@Override
			public OWLAxiom generateConclusion(List<OWLAxiom> premises) {
				return null;
			}					
		};
		
		
		
		// RULE 8
		InferenceRule rule8 = 
				new InferenceRule("8", "Top", 1) {
			
			@Override	
			public boolean matchPremises(List<OWLAxiom> premises) {
				
				OWLAxiom premise = premises.get(0);

				if (premise.isOfType(AxiomType.SUBCLASS_OF)) {			
					OWLClassExpression axiomSubCls = ((OWLSubClassOfAxiom) premise).getSubClass();
					return axiomSubCls.isOWLThing();
				}
				return false;
			}
			
			@Override	
			public boolean matchPremisesAndConclusion(List<OWLAxiom> premises, OWLAxiom conclusion) {

				if (matchPremises(premises) && conclusion.isOfType(AxiomType.SUBCLASS_OF)) {
					
					OWLAxiom premise = premises.get(0);
					OWLClassExpression premiseSuperCls = ((OWLSubClassOfAxiom) premise).getSuperClass();
					OWLClassExpression conclusionSuperCls = ((OWLSubClassOfAxiom) conclusion).getSuperClass();
					
					return premiseSuperCls.equals(conclusionSuperCls);
				}
				return false;
			}

			@Override
			public OWLAxiom generateConclusion(List<OWLAxiom> premises) {
				
				// How do you generate Y <= X, without any restrictions on Y?
				// Consider it.
				return null;
			}					
		};	
		
		
		// RULE 9
		InferenceRule rule9 = 
				new InferenceRule("9", "Bot", 1) {
			
			@Override	
			public boolean matchPremises(List<OWLAxiom> premises) {
				
				OWLAxiom premise = premises.get(0);

				if (premise.isOfType(AxiomType.SUBCLASS_OF)) {			
					OWLClassExpression axiomSuperCls = ((OWLSubClassOfAxiom) premise).getSuperClass();
					return axiomSuperCls.isOWLNothing();
				}
				return false;
			}
			
			@Override	
			public boolean matchPremisesAndConclusion(List<OWLAxiom> premises, OWLAxiom conclusion) {

				if (matchPremises(premises) && conclusion.isOfType(AxiomType.SUBCLASS_OF)) {
					
					OWLAxiom premise = premises.get(0);
					OWLClassExpression premiseSubCls = ((OWLSubClassOfAxiom) premise).getSubClass();
					OWLClassExpression conclusionSubCls = ((OWLSubClassOfAxiom) conclusion).getSubClass();
					
					return premiseSubCls.equals(conclusionSubCls);
				}
				return false;
			}

			@Override
			public OWLAxiom generateConclusion(List<OWLAxiom> premises) {
				
				// How do you generate X <= Y, without any restrictions on Y?
				// Consider it.
				return null;
			}					
		};	
		
		
		// RULE 10
		InferenceRule rule10 = 
				new InferenceRule("10", "ObjCom-1", 1) {
			
			@Override	
			public boolean matchPremises(List<OWLAxiom> premises) {
				
				OWLAxiom premise = premises.get(0);

				if (premise.isOfType(AxiomType.SUBCLASS_OF)) {			
					OWLClassExpression axiomSuperCls = ((OWLSubClassOfAxiom) premise).getSuperClass();
					OWLClassExpression axiomSubCls = ((OWLSubClassOfAxiom) premise).getSubClass();
					
					return axiomSuperCls.getObjectComplementOf().equals(axiomSubCls);
				}
				return false;
			}
			
			@Override	
			public boolean matchPremisesAndConclusion(List<OWLAxiom> premises, OWLAxiom conclusion) {

				if (matchPremises(premises) && conclusion.isOfType(AxiomType.SUBCLASS_OF)) {
					
					OWLAxiom premise = premises.get(0);
					OWLClassExpression premiseSubCls = ((OWLSubClassOfAxiom) premise).getSubClass();
					OWLClassExpression conclusionSubCls = ((OWLSubClassOfAxiom) conclusion).getSubClass();
					OWLClassExpression conclusionSuperCls = ((OWLSubClassOfAxiom) conclusion).getSuperClass();
					
					return premiseSubCls.equals(conclusionSubCls) && conclusionSuperCls.isOWLNothing();
				}
				return false;
			}

			@Override
			public OWLAxiom generateConclusion(List<OWLAxiom> premises) {
				
				if (matchPremises(premises)) {
	
					OWLAxiom premise = premises.get(0);
					OWLClassExpression premiseSubCls = ((OWLSubClassOfAxiom) premise).getSubClass();
					OWLDataFactory dataFactory = new OWLDataFactoryImpl();
					OWLSubClassOfAxiom conclusion = new OWLSubClassOfAxiomImpl(premiseSubCls, dataFactory.getOWLNothing(), new ArrayList<OWLAnnotation>());				
					
					return conclusion;		
				}
				return null;
			}					
		};	
		
		
		
		// RULE 11
		InferenceRule rule11 = 
				new InferenceRule("11", "ObjCom-2", 1) {
			
			@Override	
			public boolean matchPremises(List<OWLAxiom> premises) {
				
				OWLAxiom premise = premises.get(0);
				return premise.isOfType(AxiomType.SUBCLASS_OF);
			}
			
			@Override	
			public boolean matchPremisesAndConclusion(List<OWLAxiom> premises, OWLAxiom conclusion) {

				if (matchPremises(premises) && conclusion.isOfType(AxiomType.SUBCLASS_OF)) {
					
					OWLAxiom premise = premises.get(0);
					OWLClassExpression premiseSubCls = ((OWLSubClassOfAxiom) premise).getSubClass();
					OWLClassExpression premiseSuperCls = ((OWLSubClassOfAxiom) premise).getSuperClass();
					OWLClassExpression conclusionSubCls = ((OWLSubClassOfAxiom) conclusion).getSubClass();
					OWLClassExpression conclusionSuperCls = ((OWLSubClassOfAxiom) conclusion).getSuperClass();
					
					if (conclusionSubCls.isOWLThing() &&
							conclusionSuperCls.getClassExpressionType().equals(ClassExpressionType.OBJECT_UNION_OF)) {
						
						Set<OWLClassExpression> conclusionSuperClsUnion = ((OWLObjectUnionOf) conclusionSuperCls).getOperands();

						return (conclusionSuperClsUnion.size() == 2) 
								&& conclusionSuperClsUnion.contains(premiseSubCls.getObjectComplementOf())
								&& conclusionSuperClsUnion.contains(premiseSuperCls);
					}
				}
				return false;
			}

			@Override
			public OWLAxiom generateConclusion(List<OWLAxiom> premises) {
				
				if (matchPremises(premises)) {
	
					OWLAxiom premise = premises.get(0);
					OWLClassExpression premiseSubCls = ((OWLSubClassOfAxiom) premise).getSubClass();
					OWLClassExpression premiseSuperCls = ((OWLSubClassOfAxiom) premise).getSuperClass();

					Set<OWLClassExpression> operands = new HashSet<OWLClassExpression>();
					operands.add(premiseSubCls.getObjectComplementOf());
					operands.add(premiseSuperCls);
					OWLClassExpression union = new OWLObjectUnionOfImpl(operands);		
					
					OWLDataFactory dataFactory = new OWLDataFactoryImpl();
					OWLSubClassOfAxiom conclusion = new OWLSubClassOfAxiomImpl(dataFactory.getOWLThing(), union, new ArrayList<OWLAnnotation>());				
					
					return conclusion;		
				}
				return null;
			}					
		};	
		
		
		
		// RULE 25, 1st variant
		InferenceRule rule25_1 = 
				new InferenceRule("25.1", "ObjDom-ObjAll", 2) {
			
			@Override	
			public boolean matchPremises(List<OWLAxiom> premises) {
				
				OWLAxiom premise1 = premises.get(0);
				OWLAxiom premise2 = premises.get(1);

				if (premise1.isOfType(AxiomType.OBJECT_PROPERTY_DOMAIN) && premise2.isOfType(AxiomType.SUBCLASS_OF)) {			
					
					OWLObjectPropertyExpression property = ((OWLObjectPropertyDomainAxiom) premise1).getProperty();
					OWLClassExpression classExpression = ((OWLObjectPropertyDomainAxiom) premise1).getDomain();
					OWLClassExpression secondPremSubCls = ((OWLSubClassOfAxiom) premise2).getSubClass();
					OWLClassExpression secondPremSuperCls = ((OWLSubClassOfAxiom) premise2).getSuperClass();
					
					if (secondPremSubCls.getClassExpressionType().equals(ClassExpressionType.DATA_ALL_VALUES_FROM)) {
			
						OWLObjectPropertyExpression secondPremSubClsProp = ((OWLObjectAllValuesFrom) secondPremSubCls).getProperty();
						OWLClassExpression secondPremSubClsClassExp = ((OWLObjectAllValuesFrom) secondPremSubCls).getFiller();
						
						return secondPremSubClsClassExp.isOWLNothing() && secondPremSubClsProp.equals(property)
								&& secondPremSuperCls.equals(classExpression);
					}
				}

				return false;
			}
			
			@Override	
			public boolean matchPremisesAndConclusion(List<OWLAxiom> premises, OWLAxiom conclusion) {

				if (matchPremises(premises)) {
				
					OWLAxiom premise2 = premises.get(1);
					
					if (conclusion.isOfType(AxiomType.SUBCLASS_OF)) {
					
						OWLClassExpression secondPremSuperCls = ((OWLSubClassOfAxiom) premise2).getSuperClass();
						OWLClassExpression conclusionSuperCls = ((OWLSubClassOfAxiom) conclusion).getSuperClass();
						OWLClassExpression conclusionSubCls = ((OWLSubClassOfAxiom) conclusion).getSubClass();
								
						return conclusionSuperCls.equals(secondPremSuperCls) && conclusionSubCls.isOWLThing();
					}		
				}
				
				return false;
			}

			@Override
			public OWLAxiom generateConclusion(List<OWLAxiom> premises) {
				
				if (matchPremises(premises)) {
					
					OWLAxiom premise2 = premises.get(1);
					OWLClassExpression secondPremSuperCls = ((OWLSubClassOfAxiom) premise2).getSuperClass();										
					OWLDataFactory dataFactory = new OWLDataFactoryImpl();
					OWLSubClassOfAxiom conclusion = new OWLSubClassOfAxiomImpl(dataFactory.getOWLThing(), secondPremSuperCls, new ArrayList<OWLAnnotation>());				
					
					return conclusion;
				}
				
				return null;
			}					
		};	
		
		
		// RULE 25, 2nd variant
		InferenceRule rule25_2 = 
				new InferenceRule("25.2", "ObjDom-ObjAll", 2) {
			
			@Override	
			public boolean matchPremises(List<OWLAxiom> premises) {
				
				OWLAxiom premise1 = premises.get(0);
				OWLAxiom premise2 = premises.get(1);

				if (premise1.isOfType(AxiomType.SUBCLASS_OF) && premise2.isOfType(AxiomType.SUBCLASS_OF)) {			
					
					OWLClassExpression firstPremSubCls = ((OWLSubClassOfAxiom) premise1).getSubClass();
					OWLClassExpression firstPremSuperCls = ((OWLSubClassOfAxiom) premise1).getSuperClass();
					OWLClassExpression secondPremSubCls = ((OWLSubClassOfAxiom) premise2).getSubClass();
					OWLClassExpression secondPremSuperCls = ((OWLSubClassOfAxiom) premise2).getSuperClass();
					
					if (secondPremSubCls.getClassExpressionType().equals(ClassExpressionType.DATA_ALL_VALUES_FROM)
						&& firstPremSubCls.getClassExpressionType().equals(ClassExpressionType.DATA_SOME_VALUES_FROM)) {

						OWLObjectPropertyExpression firstPremSubClsProp = ((OWLObjectSomeValuesFrom) firstPremSubCls).getProperty();
						OWLClassExpression firstPremSubClsClassExp = ((OWLObjectSomeValuesFrom) firstPremSubCls).getFiller();
						OWLObjectPropertyExpression secondPremSubClsProp = ((OWLObjectAllValuesFrom) secondPremSubCls).getProperty();
						OWLClassExpression secondPremSubClsClassExp = ((OWLObjectAllValuesFrom) secondPremSubCls).getFiller();
						
						return firstPremSubClsClassExp.isOWLThing() 
								&& secondPremSubClsClassExp.isOWLNothing() 
								&& secondPremSubClsProp.equals(firstPremSubClsProp)
								&& secondPremSuperCls.equals(firstPremSuperCls);
					}
				}

				return false;
			}
			
			@Override	
			public boolean matchPremisesAndConclusion(List<OWLAxiom> premises, OWLAxiom conclusion) {

				if (matchPremises(premises)) {
				
					OWLAxiom premise2 = premises.get(1);
					
					if (conclusion.isOfType(AxiomType.SUBCLASS_OF)) {
					
						OWLClassExpression secondPremSuperCls = ((OWLSubClassOfAxiom) premise2).getSuperClass();
						OWLClassExpression conclusionSuperCls = ((OWLSubClassOfAxiom) conclusion).getSuperClass();
						OWLClassExpression conclusionSubCls = ((OWLSubClassOfAxiom) conclusion).getSubClass();
								
						return conclusionSuperCls.equals(secondPremSuperCls) && conclusionSubCls.isOWLThing();
					}		
				}
				
				return false;
			}

			
			@Override
			public OWLAxiom generateConclusion(List<OWLAxiom> premises) {
				
				if (matchPremises(premises)) {
					
					OWLAxiom premise2 = premises.get(1);
					OWLClassExpression secondPremSuperCls = ((OWLSubClassOfAxiom) premise2).getSuperClass();										
					OWLDataFactory dataFactory = new OWLDataFactoryImpl();
					OWLSubClassOfAxiom conclusion = new OWLSubClassOfAxiomImpl(dataFactory.getOWLThing(), secondPremSuperCls, new ArrayList<OWLAnnotation>());				
					
					return conclusion;
				}
				
				return null;
			}					
		};	
		
		
		
		// RULE 26
		InferenceRule rule26 = 
				new InferenceRule("26", "SubObj-SubObj", 2) {
			
			@Override	
			public boolean matchPremises(List<OWLAxiom> premises) {
				
				OWLAxiom premise1 = premises.get(0);
				OWLAxiom premise2 = premises.get(1);

				if (premise1.isOfType(AxiomType.SUB_OBJECT_PROPERTY) && premise2.isOfType(AxiomType.SUB_OBJECT_PROPERTY)) {			

					OWLObjectPropertyExpression firstPremSuperProp = ((OWLSubObjectPropertyOfAxiom) premise1).getSuperProperty();
					OWLObjectPropertyExpression secondPremSubProp = ((OWLSubObjectPropertyOfAxiom) premise2).getSubProperty();
						
					return firstPremSuperProp.equals(secondPremSubProp);
				}

				return false;
			}
			
			@Override	
			public boolean matchPremisesAndConclusion(List<OWLAxiom> premises, OWLAxiom conclusion) {

				if (matchPremises(premises)) {
				
					OWLAxiom premise1 = premises.get(0);
					OWLAxiom premise2 = premises.get(1);
					
					if (conclusion.isOfType(AxiomType.SUB_OBJECT_PROPERTY)) {

						OWLObjectPropertyExpression firstPremSubProp = ((OWLSubObjectPropertyOfAxiom) premise1).getSubProperty();
						OWLObjectPropertyExpression secondPremSuperProp = ((OWLSubObjectPropertyOfAxiom) premise2).getSuperProperty();
						OWLObjectPropertyExpression conclusionPremSuperProp = ((OWLSubObjectPropertyOfAxiom) conclusion).getSuperProperty();
						OWLObjectPropertyExpression conclusionPremSubProp = ((OWLSubObjectPropertyOfAxiom) conclusion).getSubProperty();
								
						return conclusionPremSuperProp.equals(secondPremSuperProp)
								&& conclusionPremSubProp.equals(firstPremSubProp);
					}		
				}
				
				return false;
			}

			
			@Override
			public OWLAxiom generateConclusion(List<OWLAxiom> premises) {
				
				if (matchPremises(premises)) {
					
					OWLAxiom premise1 = premises.get(0);
					OWLAxiom premise2 = premises.get(1);
					OWLObjectPropertyExpression firstPremSubProp = ((OWLSubObjectPropertyOfAxiom) premise1).getSubProperty();
					OWLObjectPropertyExpression secondPremSuperProp = ((OWLSubObjectPropertyOfAxiom) premise2).getSuperProperty();
					OWLSubObjectPropertyOfAxiom conclusion = new OWLSubObjectPropertyOfAxiomImpl(firstPremSubProp, secondPremSuperProp, new ArrayList<OWLAnnotation>());				
					
					return conclusion;
				}
				
				return null;
			}					
		};	
		
		
		
		
		
		
		// RULE 39
		InferenceRule rule39 = 
				new InferenceRule("39", "SubCls-SubCls-1", 2) {
			
			@Override	
			public boolean matchPremises(List<OWLAxiom> premises) {
				
				OWLAxiom premise1 = premises.get(0);
				OWLAxiom premise2 = premises.get(1);

				if (premise1.isOfType(AxiomType.SUBCLASS_OF) && premise2.isOfType(AxiomType.SUBCLASS_OF)) {			

					OWLClassExpression firstPremSuperCls = ((OWLSubClassOfAxiom) premise1).getSuperClass();
					OWLClassExpression secondPremSubCls = ((OWLSubClassOfAxiom) premise2).getSubClass();
					
					return firstPremSuperCls.equals(secondPremSubCls);
				}

				return false;
			}
			
			@Override	
			public boolean matchPremisesAndConclusion(List<OWLAxiom> premises, OWLAxiom conclusion) {

				OWLAxiom premise1 = premises.get(0);
				OWLAxiom premise2 = premises.get(1);

				if (premise1.isOfType(AxiomType.SUBCLASS_OF) && premise2.isOfType(AxiomType.SUBCLASS_OF)
						&& conclusion.isOfType(AxiomType.SUBCLASS_OF)) {			

					OWLClassExpression firstPremSuperCls = ((OWLSubClassOfAxiom) premise1).getSuperClass();
					OWLClassExpression firstPremSubCls = ((OWLSubClassOfAxiom) premise1).getSubClass();
					OWLClassExpression secondPremSuperCls = ((OWLSubClassOfAxiom) premise2).getSuperClass();
					OWLClassExpression secondPremSubCls = ((OWLSubClassOfAxiom) premise2).getSubClass();
					OWLClassExpression conclusionSuperCls = ((OWLSubClassOfAxiom) conclusion).getSuperClass();
					OWLClassExpression conclusionSubCls = ((OWLSubClassOfAxiom) conclusion).getSubClass();
					
					return (firstPremSuperCls.equals(secondPremSubCls) 
							&& firstPremSubCls.equals(conclusionSubCls) 
							&& secondPremSuperCls.equals(conclusionSuperCls));
				}

				return false;
			}

			@Override
			public OWLAxiom generateConclusion(List<OWLAxiom> premises) {
				
				if (matchPremises(premises)) {
					OWLAxiom premise1 = premises.get(0);
					OWLAxiom premise2 = premises.get(1);

					OWLClassExpression firstPremSubCls = ((OWLSubClassOfAxiom) premise1).getSubClass();
					OWLClassExpression secondPremSuperCls = ((OWLSubClassOfAxiom) premise2).getSuperClass();
					OWLSubClassOfAxiom conclusion = new OWLSubClassOfAxiomImpl(firstPremSubCls, secondPremSuperCls, new ArrayList<OWLAnnotation>());
					
					return conclusion;
				}
				
				return null;
			}					
		};	
		
		
		
		// RULE 40
		InferenceRule rule40 = 
				new InferenceRule("40", "SubCls-SubCls-2", 2) {
			
			@Override	
			public boolean matchPremises(List<OWLAxiom> premises) {
				
				OWLAxiom premise1 = premises.get(0);
				OWLAxiom premise2 = premises.get(1);

				if (premise1.isOfType(AxiomType.SUBCLASS_OF) && premise2.isOfType(AxiomType.SUBCLASS_OF)) {			

					OWLClassExpression firstPremSubCls = ((OWLSubClassOfAxiom) premise1).getSubClass();
					OWLClassExpression secondPremSubCls = ((OWLSubClassOfAxiom) premise2).getSubClass();
					
					return firstPremSubCls.equals(secondPremSubCls);
				}

				return false;
			}
			
			@Override	
			public boolean matchPremisesAndConclusion(List<OWLAxiom> premises, OWLAxiom conclusion) {

				OWLAxiom premise1 = premises.get(0);
				OWLAxiom premise2 = premises.get(1);

				if (premise1.isOfType(AxiomType.SUBCLASS_OF) && premise2.isOfType(AxiomType.SUBCLASS_OF)
						&& conclusion.isOfType(AxiomType.SUBCLASS_OF)) {			

					OWLClassExpression firstPremSuperCls = ((OWLSubClassOfAxiom) premise1).getSuperClass();
					OWLClassExpression firstPremSubCls = ((OWLSubClassOfAxiom) premise1).getSubClass();
					OWLClassExpression secondPremSuperCls = ((OWLSubClassOfAxiom) premise2).getSuperClass();
					OWLClassExpression secondPremSubCls = ((OWLSubClassOfAxiom) premise2).getSubClass();
					OWLClassExpression conclusionSuperCls = ((OWLSubClassOfAxiom) conclusion).getSuperClass();
					OWLClassExpression conclusionSubCls = ((OWLSubClassOfAxiom) conclusion).getSubClass();

					Set<OWLClassExpression> axiomSet = new HashSet<OWLClassExpression>();
					axiomSet.add(firstPremSuperCls);
					axiomSet.add(secondPremSuperCls);					
					OWLClassExpression intersection = new OWLObjectIntersectionOfImpl(axiomSet);
					
					return (firstPremSubCls.equals(secondPremSubCls) 
							&& firstPremSubCls.equals(conclusionSubCls)
							&& conclusionSuperCls.equals(intersection));
				}

				return false;
			}

			@Override
			public OWLAxiom generateConclusion(List<OWLAxiom> premises) {
				
				if (matchPremises(premises)) {
					
					OWLAxiom premise1 = premises.get(0);
					OWLAxiom premise2 = premises.get(1);
					
					OWLClassExpression firstPremSuperCls = ((OWLSubClassOfAxiom) premise1).getSuperClass();
					OWLClassExpression firstPremSubCls = ((OWLSubClassOfAxiom) premise1).getSubClass();
					OWLClassExpression secondPremSuperCls = ((OWLSubClassOfAxiom) premise2).getSuperClass();
					
					Set<OWLClassExpression> axiomSet = new HashSet<OWLClassExpression>();
					axiomSet.add(firstPremSuperCls);
					axiomSet.add(secondPremSuperCls);					
					OWLClassExpression conclusionSuperCls = new OWLObjectIntersectionOfImpl(axiomSet);
					
					OWLSubClassOfAxiom conclusion = new OWLSubClassOfAxiomImpl(firstPremSubCls, conclusionSuperCls, new ArrayList<OWLAnnotation>());
					
					return conclusion;
				}
				
				return null;
			}					
		};	

		
		
		// RULE 43
		InferenceRule rule43 = 
				new InferenceRule("43", "ObjSom-SubCls", 2) {
			
			@Override	
			public boolean matchPremises(List<OWLAxiom> premises) {
				
				OWLAxiom premise1 = premises.get(0);
				OWLAxiom premise2 = premises.get(1);

				if (premise1.isOfType(AxiomType.SUBCLASS_OF) && premise2.isOfType(AxiomType.SUBCLASS_OF)) {			

					OWLClassExpression firstPremSuperCls = ((OWLSubClassOfAxiom) premise1).getSuperClass();
					OWLClassExpression secondPremSubCls = ((OWLSubClassOfAxiom) premise2).getSubClass();
					
					return firstPremSuperCls.getClassExpressionType().equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM)
							&& ((OWLObjectSomeValuesFrom) firstPremSuperCls).getFiller().equals(secondPremSubCls);				
				}

				return false;
			}
			
			@Override	
			public boolean matchPremisesAndConclusion(List<OWLAxiom> premises, OWLAxiom conclusion) {
				
				OWLAxiom premise1 = premises.get(0);
				OWLAxiom premise2 = premises.get(1);

				if (premise1.isOfType(AxiomType.SUBCLASS_OF) && premise2.isOfType(AxiomType.SUBCLASS_OF)
						&& conclusion.isOfType(AxiomType.SUBCLASS_OF)) {			

					OWLClassExpression firstPremSuperCls = ((OWLSubClassOfAxiom) premise1).getSuperClass();
					OWLClassExpression firstPremSubCls = ((OWLSubClassOfAxiom) premise1).getSubClass();
					OWLClassExpression secondPremSuperCls = ((OWLSubClassOfAxiom) premise2).getSuperClass();
					OWLClassExpression secondPremSubCls = ((OWLSubClassOfAxiom) premise2).getSubClass();
					OWLClassExpression conclusionSuperCls = ((OWLSubClassOfAxiom) conclusion).getSuperClass();
					OWLClassExpression conclusionSubCls = ((OWLSubClassOfAxiom) conclusion).getSubClass();
					
					return firstPremSubCls.equals(conclusionSubCls) &&					
							firstPremSuperCls.getClassExpressionType().equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM)
							&& ((OWLObjectSomeValuesFrom) firstPremSuperCls).getFiller().equals(secondPremSubCls)			
							&& conclusionSuperCls.getClassExpressionType().equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM)
							&& ((OWLObjectSomeValuesFrom) conclusionSuperCls).getFiller().equals(secondPremSuperCls);				
				}

				return false;
			}

			@Override
			public OWLAxiom generateConclusion(List<OWLAxiom> premises) {
				
				if (matchPremises(premises)) {
					
					OWLAxiom premise1 = premises.get(0);
					OWLAxiom premise2 = premises.get(1);
					
					OWLClassExpression firstPremSubCls = ((OWLSubClassOfAxiom) premise1).getSubClass();
					OWLObjectSomeValuesFrom firstPremSuperCls = (OWLObjectSomeValuesFrom)((OWLSubClassOfAxiom) premise1).getSuperClass();
					OWLClassExpression secondPremSuperCls = ((OWLSubClassOfAxiom) premise2).getSuperClass();
					
					OWLObjectSomeValuesFrom conclusionSuperCls = new OWLObjectSomeValuesFromImpl(firstPremSuperCls.getProperty(), secondPremSuperCls);
					OWLSubClassOfAxiom conclusion = new OWLSubClassOfAxiomImpl(firstPremSubCls, conclusionSuperCls, new ArrayList<OWLAnnotation>());
					
					return conclusion;
				}
				
				return null;
			}					
		};	
		
		
		
		rules.get(1).add(rule1);
		rules.get(1).add(rule6);
		rules.get(2).add(rule39);
		rules.get(2).add(rule40);
		rules.get(2).add(rule43);

	}	
}
