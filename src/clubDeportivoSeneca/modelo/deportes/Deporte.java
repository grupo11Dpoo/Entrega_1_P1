package clubDeportivoSeneca.modelo.deportes;

public abstract class Deporte {
	protected String nombre;

	protected Deporte(String nombre) throws Exception {
		
		if(nombre == null) {
			throw new Exception("El nombre del deporte es obligatorio.");
		}
		
		this.nombre = nombre;
	}

	public String getNombre() {
		return nombre;
	}
	
	@Override
    public boolean equals(Object o) {
        return o instanceof Deporte d && nombre.equalsIgnoreCase(d.nombre);
    }
 
    @Override
    public String toString() {
        return nombre;
    }

}
