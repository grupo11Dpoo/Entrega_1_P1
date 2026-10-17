package clubDeportivoSeneca.modelo.deportes;

public class CategoriaEdad {
	
	private String nombreCategoria;
	private int edadMin;
	private int edadMax;
	
	public CategoriaEdad(String nombreCategoria, int edadMin, int edadMax) throws Exception {

		if(nombreCategoria == null) {
			throw new Exception ("El nombre de la categoría no puede estar vacío.");
		}
		
		if(edadMin > edadMax) {
			throw new Exception("Rango de edas inválido.");
		}
		
		this.nombreCategoria = nombreCategoria;
		this.edadMin = edadMin;
		this.edadMax = edadMax;
	}
	public String getNombreCategoria() {
		return nombreCategoria;
	}
	public int getEdadMin() {
		return edadMin;
	}
	public int getEdadMax() {
		return edadMax;
	}
	
	public boolean aceptaEdad(int edad) {
		
		if(edadMin <= edad && edad <= edadMax) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
    public boolean equals(Object o) {
        return o instanceof CategoriaEdad c && nombreCategoria.equalsIgnoreCase(c.nombreCategoria);
    }

    @Override
    public String toString() {
        return nombreCategoria;
    }
}
