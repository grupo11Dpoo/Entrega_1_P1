package clubDeportivoSeneca.modelo.inventario;

import java.util.Objects;

public class Articulo implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	private String nombre;
	private String categoria;
	private int precio;
	
	public Articulo(String nombre, String categoria, int precio) {
		if (nombre == null || nombre.isBlank()) {
			throw new IllegalArgumentException("El nombre del artículo no puede estar vacío."); 
		}
		if (categoria == null || categoria.isBlank()) {
			throw new IllegalArgumentException("El categoría del artículo no puede estar vacía.");
		}
		if (precio <= 0) {
			throw new IllegalArgumentException("El precio debe ser mayor a 0");
		}
		this.nombre = nombre;
		this.categoria = categoria;
		this.precio = precio;
	}
	public String getNombre() {
		return nombre;
	}
	public String getCategoria() {
		return categoria;
	}
	public int getPrecio () {
		return precio;
	}
	
	public void setCategoria(String categoria) {
		if (categoria == null || categoria.isBlank()) {
			throw new IllegalArgumentException ("La categoria del articulo no puede estar vacia");
		}
		this.categoria = categoria;
	}
	
	public void setPrecio(int precio) {
		if (precio <=0) {
			throw new IllegalArgumentException ("El precio debe ser mayor a 0");
		}
		this.precio = precio;
	}
	
	@Override
	public boolean equals (Object o) {
		if (this== o) return true;
		if (!(o instanceof Articulo)) return false;
		Articulo otro = (Articulo) o ;
		return nombre.equals(otro.nombre);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(nombre);
	}

	@Override
	public String toString() {
		return nombre + "(" + categoria + ", $" + precio + ")";
	}

}
