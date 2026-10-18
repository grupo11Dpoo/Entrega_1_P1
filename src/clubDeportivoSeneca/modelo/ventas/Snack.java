package clubDeportivoSeneca.modelo.ventas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Snack extends ProductoCafeteria implements java.io.Serializable {
	
	private final List<Alergeno>alergenos;
	
	public Snack(String nombre, int precio) {
		super (nombre, precio);
		this.alergenos = new ArrayList<>();
	}
	
	public List<Alergeno> getAlergenos() {
        return Collections.unmodifiableList(new ArrayList<>(alergenos));
    }
	
	public void agregarAlergeno(Alergeno alergeno) {
        if (alergeno == null) {
            throw new IllegalArgumentException("El alérgeno no puede ser nulo.");
        }
        if (!alergenos.contains(alergeno)) {
            alergenos.add(alergeno);
        }
    }
 
    public boolean tieneAlergenos() {
        return !alergenos.isEmpty();
    }

}
