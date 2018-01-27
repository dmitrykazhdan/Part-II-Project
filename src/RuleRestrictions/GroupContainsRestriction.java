package RuleRestrictions;

public class GroupContainsRestriction implements RuleRestriction {
	String anonymousGroupName;
	String atomicClsName;
	
	public GroupContainsRestriction(String atomicClsName, String anonymousGroupName) {
		this.atomicClsName = atomicClsName;
		this.anonymousGroupName = anonymousGroupName;
	}
	
	public String getAnonymousGroupName() { return anonymousGroupName; }
	
	public String getAtomicClsName() { return atomicClsName; }

}
