package RuleRestrictions;

// This class holds identifiers of either datatypes, or of literals.
public class DisjointDatatypesRestriction implements RuleRestriction {
	private String datatype1;
	private String datatype2;
	
	public DisjointDatatypesRestriction(String datatype1, String datatype2) {
		this.datatype1 = datatype1;
		this.datatype2 = datatype2;
	}
	
	public String getFirstDataProperty() { return datatype1; }
	
	public String getSecondDataProperty() { return datatype2; }	

}
