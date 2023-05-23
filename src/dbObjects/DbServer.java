package dbObjects;

import java.util.Map;

public class DbServer extends DbObject {

	private Map<String, String> dbDb;
	
	public DbServer(String name) {
		super(name);
	}

	public Map<String, String> getDb_db() {
		return dbDb;
	}

	public void setDb_db(Map<String, String> db_db) {
		this.dbDb = db_db;
	}

}
