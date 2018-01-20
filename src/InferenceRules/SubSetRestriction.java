package InferenceRules;

import OWLExpressionTemplates.InterUnion;

// Checks whether one set of class expressions is a subset of another set.
public class SubSetRestriction implements RuleRestriction{
	
	private String subClass;
	private String superClass;
	
	public SubSetRestriction(String subClass, String superClass) {
		this.subClass = subClass;
		this.superClass = superClass;
	}
	
	public String getSubClass() { return subClass; }

	public String getSuperClass() { return superClass; }

}
