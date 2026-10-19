package clubDeportivoSeneca.modelo.ventas;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
 
import clubDeportivoSeneca.modelo.inventario.Articulo;
import clubDeportivoSeneca.modelo.inventario.InventarioArticulos;
import clubDeportivoSeneca.modelo.usuario.Comprador;

public class VentaArticulosDeportivos extends VentaServiciosAdicionales implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	
	public static final int IVA = 19;
	
	private final Map<Articulo, Integer> lineas;
	private DescuentoEmpleado descuento;
	
	public VentaArticulosDeportivos(LocalDate fecha, Comprador comprador) {
        super(fecha, comprador, Rubro.TIENDA);
        this.lineas = new LinkedHashMap<>();
        this.descuento = null;
    }
 
    public Map<Articulo, Integer> getLineas() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(lineas));
    }
 
    public DescuentoEmpleado getDescuento() {
        return descuento;
    }
    
    public void setDescuento(DescuentoEmpleado descuento) {
        validarNoFinalizada();
        if (descuento != null && getPuntosRedimidos() > 0) {
            throw new IllegalStateException("El descuento de empleado no se combina con la redención de puntos.");
        }
        this.descuento = descuento;
    }
    
    public void agregarArticulo(Articulo articulo, int cantidad, InventarioArticulos inventario) {
        validarNoFinalizada();
        if (articulo == null) {
            throw new IllegalArgumentException("El artículo no puede ser nulo.");
        }
        if (inventario == null) {
            throw new IllegalArgumentException("El inventario no puede ser nulo.");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que 0.");
        }
        int yaEnVenta = lineas.getOrDefault(articulo, 0);
        int disponible = inventario.cantidadDisponible(articulo, InventarioArticulos.UBICACION_TIENDA);
        if (yaEnVenta + cantidad > disponible) {
            throw new IllegalStateException("Stock insuficiente de " + articulo.getNombre()
                    + ": hay " + disponible + " y se piden " + (yaEnVenta + cantidad) + ".");
        }
        lineas.put(articulo, yaEnVenta + cantidad);
    }
    
    public void quitarArticulo(Articulo articulo) {
        validarNoFinalizada();
        if (articulo == null || !lineas.containsKey(articulo)) {
            throw new IllegalArgumentException("El artículo no está en la venta.");
        }
        lineas.remove(articulo);
    }
 
    @Override
    public int calcularSubtotal() {
        int subtotal = 0;
        for (Map.Entry<Articulo, Integer> linea : lineas.entrySet()) {
            subtotal += linea.getKey().getPrecio() * linea.getValue();
        }
        return subtotal;
    }
    
    public int calcularDescuento() {
        if (descuento == null) {
            return 0;
        }
        return descuento.calcularDescuento(calcularSubtotal(), getComprador());
    }
    
    public int calcularIva() {
        return (calcularSubtotal() - calcularDescuento()) * IVA / 100;
    }
    
    @Override
    protected int calcularTotalSinPuntos() {
        return calcularSubtotal() - calcularDescuento() + calcularIva();
    }
 
    @Override
    public int calcularTotal() {
        return calcularTotalSinPuntos() - getPuntosRedimidos();
    }
    
    @Override
    public void aplicarPuntos(int puntos) {
        validarNoFinalizada();
        if (descuento != null && puntos > 0) {
            throw new IllegalStateException("No se pueden redimir puntos en una venta con descuento de empleado.");
        }
        super.aplicarPuntos(puntos);
    }
    @Override
    public void finalizar() {
        throw new UnsupportedOperationException("Use finalizar(InventarioArticulos).");
    }
    
    public void finalizar(InventarioArticulos inventario) {
        if (inventario == null) {
            throw new IllegalArgumentException("El inventario no puede ser nulo.");
        }
        validarCierre();
        for (Map.Entry<Articulo, Integer> linea : lineas.entrySet()) {
            int disponible = inventario.cantidadDisponible(linea.getKey(), InventarioArticulos.UBICACION_TIENDA);
            if (disponible < linea.getValue()) {
                throw new IllegalStateException("Stock insuficiente de " + linea.getKey().getNombre()
                        + ": hay " + disponible + " y se piden " + linea.getValue() + ".");
            }
        }
        for (Map.Entry<Articulo, Integer> linea : lineas.entrySet()) {
            inventario.descontar(linea.getKey(), linea.getValue());
        }
        ejecutarCierre();
    }
}
