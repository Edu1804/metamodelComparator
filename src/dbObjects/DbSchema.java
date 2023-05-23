package dbObjects;

import java.util.Map;

public class DbSchema extends DbObject{

	private Map<String, String> db_tables;
	
	public DbSchema(String name) {
		super(name);
	}

	public Map<String, String> getDb_tables() {
		return db_tables;
	}

	public void setDb_tables(Map<String, String> db_tables) {
		this.db_tables = db_tables;
	}
}
