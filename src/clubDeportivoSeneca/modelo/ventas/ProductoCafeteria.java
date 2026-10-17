package clubDeportivoSeneca.modelo.ventas;
import java.util.Objects;

public abstract class ProductoCafeteria {
	private final String nombre;
	private int precio;
	
	protected ProductoCafeteria(String nombre, int precio) {
		if (nombre == null || nombre.isBlank()) {
			throw new IllegalArgumentException("El nombre del producto no puede estar vacío.");
        }
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que 0.");
        }
        this.nombre = nombre;
        this.precio = precio;
    }
	
	public String getNombre() {
        return nombre;
    }
 
    public int getPrecio() {
        return precio;
    }
 
    public void setPrecio(int precio) {
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que 0.");
        }
        this.precio = precio;
    }
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductoCafeteria)) return false;
        return nombre.equals(((ProductoCafeteria) o).nombre);
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }
 
    @Override
    public String toString() {
        return nombre + " ($" + precio + ")";
    }

}
