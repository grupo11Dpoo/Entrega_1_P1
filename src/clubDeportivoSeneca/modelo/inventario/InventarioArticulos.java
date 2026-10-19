package clubDeportivoSeneca.modelo.inventario;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class InventarioArticulos implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	
	public static final String UBICACION_TIENDA = "TIENDA";
	
	private final Map<String, Map<Articulo, Integer>> stock;
	
	public InventarioArticulos() {
		this.stock = new LinkedHashMap<>();
		this.stock.put(UBICACION_TIENDA, new LinkedHashMap<>());
	}
	
	public Map<String, Map<Articulo, Integer>> getStock() {
        Map<String, Map<Articulo, Integer>> copia = new LinkedHashMap<>();
        for (Map.Entry<String, Map<Articulo, Integer>> e : stock.entrySet()) {
            copia.put(e.getKey(), Collections.unmodifiableMap(new LinkedHashMap<>(e.getValue())));
        }
        return Collections.unmodifiableMap(copia);
    }
	
	public int cantidadDisponible (Articulo articulo, String ubicacion) {
		return obtenerUbicacion(ubicacion).getOrDefault(articulo, 0);
	}
	
	public int cantidadTotal(Articulo articulo) {
		int total = 0;
		for (Map<Articulo, Integer> enUbicacion : stock.values()) {
			total += enUbicacion.getOrDefault (articulo, 0);
		}
		return total;
	}
	
	public void agregarUbicacion(String ubicacion) {
        if (ubicacion == null || ubicacion.isBlank()) {
            throw new IllegalArgumentException("El nombre de la ubicación no puede estar vacío.");
        }
        if (stock.containsKey(ubicacion)) {
            throw new IllegalArgumentException("La ubicación ya existe: " + ubicacion);
        }
        stock.put(ubicacion, new LinkedHashMap<>());
    }
	
	public void registrarArticulo(Articulo articulo, int cantidadInicial) {
        validarArticulo(articulo);
        if (cantidadInicial < 0) {
            throw new IllegalArgumentException("La cantidad inicial no puede ser negativa.");
        }
        if (estaRegistrado(articulo)) {
            throw new IllegalArgumentException("El artículo ya está registrado: " + articulo.getNombre());
        }
        stock.get(UBICACION_TIENDA).put(articulo, cantidadInicial);
    }
	
	public void mover(Articulo articulo, String origen, String destino, int cantidad) {
        validarArticulo(articulo);
        validarCantidadPositiva(cantidad);
        Map<Articulo, Integer> enOrigen = obtenerUbicacion(origen);
        Map<Articulo, Integer> enDestino = obtenerUbicacion(destino);
        if (origen.equals(destino)) {
            throw new IllegalArgumentException("El origen y el destino deben ser distintos.");
        }
        validarRegistrado(articulo);
        int disponible = enOrigen.getOrDefault(articulo, 0);
        if (disponible < cantidad) {
            throw new IllegalStateException("Stock insuficiente en " + origen + ": hay "
                    + disponible + " y se piden " + cantidad + ".");
        }
        enOrigen.put(articulo, disponible - cantidad);
        enDestino.merge(articulo, cantidad, Integer::sum);
	}
	
	public void reabastecer(Articulo articulo, int cantidad, String ubicacion) {
        validarArticulo(articulo);
        validarCantidadPositiva(cantidad);
        Map<Articulo, Integer> enUbicacion = obtenerUbicacion(ubicacion);
        validarRegistrado(articulo);
        enUbicacion.merge(articulo, cantidad, Integer::sum);
    }
	
	public void descontar(Articulo articulo, int cantidad) {
        validarArticulo(articulo);
        validarCantidadPositiva(cantidad);
        validarRegistrado(articulo);
        Map<Articulo, Integer> tienda = stock.get(UBICACION_TIENDA);
        int disponible = tienda.getOrDefault(articulo, 0);
        if (disponible < cantidad) {
            throw new IllegalStateException("Stock insuficiente de " + articulo.getNombre()
                    + " en la tienda: hay " + disponible + " y se piden " + cantidad + ".");
        }
        tienda.put(articulo, disponible - cantidad);
    }
	
	private Map<Articulo, Integer> obtenerUbicacion(String ubicacion) {
        Map<Articulo, Integer> enUbicacion = (ubicacion == null) ? null : stock.get(ubicacion);
        if (enUbicacion == null) {
            throw new IllegalArgumentException("La ubicación no existe: " + ubicacion);
        }
        return enUbicacion;
    }

    private boolean estaRegistrado(Articulo articulo) {
        for (Map<Articulo, Integer> enUbicacion : stock.values()) {
            if (enUbicacion.containsKey(articulo)) {
                return true;
            }
        }
        return false;
    }

    private void validarRegistrado(Articulo articulo) {
        if (!estaRegistrado(articulo)) {
            throw new IllegalArgumentException("El artículo no está registrado: " + articulo.getNombre());
        }
    }

    private void validarArticulo(Articulo articulo) {
        if (articulo == null) {
            throw new IllegalArgumentException("El artículo no puede ser nulo.");
        }
    }

    private void validarCantidadPositiva(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que 0.");
        }
    }

}
