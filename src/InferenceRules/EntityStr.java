package InferenceRules;

import java.util.List;

import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.EntityType;

public class EntityStr  implements GenericExpStr {
	
	private String atomic;
	private EntityType entityType;
	
	public EntityStr(String atomic, EntityType entityType) {
		this.entityType = entityType;
		this.atomic = atomic;	
	}

	public EntityType getEntityType() {
		return entityType;
	}
	
	public String getAtomic() {
		return atomic;
	}
}
