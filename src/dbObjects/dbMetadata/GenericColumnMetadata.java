package dbObjects.dbMetadata;

public enum GenericColumnMetadata {
	NOMBRE_CAMPO,
	TIPO,
	DESCRIPCION,
	FLAG_DISOCIADOR,
	ALGORITMO,
	MIN_LENGTH_DISOCIADOR,
	MAX_LENGTH_DISOCIADOR,
	MIN_VALUE_DISOCIADOR,
	MAX_VALUE_DISOCIADOR,
	TIPO_DISOCIADOR
	;
	
	public String getNormName() {
		return this.toString();
	}
}
