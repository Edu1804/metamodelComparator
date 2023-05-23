package extractorLoader.excel.dbMetadata;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import dbObjects.dbMetadata.GenericTableMetadata;

public enum ExcelNormalizedTableMetadata {
	tablaExc ("TABLA", GenericTableMetadata.TABLA),
	pathProExc ("PATH_PRO", GenericTableMetadata.PATH_PRO),
	pathPreExc ("PATH_PRE", GenericTableMetadata.PATH_PRE),
	pathFormatExc ("FORMATO", GenericTableMetadata.FORMATO),
	pathFrequencyExc ("FRECUENCIA", GenericTableMetadata.FRECUENCIA),
	pathNumPartitionsExc ("NUM_PARTICIONES", GenericTableMetadata.TAMANO_TOTAL),
	pathPartitionsSizeExc ("TAMAÑO PARTITION", GenericTableMetadata.TAMANO_PARTICION),
	pathTotalSizeExc ("TMAÑO TOTAL", GenericTableMetadata.NUM_PARTICIONES),
	pathCamposPartition ("CAMPOS PARTICION", GenericTableMetadata.CAMPOS_PARTICION),
	pathDdl ("DDL", GenericTableMetadata.DDL),
	;

	String normalizedExcelName = null;
	GenericTableMetadata normalizedGenericName = null;
	private static Map<String, ExcelNormalizedTableMetadata> indexedEnum = new HashMap<>();

	ExcelNormalizedTableMetadata(String normalizedExcelName, GenericTableMetadata normalizedGenericName) {
		this.normalizedExcelName = normalizedExcelName;
		this.normalizedGenericName = normalizedGenericName;
		return;
	}

	static {
		Arrays.stream(values())
		.forEach(e -> 
		indexedEnum.put(e.normalizedExcelName, e)
				);
	}

	public static GenericTableMetadata getNormGenericName (String excelName) {
		ExcelNormalizedTableMetadata normalizedField = indexedEnum.get(excelName);
		if (normalizedField == null) {
			return null;
		}
		return normalizedField.normalizedGenericName;
	}
}
