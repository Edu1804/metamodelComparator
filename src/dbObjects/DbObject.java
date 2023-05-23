package dbObjects;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class DbObject {

	protected String name;
	protected Map<String, DbObject> collectedDbObj = null;		
	protected Map<String, String> collectedAttributes = null;
	protected DbMetadata metadata = new DbMetadata();
	
	public DbMetadata getMetadata() {
		return metadata;
	}

	public void setMetadata(DbMetadata metadata) {
		this.metadata = metadata;
	}
	
	public DbObject() {
		super();
	}
	
	public DbObject(String name) {
		super();
		this.name=name;
		return;
	}

	public void addCollectedObj (DbObject obj) {
		if (this.collectedDbObj == null) {
			this.collectedDbObj = new HashMap<String, DbObject> ();
		}
		this.collectedDbObj.put(obj.name, obj);
		return;
	}

	public void removeCollectedObj (DbObject obj) {
		if (this.collectedDbObj == null) {
			this.collectedDbObj = new HashMap<String, DbObject> ();
		}
		this.collectedDbObj.remove(obj.name, obj);

	}

	public void addMetadataProperty(String metadataKey, Object metadataValue) {
		metadata.put(metadataKey, metadataValue);
		return;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, DbObject> getCollectedDbObj() {
		return collectedDbObj;
	}

	public void setCollectedDbObj(Map<String, DbObject> collectedDbObj) {
		this.collectedDbObj = collectedDbObj;
	}

	/**
	 * ¿Qué hace?
	 * -> Crea un fichero CSV que contendrá el resumen del Excel
	 * @param selFile 
	 * @param dbServer 
	 */
	public void saveDissocToFile(File selFile, DbObject dbServer) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(selFile+"\\csvdata.csv", "UTF-8");
			writer.println("SCHEMA,TABLA,NOMBRE_CAMPO,TIPO,ALGORITMO,TIPO_DISOCIADOR,DESCRIPCION");

			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		BufferedWriter out;
		try {
			System.out.println(selFile);
			out = new BufferedWriter(
					new FileWriter(selFile+"\\csvdata.csv", true));
			writeData(out, dbServer);
			out.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * ¿Qué hace?
	 * -> Obtiene el tipo de algoritmo dada una celda
	 * @param dbcolumn
	 * @param metadataKey 
	 */
	private String getAlgorithm(DbObject dbColumn, String metadataKey) {
		Object rValue = dbColumn.getMetadata().getMetadata().get(metadataKey);
		return rValue == null ? "": rValue.toString(); 
	}

	/**
	 * ¿Qué hace?
	 * -> Escribe en el CSV todo el resumen del Excel
	 * @param out
	 * @param server
	 */
	private void writeData(BufferedWriter out, DbObject server) {
		if(server.getCollectedDbObj() !=null) {
			for(DbObject dbase : server.getCollectedDbObj().values()) {
				for(DbObject dbschema : dbase.getCollectedDbObj().values()) {
					String schemaName = dbschema.getName();
					for(DbObject dbtable : dbschema.getCollectedDbObj().values()) {
						String tableName=dbtable.getName();
						for(DbObject dbColumn : dbtable.getCollectedDbObj().values()) {
							if(dbColumn.getMetadata().getMetadata().containsKey("ALGORITMO")) {
								try {
									out.write( 												  "\""
											+ schemaName									+ "\",\""												
											+ tableName 									+ "\",\""
											+ getAlgorithm(dbColumn, "NOMBRE_CAMPO")		+ "\",\""
											+ getAlgorithm(dbColumn, "TIPO")				+ "\",\""
											+ getAlgorithm(dbColumn, "ALGORITMO")			+ "\",\""
											+ getAlgorithm(dbColumn, "TIPO_DISOCIADOR")		+ "\",\""
											+ getAlgorithm(dbColumn, "DESCRIPCION")			+ "\""
											);		
									out.newLine();

								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		}
	}



}
