package InferenceRules;

import OWLExpressionTemplates.InterUnion;

public class NaryClassExpressionSubset {
	
	private String subClass;
	private String superClass;
	
	public NaryClassExpressionSubset(String subClass, String superClass) {
		this.subClass = subClass;
		this.superClass = superClass;
	}
	
	public String getSubClass() { return subClass; }

	public String getSuperClass() { return superClass; }

}
