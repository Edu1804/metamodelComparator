package dbObjects;

import java.util.Map;

public class DbTable extends DbObject{
	
	private Map<String, String> db_metadata;

	public DbTable(String name) {
		super(name);
	}

	public Map<String, String> getDb_metadata() {
		return db_metadata;
	}

	public void setDb_metadata(Map<String, String> db_metadata) {
		this.db_metadata = db_metadata;
	}

}
