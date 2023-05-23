package dbObjects;

import java.io.File;

public class ApplyDiferencial {

	/**
	 * ¿Qué hace?
	 * -> Comienza a aplicar el diferencial sobre la estructura de ficheros
	 * @param diferencial
	 * @param serverCopy
	 * @param pathFile 
	 */
	public void completeDiferencial(DbObject excelCopy, DbObject serverCopy, String pathFile, boolean includeDelete, boolean includeInserts) {
		File[] directories = new File(pathFile).listFiles(File::isDirectory);
		if(directories!=null) {
			String serverName = directories[0].getName();
			if(excelCopy.collectedDbObj.containsKey(serverName)) {
				completeDiferencialGeneric(directories[0], 0, excelCopy.getCollectedDbObj().get(serverName), includeDelete, includeInserts);
				System.out.println("Diferencial aplicado sobre la estructura de ficheros");
				
			}else {
				System.err.println("El diferencial no puede ser aplicado en la ruta especificada");
			}
		}else {
			System.err.println("No se encuentra la base de datos, revise la ruta dada ");
		}
	}

	/**
	 * ¿Qué hace?
	 * -> Compara de forma recursiva cada nivel del árbol, ne caso de ser distintos, guarda los datos a añadir y/o eliminar
	 * @param excelBD
	 * @param fileBD
	 */
	private void completeDiferencialGeneric(File directories, int i, DbObject object, boolean includeDelete, boolean includeInserts) {
		//si la condición del if (i) se cambia a i<4, vale para revisar los campos de las tablas
		i++;
		if(i<3) {
			File[] subDirectories= new File(directories.getPath()).listFiles(File::isDirectory);
			for (int j = 0; j < subDirectories.length; j++) {
				//si no están vacíos
				if(object.collectedDbObj != null && !object.collectedDbObj.isEmpty()) {
					//en caso de que sean iguales, entra dentro para revisar
					if(object.collectedDbObj.containsKey(subDirectories[j].getName())) {
						completeDiferencialGeneric(subDirectories[j], i, object.getCollectedDbObj().get(subDirectories[j].getName()), includeDelete, includeInserts);
						object.removeCollectedObj(object.collectedDbObj.get(subDirectories[j].getName()));
					}else {
						if(includeDelete) {
							deleteDirectory(subDirectories[j]);
						}

					}
				}
			}
			if(object!=null && object.collectedDbObj != null && !object.collectedDbObj.isEmpty() && includeInserts) {
				for(String keyFile : object.collectedDbObj.keySet()) {
					String pathAux=changeDirectoryName(directories, keyFile);
					addDirectory(pathAux);
					File f = new File(pathAux);
					completeDiferencialGeneric(f, i, object.getCollectedDbObj().get(f.getName()), includeDelete, includeInserts);
				}
			}
		}else {
			//esta rama ha sido terminada
		}
	}

	/**
	 * ¿Qué hace?
	 * -> Devuelve el path de una ruta añadiendo un nombre dado a la ruta
	 * @param directories
	 * @param name
	 */
	private String changeDirectoryName(File directories, String name) {
		if(directories==null) {
			return null;
		}
		String character="\\";
		return directories.toString()+character+name;
	}

	/**
	 * ¿Qué hace?
	 * -> Crea un directorio a partir de una ruta dada
	 * @param path
	 */
	private void addDirectory(String path) {
		File directories = new File(path);
		try{
			if(directories.mkdir()) { 
				//System.out.println("Directory Created");
			} else {
				System.out.println("Directory is not created");
			}
		} catch(Exception e){
			e.printStackTrace();
		} 
	}

	/**
	 * ¿Qué hace?
	 * -> Elimina un directorio dada su ruta
	 * @param folder
	 */
	public static void deleteDirectory(File folder) {
		File[] files = folder.listFiles();
		if(files!=null) { //some JVMs return null for empty dirs
			for(File f: files) {
				if(f.isDirectory()) {
					deleteDirectory(f);
				} else {
					f.delete();
				}
			}
		}
		folder.delete();
	}




}
