package InferenceRules;

import OWLExpressionTemplates.InterUnion;

public class subSetRestriction implements RuleRestriction{
	
	private String subClass;
	private String superClass;
	
	public subSetRestriction(String subClass, String superClass) {
		this.subClass = subClass;
		this.superClass = superClass;
	}
	
	public String getSubClass() { return subClass; }

	public String getSuperClass() { return superClass; }

}
