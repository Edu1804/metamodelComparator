package extractorLoader.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import dbObjects.DbColumn;
import dbObjects.DbDb;
import dbObjects.DbSchema;
import dbObjects.DbServer;
import dbObjects.DbTable;
import dbObjects.dbMetadata.GenericColumnMetadata;
import dbObjects.dbMetadata.GenericTableMetadata;
import extractorLoader.excel.dbMetadata.ExcelNormalizedColumnMetadata;
import extractorLoader.excel.dbMetadata.ExcelNormalizedTableMetadata;

public class LoadExcel {

	private static final String tabla="TABLA";
	private static final String ddl="DDL";
	private static final String nombre_campo="NOMBRE_CAMPO";

	/**
	 * ¿Qué hace?
	 * -> Busca el excel a partir del path
	 * -> Inicializa los objetos de tipo server y bd
	 * @param path
	 * @param bdName 
	 * @param servidorName 
	 */
	public DbServer readExcel (String path, String servidorName, String bdName) {
		File f = new File(path);
		if(f.exists() && !f.isDirectory()) { 
			DbServer servidor= new DbServer(servidorName);
			DbDb bd = new DbDb(bdName);
			servidor.addCollectedObj(bd);
			readExcel1(path, bd);
			return servidor;
		}
		else {
			System.err.println("Archivo no encontrado. Por favor, revise que la ruta es correcta.");
			return null;
		}		
	}



	/**
	 * ¿Qué hace?
	 * -> Recorre el índice de la primera hoja del excel
	 * -> Por cada hoja disponible en el índice, llama al método para analizar las tablas.
	 * @param excelFilePath
	 * @param bd 
	 */
	private void readExcel1(String excelFilePath, DbDb bd) {
		try {
			FileInputStream inputstream = new FileInputStream(excelFilePath); 
			try {
				XSSFWorkbook workbook=new XSSFWorkbook(inputstream);
				XSSFSheet sheet = workbook.getSheetAt(0); //Accedemos a la hoja del índice
				//Buscamos la matriz de indexado del índice
				int lastRow 	= sheet.getLastRowNum();
				int lastRowFirstCell = sheet.getRow(lastRow).getFirstCellNum();
				boolean reachedFirstSheet = false;
				boolean firstIteration=true;
				//Para cada valor del índice, buscamos las hojas asociadas a dichos valores
				for (int i = lastRow; (i > 0) && (! reachedFirstSheet); i--) {
					XSSFRow row = sheet.getRow(i);
					if (row == null) {
						//Si la fila está vacía el método ha devuelto null
						continue;
					}
					XSSFCell cell = row.getCell(lastRowFirstCell);
					if (cell == null) {
						//Si la celda está vacía el método ha devuelto null
						continue;
					}
					String cellValue = cell.getRawValue();
					if (cellValue == null) {
						continue;
					}
					cellValue = cellValue.trim();
					//recorre la lista numerada de las hojas de excel de arriba hacia abajo, termina cuando encuentra la primera hoja (1)
					if ("1".equals(cellValue)) {
						reachedFirstSheet = true;
						//System.out.println("Last cell; " + cellValue); 
					}
					//En este punto ya tenemos la hoja a la que hay que dirigirse para rescatar los datos de la tabla
					//System.out.println(cellValue);
					XSSFCell cell2 = row.getCell(lastRowFirstCell+1);
					FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
					String cellValueSchema=getCellValueAsStr(cell2, evaluator);
					DbSchema schema=null;
					//System.out.println(bd.getName());
					cellValueSchema= cellValueSchema.substring(0, cellValueSchema.indexOf("."));
					if(firstIteration) {
						schema = new DbSchema(cellValueSchema);
						firstIteration=false;
					}else {
						if(!firstIteration && !checkExistingSchema(bd, cellValueSchema)) {
							schema = new DbSchema(cellValueSchema);
						}else {
							schema=(DbSchema) bd.getCollectedDbObj().get(cellValueSchema);
						}
					}
					bd.addCollectedObj(schema);
					//System.out.println(schema.getName());
					//Antes de analizar la estructura de la tabla indicamos que el índice sea correcto
					//Revisar que es un número
					try {
						Integer j = new Integer(cellValue);
						//Confirmar que la hoja existe
						try {
							//Intentamos buscar la hoja que contiene la tabla
							XSSFSheet sheetFound = workbook.getSheetAt(j);
							//En este punto la hoja ya se ha encontrado en una de las pestañas del excel y se procede a analizarla
							//System.out.println(sheetFound.getSheetName());
							analyzeTableStructure(cellValue, sheetFound, evaluator, schema);
						} catch(IllegalArgumentException error) {
							System.err.println("No se ha encontrado la hoja "+j+". Fuera de rango. ");
							continue;
						}
					} catch (NumberFormatException e) {
						System.err.println("En la fila número: " + (i + 1) + " y columna: " + (lastRow + 1) + " no encontramos un valor de índice válido. El valor es: " + cellValue);
						continue;
					}
				}
				workbook.close();
			} catch (IOException e) {
				System.err.println("IOException");
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			System.err.println("FileNotFoundException");
			e.printStackTrace();
		}
	}

	/**
	 * ¿Qué hace?
	 * -> Comprueba si existe el objeto schema en la colección de un objeto db
	 * @param bd
	 * @param cellValueSchema 
	 */
	private boolean checkExistingSchema(DbDb bd, String cellValueSchema) {
		if(bd.getCollectedDbObj().containsKey(cellValueSchema)) {
			return true;
		}			
		return false;
	}

	/**
	 * ¿Qué hace?
	 * -> Inicia el análisis de una pestaña
	 * @param cellValueAux
	 * @param sheetFound
	 * @param evaluator
	 * @param schema 
	 */
	private  void analyzeTableStructure(String cellValueAux, XSSFSheet sheetFound, FormulaEvaluator evaluator, DbSchema schema) {
		System.out.println("Análisis de la tabla " + sheetFound.getSheetName() + " asociada al índice número " + cellValueAux);
		int firstRow = sheetFound.getFirstRowNum();
		int firstRowCell = sheetFound.getRow(firstRow).getFirstCellNum();
		//System.out.println(firstRowCell);
		analyzeMetadata(firstRowCell, sheetFound, evaluator, schema);
	}

	/**
	 * ¿Qué hace?
	 * -> Recorro la tabla para obtener sus metadatos
	 * -> Recorro de abajo hacia arriba todas las propiedades de la hoja
	 * -> Recorro la tabla principal de la hoja  para saber el número de filas
	 * -> Por cada fila obtengo todas las celdas asociadas a dicha fila
	 * @param cellValueAux
	 * @param sheetFound
	 * @param evaluator
	 * @param schema 
	 */
	private void analyzeMetadata(int firstRowCell, XSSFSheet sheetFound, FormulaEvaluator evaluator, DbSchema schema) {
		DbTable table=null;
		boolean FoundBlanck=false;
		int i=firstRowCell;
		do {
			XSSFRow row = sheetFound.getRow(i);
			if(row==null) {
				FoundBlanck=true;
			}
			XSSFCell cell = row.getCell(firstRowCell);
			String cellData = normalizeData(getCellValueAsStr(cell, evaluator));//save
			
			int cellRightIndex = cell.getColumnIndex()+1;
			XSSFCell cellRight = row.getCell(cellRightIndex);
			String cellRightData = getCellValueAsStr(cellRight, evaluator);//save
			if(tabla.equals(cellData)) {
				table=new DbTable(cellRightData.split("\\.")[1]);
				schema.addCollectedObj(table);
				//System.out.println(table.getName());
			}
			if (cellData == null) {
				FoundBlanck=true;
			}
			GenericTableMetadata normName = ExcelNormalizedTableMetadata.getNormGenericName(cellData);
			if(normName!=null) {
				if (ddl.equals(normName.getNormName())) {
					FoundBlanck=true;				
				}else {
					//metadata.put(normalizeGenericName, cellRightData);
					table.addMetadataProperty(normName.getNormName(), cellRightData);
					i++;
				}
			}
			else {
				System.out.println("Se ha localizado un valor no normalizado para el Excel en la hoja: "+table.getName()+", en la celda: "+cell.getRowIndex()+","+cell.getColumnIndex());
			}
		} while(!FoundBlanck);
		analyzeTable(evaluator, sheetFound, firstRowCell, i, table);

	}

	/**
	 * ¿Qué hace?
	 * -> Recorro la tabla para obtener sus filas
	 * -> Por cada fila obtengo todas las celdas asociadas a dicha fila
	 * -> De cada fila, va guardando las celdas con todos los datos del Excel
	 * @param evaluator
	 * @param sheetFound
	 * @param firstRowCell
	 * @param table
	 */
	private void analyzeTable(FormulaEvaluator evaluator, XSSFSheet sheetFound, int firstRowCell, int i, DbTable table) {
		boolean FoundBlanck=true;
		while(FoundBlanck) {
			XSSFRow row = sheetFound.getRow(i);
			if(row==null) {
				FoundBlanck=false;
			}else {
				int y=0;
				XSSFCell cell = row.getCell(firstRowCell);
				String cellData = normalizeData(getCellValueAsStr(cell, evaluator));
				boolean finishedRow=false;
				if (cellData.equals(ddl)) {
					ArrayList<String> titleMapTable= new ArrayList<>();
					while(!finishedRow) {
						y++;
						int cellRightIndex = cell.getColumnIndex()+y;
						XSSFCell cellRight = row.getCell(cellRightIndex);
						if(cellRight==null) {
							finishedRow=true;
						}else {
							String cellRightData = normalizeData(getCellValueAsStr(cellRight, evaluator));
							//System.out.println(cellRightData);
							GenericColumnMetadata normName = ExcelNormalizedColumnMetadata.getNormGenericName(cellRightData);
							if(normName!=null) {
								titleMapTable.add(normName.getNormName());
							}
							//guardar aqui la cellRightData
						}
					}
					boolean finished=false;
					DbColumn iColumn = null;
					while(!finished) {
						i++;
						XSSFRow rowTable = sheetFound.getRow(i);
						if(rowTable==null) {
							//writeCsv(csvobj.getGeneral(), table.getName());
							finished=true;
						}else {

							for (int j = 1; j <= titleMapTable.size(); j++) {
								XSSFCell cellTable = rowTable.getCell(j);
								String cellTableData = null;
								if(cellTable!=null) {
									cellTableData = getCellValueAsStr(cellTable, evaluator);//save
								}
								if (nombre_campo.equals(titleMapTable.get(j-1))){
									iColumn = new DbColumn(cellTableData);
									table.addCollectedObj(iColumn);
								}
								iColumn.addMetadataProperty(titleMapTable.get(j-1), cellTableData);
								//System.out.println(cellTableData);
								//save the cellTableData
							}
						}
					}
				}
			}
		}
	}

	/**
	 * ¿Qué hace?
	 * -> Normaliza el contenido de una celda quitando espacios y poniendo todos los caracteres en mayúsculas
	 * @param cellData
	 */
	private String normalizeData(String cellData) {
		if(cellData.isEmpty()) {
			return null;
		}
		cellData=cellData.toUpperCase().trim();
		return cellData;
	}

	/**
	 * ¿Qué hace?
	 * -> Obtiene el valor de una celda dada en función del tipo de dato de dicha celda
	 * @param cell     
	 * @param evaluator
	 */
	private String getCellValueAsStr(XSSFCell cell, FormulaEvaluator evaluator) {
		//System.out.println(cell.getCellType()+ " Fila: "+cell.getRowIndex()+ " Columna: "+cell.getColumnIndex());
		CellValue cellValue = evaluator.evaluate(cell);
		if (cellValue!=null) {
			CellType cellType = cellValue.getCellType();
			switch (cellType) {
			case NUMERIC:
				int i=  (int) cellValue.getNumberValue();
				return String.valueOf(i);
			case STRING:
				return cellValue.getStringValue().trim();
			case _NONE:
				return "";
			case FORMULA:
				return "";
			default:
				System.err.println("Caso no contemplato, revise el tipo de dato de la celda");
				System.err.println(cell.getCellType()+ " Fila: "+cell.getRowIndex()+ " Columna: "+cell.getColumnIndex());
				return null;
			}
		}
		return null;
	}

}
