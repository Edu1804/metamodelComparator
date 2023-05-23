package extractorLoader.excel.dbMetadata;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import dbObjects.dbMetadata.GenericColumnMetadata;



public enum ExcelNormalizedColumnMetadata {
	pathColumnFieldName ("NOMBRE_CAMPO", GenericColumnMetadata.NOMBRE_CAMPO),
	pathColumnTipo ("TIPO", GenericColumnMetadata.TIPO),
	pathColumnDescription ("DESCRIPCIÓN", GenericColumnMetadata.DESCRIPCION),
	pathColumnFlag ("FLAG_DISOCIADOR", GenericColumnMetadata.FLAG_DISOCIADOR),
	pathColumnAlgorithm ("ALGORITMO", GenericColumnMetadata.ALGORITMO),
	pathColumnMinLength ("MIN_LENTGH_DISOCIADOR", GenericColumnMetadata.MIN_LENGTH_DISOCIADOR),
	pathColumnMaxLength ("MAX_LENTGH_DISOCIADOR", GenericColumnMetadata.MAX_LENGTH_DISOCIADOR),
	pathColumnMinValue ("MIN_VALUE_DISOCIADOR", GenericColumnMetadata.MIN_VALUE_DISOCIADOR),
	pathColumnMaxValue ("MAX_VALUE_DISOCIADOR", GenericColumnMetadata.MAX_VALUE_DISOCIADOR),
	pathColumnTipoDisociador ("TIPO_DISOCIADOR", GenericColumnMetadata.TIPO_DISOCIADOR),
	;

	String normalizedExcelName = null;
	GenericColumnMetadata normalizedGenericName = null;

	private static Map<String, ExcelNormalizedColumnMetadata> indexedEnum = new HashMap<>();

	ExcelNormalizedColumnMetadata(String normalizedExcelName, GenericColumnMetadata normalizedGenericName) {
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

	public static GenericColumnMetadata getNormGenericName (String excelName) {
		ExcelNormalizedColumnMetadata normalizedField = indexedEnum.get(excelName);
		if (normalizedField == null) {
			return null;
		}
		return normalizedField.normalizedGenericName;
	}
}
