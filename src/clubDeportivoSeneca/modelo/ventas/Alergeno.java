package clubDeportivoSeneca.modelo.ventas;

import java.util.Locale;
import java.util.Objects;

public class Alergeno implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	
	private final String nombre;
	
	public Alergeno(String nombre) {
		if (nombre == null || nombre.isBlank()) {
			throw new IllegalArgumentException("El nombre del alérgeno no puede estar vacío.");
        }
        this.nombre = nombre.trim();
    }
 
    public String getNombre() {
        return nombre;
    }
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Alergeno)) return false;
        return nombre.equalsIgnoreCase(((Alergeno) o).nombre);
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(nombre.toLowerCase(Locale.ROOT));
	}
    
    @Override
    public String toString() {
        return nombre;
    }

}
