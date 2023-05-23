package dbObjects.dbMetadata;

public enum GenericTableMetadata {
	TABLA,
	PATH_PRO,
	PATH_PRE,
	FORMATO,
	FRECUENCIA,
	TAMANO_PARTICION,
	NUM_PARTICIONES,
	TAMANO_TOTAL, 
	DDL, 
	CAMPOS_PARTICION,
	;
	
	public String getNormName() {
		return this.toString();
	}
}






