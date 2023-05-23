package dbObjects;

import java.util.HashMap;
import java.util.Map;

public class DbMetadata {
	
	protected Map<String, Object> metadata = new HashMap<String, Object>();

	public Map<String, Object> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
	}

	public void put(String metadataKey, Object metadataValue) {
		this.metadata.put(metadataKey, metadataValue);
		return;
	}
	
	
}
