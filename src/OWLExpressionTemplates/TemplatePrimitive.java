package OWLExpressionTemplates;

public abstract class TemplatePrimitive implements GenericExpStr {
	
	private String name;
	
	public TemplatePrimitive(String name) {
		this.name = name;	
	}

	public String getAtomic() {
		return name;
	}
}
