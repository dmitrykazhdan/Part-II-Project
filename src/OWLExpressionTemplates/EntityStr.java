package OWLExpressionTemplates;

import java.util.List;

import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.EntityType;

public class EntityStr  implements GenericExpStr {
	
	private String value;
	private EntityType entityType;
	
	public EntityStr(String value, EntityType entityType) {
		this.entityType = entityType;
		this.value = value;	
	}

	public EntityType getEntityType() {
		return entityType;
	}
	
	public String getAtomic() {
		return value;
	}
}
