package OWLExpressionTemplates;

import org.semanticweb.owlapi.model.EntityType;

public class DataRangeTemplatePrimitive implements TemplatePrimitive {

	private String value;
	
	public DataRangeTemplatePrimitive(String value) {
		this.value = value;	
	}

	public String getAtomic() {
		return value;
	}
}
