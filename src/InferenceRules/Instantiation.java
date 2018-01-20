package InferenceRules;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObject;

public class Instantiation {
	private Map<String, OWLObject> variableInstantiation;
	private Map<String, Integer> cardinalityInstantiation;
	private Map<String, Set<OWLClassExpression>> groupInstantiation;

	
	public Instantiation() {
		this.variableInstantiation = new HashMap<String, OWLObject>();
		this.cardinalityInstantiation = new HashMap<String, Integer>();
		this.groupInstantiation = new HashMap<String, Set<OWLClassExpression>>();		
	}
	
	public Instantiation(Map<String, OWLObject> variableInstantiation, Map<String, Integer> cardinalityInstantiation, Map<String, Set<OWLClassExpression>> groupInstantiation) {
		this.variableInstantiation = variableInstantiation;
		this.cardinalityInstantiation = cardinalityInstantiation;
		this.groupInstantiation = groupInstantiation;		
	}
	
	public Instantiation(Instantiation instantiation) {
		this.variableInstantiation = new HashMap<String, OWLObject>(instantiation.getVariableInstantiation());
		this.groupInstantiation = new HashMap<String, Set<OWLClassExpression>>(instantiation.getGroupInstantiation());
		this.cardinalityInstantiation =	new HashMap<String, Integer>(instantiation.getCardinalityInstantiation());
	}
	
	
	public Map<String, OWLObject> getVariableInstantiation() { return variableInstantiation; }
	
	public Map<String, Integer> getCardinalityInstantiation() { return cardinalityInstantiation; }
	
	public Map<String, Set<OWLClassExpression>> getGroupInstantiation() { return groupInstantiation; }
}
