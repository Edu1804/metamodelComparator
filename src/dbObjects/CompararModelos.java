package dbObjects;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class CompararModelos {

	private ArrayList<DbObject> objectsNotfound = new ArrayList<>();
	private ArrayList<DbObject> objectsToAdd = new ArrayList<>();

	/**
	 * ¿Qué hace?
	 * -> Comparamos el primer nivel para asegurarnos de que las bd son iguales
	 * -> En caso de ser iguales, se procede a comparar el resto de objetos
	 * @param excelCopy2
	 * @param serverCopy
	 * @param selFile 
	 * @param pathFile 
	 * @param serverName 
	 * @return 
	 */
	public void comparacionRoot(DbObject excelCopy, DbObject serverCopy, File selFile, String bdName, String pathFile){
		for(String keyFile : serverCopy.collectedDbObj.keySet()) {
			for ( String keyExcel : excelCopy.collectedDbObj.keySet() ) {
				if(keyFile.equals(keyExcel)) {
					comparacionServidor(excelCopy.getCollectedDbObj().get(keyExcel), serverCopy.getCollectedDbObj().get(keyFile));
				}
			}
		}
		createFile(selFile);
		applyChangesFile(objectsNotfound, objectsToAdd, serverCopy, selFile);
		System.out.println("Comparación terminada");
	}

	/**
	 * ¿Qué hace?
	 * -> Crea el archivo con las diferencias de los metamodelos
	 * @param selFile 
	 */
	private void createFile(File selFile) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(selFile+"\\comparador-log.csv", "UTF-8");
			writer.println("DIFERENCIAS ENTRE LOS METAMODELOS");
			writer.println("ACCION, ARCHIVO_PADRE, NOMBRE");
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ¿Qué hace?
	 * -> Guardamos en el archivo de las diferencias los directorios a eliminar porque no han sido encontrados en el Excel
	 * @param objectsNotfound
	 * @param objectsToAdd2 
	 * @param fileDB
	 * @param selFile  
	 */
	private void applyChangesFile(ArrayList<DbObject> objectsNotfound, ArrayList<DbObject> objectsToAdd, DbObject fileDB, File selFile) {
		try {
			System.out.println(selFile);
			BufferedWriter out = new BufferedWriter(
					new FileWriter(selFile+"\\comparador-log.csv", true));
			//imprimir los que no se han encontrado en el sistema de ficheros
			if(!objectsNotfound.isEmpty()) {
				for (int i = 0; i < objectsNotfound.size(); i++) {
					out.write("DELETE,"+fileDB.getName()+","+objectsNotfound.get(i).getName()+"\n");
				}
			}
			if(!objectsToAdd.isEmpty()) {
				for (int i = 0; i < objectsToAdd.size(); i++) {
					out.write("INSERT,"+fileDB.getName()+","+objectsToAdd.get(i).getName()+"\n");
				}
			}
			out.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * ¿Qué hace?
	 * -> Compara de forma recursiva cada nivel del árbol, en caso de ser distintos, guarda los datos a añadir y/o eliminar
	 * @param excelBD
	 * @param fileBD
	 */
	private void comparacionServidor(DbObject excelBD, DbObject fileDB) {
		if(fileDB.collectedDbObj != null && !fileDB.collectedDbObj.isEmpty()) {
			for(String keyFile : fileDB.collectedDbObj.keySet()) {
				if(excelBD.collectedDbObj.containsKey(keyFile)) {
					excelBD.getCollectedDbObj().get(keyFile);
					//volvemos a llamar a la función de forma recursiva para que analice el siguiente nivel
					comparacionServidor(excelBD.getCollectedDbObj().get(keyFile), fileDB.getCollectedDbObj().get(keyFile));
					//funcion no implementada pero diseñada para posteriores desarrollos que incluyan propiedades específicas sobre el metadata
					compareMetadada(excelBD.getCollectedDbObj().get(keyFile), fileDB.getCollectedDbObj().get(keyFile));
				} else {
					//si no se encuentra el objeto de la estructura de ficheros se guarda para eliminarlo posteriormente
					objectsNotfound.add(fileDB.getCollectedDbObj().get(keyFile));
				}
			}
			if(excelBD.collectedDbObj != null && !excelBD.collectedDbObj.isEmpty()) {
				for(String keyFile2 : excelBD.collectedDbObj.keySet()) {
					if(fileDB.collectedDbObj.containsKey(keyFile2)) {
						fileDB.getCollectedDbObj().get(keyFile2);
						//volvemos a llamar a la función de forma recursiva para que analice el siguiente nivel
						comparacionServidor(excelBD.getCollectedDbObj().get(keyFile2), fileDB.getCollectedDbObj().get(keyFile2));
					} else {
						//si no se encuentra el objeto de la estructura de ficheros se guarda para eliminarlo posteriormente
						objectsToAdd.add(excelBD.getCollectedDbObj().get(keyFile2));
					}
				}
			}

		}
	}


	private void compareMetadada(DbObject dbObject, DbObject dbObject2) {

	}

}
