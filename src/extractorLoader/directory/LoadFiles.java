package extractorLoader.directory;

import java.io.File;
import dbObjects.DbDb;
import dbObjects.DbObject;
import dbObjects.DbSchema;
import dbObjects.DbServer;
import dbObjects.DbTable;

public class LoadFiles {

	/**
	 * ¿Qué hace?
	 * -> Obtiene la ruta de la estructura de ficheros
	 * -> Comienza el recorrido de la estructura 
	 * @param path
	 */
	public DbServer initialFiles(String path) {
		File[] directories = new File(path).listFiles(File::isDirectory);
		if(directories==null) {
			return null;
		}else {
			String serverName = getParentName(directories[0]);
		DbServer server=new DbServer(serverName);
		readFiles(directories[0], 0, server);
		return server;
		}
		
	}

	/**
	 * ¿Qué hace?
	 * -> Lee de forma recursiva la estructura de ficheros recorriendo cada rama del árbol
	 * -> Por cada hoja disponible en el índice, llama al método para analizar las tablas.
	 * @param directory
	 * @param i
	 * @param object
	 */
	public void readFiles (File directory, int i, DbObject object) {
		DbObject levelObj = null;
		if(i<4) {	
			i++;	
			switch(i-1) {
			case 0: 
				//si estamos en el primer nivel, creamos el objeto server
				levelObj = new DbServer(directory.getName());
				object.addCollectedObj(levelObj);
				//System.out.println(object.getName());
				break;
			case 1:
				//si estamos en el segundo nivel, creamos el objeto db
				levelObj = new DbDb(directory.getName());
				object.addCollectedObj(levelObj);
				//System.out.println(levelObj.getName());
				break;
			case 2:
				//si estamos en el tercer nivel, creamos el objeto schema
				levelObj = new DbSchema(directory.getName());
				object.addCollectedObj(levelObj);
				//System.out.println(levelObj.getName());
				break;
			case 3:
				//si estamos en el cuarto nivel, creamos el objeto table
				//el nombre que le llega es schema.tabla, así nos quedamos solo con el nombre de la tabla
				//levelObj = new DbTable(directory.getName().substring(0, directory.getName().indexOf("."))); 
				levelObj = new DbTable(directory.getName());
				object.addCollectedObj(levelObj);
				//System.out.println(levelObj.getName());
				break;
			default:
				System.err.println("Revise la estructura del árbol");
				break;
			}
			//seguimos recorriendo el árbol hacia el siguiente nivel
			File[] subDirectories= new File(directory.getPath()).listFiles(File::isDirectory);
			for (int j = 0; j < subDirectories.length; j++) {
				readFiles(subDirectories[j], i, levelObj);
			}
		}
	} 

	/**
	 * ¿Qué hace?
	 * -> Obtiene el nombre del directorio padre dado una ruta
	 * @param directories
	 */
	private String getParentName(File directories) {
		String parentDirName = directories.getParent(); // to get the parent dir name
		parentDirName=parentDirName.substring(parentDirName.lastIndexOf("\\") + 1);
		return parentDirName;
	}

}
