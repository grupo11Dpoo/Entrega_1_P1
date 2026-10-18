package clubDeportivoSeneca.modelo.ventas;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
 
import clubDeportivoSeneca.modelo.instalaciones.Instalacion;
import clubDeportivoSeneca.modelo.usuario.Comprador;
import clubDeportivoSeneca.modelo.usuario.Empleado;
import clubDeportivoSeneca.modelo.usuario.Jugador;

public class VentaCafeteria extends VentaServiciosAdicionales implements java.io.Serializable{
	public static final int IMPUESTO = 8;
    public static final int PROPINA_SUGERIDA = 10;
 
    private final Map<ProductoCafeteria, Integer> productos;
    private int porcentajePropina;
    private boolean alergenosInformados;
 
    public VentaCafeteria(LocalDate fecha, Comprador comprador) {
        super(fecha, comprador, Rubro.CAFETERIA);
        this.productos = new LinkedHashMap<>();
        this.porcentajePropina = PROPINA_SUGERIDA;
        this.alergenosInformados = false;
    }
    
    public Map<ProductoCafeteria, Integer> getProductos() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(productos));
    }
 
    public int getPorcentajePropina() {
        return porcentajePropina;
    }
 
    public boolean isAlergenosInformados() {
        return alergenosInformados;
    }
 
    public void setPorcentajePropina(int porcentajePropina) {
        validarNoFinalizada();
        if (porcentajePropina < 0) {
            throw new IllegalArgumentException("La propina no puede ser negativa.");
        }
        this.porcentajePropina = porcentajePropina;
    }
    
    public void agregarBebida(Bebida bebida, int cantidad) {
        validarNoFinalizada();
        if (bebida == null) {
            throw new IllegalArgumentException("La bebida no puede ser nula.");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que 0.");
        }
        if (bebida.esCaliente()) {
            Jugador jugador = obtenerJugador();
            if (jugador != null) {
                Instalacion actual = jugador.getInstalacionActual();
                if (actual != null && actual.restringeBebidaCaliente()) {
                    throw new IllegalStateException("No se puede despachar bebida caliente a un socio "
                            + "que está en una instalación que lo restringe.");
                }
            }
        }
        productos.merge(bebida, cantidad, Integer::sum);
    }
    
    public void agregarSnack(Snack snack, int cantidad) {
        validarNoFinalizada();
        if (snack == null) {
            throw new IllegalArgumentException("El snack no puede ser nulo.");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que 0.");
        }
        productos.merge(snack, cantidad, Integer::sum);
        alergenosInformados = false;
    }
    
    public List<Alergeno> getAlergenosAInformar() {
        Set<Alergeno> alergenos = new LinkedHashSet<>();
        for (ProductoCafeteria producto : productos.keySet()) {
            if (producto instanceof Snack) {
                alergenos.addAll(((Snack) producto).getAlergenos());
            }
        }
        return Collections.unmodifiableList(new ArrayList<>(alergenos));
    }
    
    public void confirmarAlergenos() {
        validarNoFinalizada();
        alergenosInformados = true;
    }
 
    @Override
    public int calcularSubtotal() {
        int subtotal = 0;
        for (Map.Entry<ProductoCafeteria, Integer> linea : productos.entrySet()) {
            subtotal += linea.getKey().getPrecio() * linea.getValue();
        }
        return subtotal;
    }
    
    public int calcularImpuesto() {
        return calcularSubtotal() * IMPUESTO / 100;
    }
    
    public int calcularPropina() {
        return calcularSubtotal() * porcentajePropina / 100;
    }
 
    @Override
    protected int calcularTotalSinPuntos() {
        return calcularSubtotal() + calcularImpuesto() + calcularPropina();
    }
 
    @Override
    public int calcularTotal() {
        return calcularTotalSinPuntos() - getPuntosRedimidos();
    }
 
    @Override
    protected void validarCierre() {
        super.validarCierre();
        if (tieneSnacks() && !alergenosInformados) {
            throw new IllegalStateException("Hay que informar los alérgenos al socio antes de finalizar la venta.");
        }
    }
    
    @Override
    public void finalizar() {
        boolean conBebidaCaliente = tieneBebidaCaliente();
        super.finalizar();
        if (conBebidaCaliente) {
            Jugador jugador = obtenerJugador();
            if (jugador != null) {
                jugador.setBebidaCaliente(true);
            }
        }
    }
 
    private boolean tieneSnacks() {
        for (ProductoCafeteria producto : productos.keySet()) {
            if (producto instanceof Snack) {
                return true;
            }
        }
        return false;
    }
 
    private boolean tieneBebidaCaliente() {
        for (ProductoCafeteria producto : productos.keySet()) {
            if (producto instanceof Bebida && ((Bebida) producto).esCaliente()) {
                return true;
            }
        }
        return false;
    }
    
    private Jugador obtenerJugador() {
        Comprador comprador = getComprador();
        if (comprador instanceof Jugador) {
            return (Jugador) comprador;
        }
        if (comprador instanceof Empleado) {
            return ((Empleado) comprador).getPerfilJugador();
        }
        return null;
    }

}
