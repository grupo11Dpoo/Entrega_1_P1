package clubDeportivoSeneca.modelo.ventas;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

import clubDeportivoSeneca.modelo.inventario.Articulo;
import clubDeportivoSeneca.modelo.inventario.InventarioArticulos;
import clubDeportivoSeneca.modelo.torneos.BonoDescuento;
import clubDeportivoSeneca.modelo.usuario.Entrenador;
import clubDeportivoSeneca.modelo.usuario.Jugador;

public class BonoVentaTest {

    private Jugador socio;
    private BonoDescuento bono;

    @Before
    public void preparar() throws Exception {
        socio = new Jugador("ana", "x", "Ana", LocalDate.of(2000, 1, 1));
        bono = new BonoDescuento("Copa", 0.2, LocalDate.now().plusDays(10));
        socio.agregarBono(bono);
    }

    private VentaCafeteria ventaCafeteria(Jugador comprador) {
        VentaCafeteria venta = new VentaCafeteria(LocalDate.now(), comprador);
        venta.agregarBebida(new Bebida("Jugo", 10000, Temperatura.FRIA), 1);
        return venta;
    }

    private VentaArticulosDeportivos ventaTienda(Jugador comprador, InventarioArticulos inventario) {
        Articulo raqueta = new Articulo("Raqueta", "Tenis", 100000);
        inventario.registrarArticulo(raqueta, 5);
        VentaArticulosDeportivos venta = new VentaArticulosDeportivos(LocalDate.now(), comprador);
        venta.agregarArticulo(raqueta, 1, inventario);
        return venta;
    }

    @Test
    public void aplicarBono() throws Exception {
        VentaCafeteria venta = ventaCafeteria(socio);
        assertEquals(11800, venta.calcularTotal());

        venta.aplicarBono(bono);

        assertSame(bono, venta.getBono());
        assertEquals(2000, venta.calcularDescuentoBono());
        assertEquals(640, venta.calcularImpuesto()); 
        assertEquals(1000, venta.calcularPropina()); 
        assertEquals(9640, venta.calcularTotal());

        Jugador otro = new Jugador("luis", "x", "Luis", LocalDate.of(2000, 1, 1));
        BonoDescuento vencido = new BonoDescuento("Viejo", 0.2, LocalDate.now().minusDays(1));
        socio.agregarBono(vencido);
        BonoDescuento usado = new BonoDescuento("Usado", 0.2, LocalDate.now().plusDays(10));
        socio.agregarBono(usado);
        usado.redimir();
        assertThrows(IllegalStateException.class, () -> ventaCafeteria(otro).aplicarBono(bono));
        assertThrows(IllegalStateException.class, () -> ventaCafeteria(socio).aplicarBono(vencido));
        assertThrows(IllegalStateException.class, () -> ventaCafeteria(socio).aplicarBono(usado));
        assertThrows(IllegalArgumentException.class, () -> ventaCafeteria(socio).aplicarBono(null));
    }

    @Test
    public void quitarBono() throws Exception {
        VentaCafeteria venta = ventaCafeteria(socio);
        venta.aplicarBono(bono);

        venta.quitarBono();

        assertEquals(0, venta.calcularDescuentoBono());
        assertEquals(11800, venta.calcularTotal());
    }

    @Test
    public void calcularDescuentoBonoEnLaTienda() throws Exception {
        VentaArticulosDeportivos venta = ventaTienda(socio, new InventarioArticulos());
        venta.aplicarBono(bono);

        assertEquals(20000, venta.calcularDescuentoBono());
        assertEquals(15200, venta.calcularIva());
        assertEquals(95200, venta.calcularTotal());
    }

    @Test
    public void finalizarRedimeElBono() throws Exception {
        VentaCafeteria cafeteria = ventaCafeteria(socio);
        cafeteria.aplicarBono(bono);
        cafeteria.finalizar();
        assertTrue(bono.isRedimido());
        assertThrows(IllegalStateException.class, () -> ventaCafeteria(socio).aplicarBono(bono));

        BonoDescuento otroBono = new BonoDescuento("Otro", 0.1, LocalDate.now().plusDays(10));
        socio.agregarBono(otroBono);
        InventarioArticulos inventario = new InventarioArticulos();
        VentaArticulosDeportivos tienda = ventaTienda(socio, inventario);
        tienda.aplicarBono(otroBono);
        tienda.finalizar(inventario);
        assertTrue(otroBono.isRedimido());
    }

    @Test
    public void elBonoNoSeCombinaConPuntosDeFidelidad() {
        socio.acumularPuntos(5000);

        VentaCafeteria conPuntos = ventaCafeteria(socio);
        conPuntos.aplicarPuntos(50);
        assertThrows(IllegalStateException.class, () -> conPuntos.aplicarBono(bono));

        VentaCafeteria conBono = ventaCafeteria(socio);
        conBono.aplicarBono(bono);
        assertThrows(IllegalStateException.class, () -> conBono.aplicarPuntos(50));
    }

    @Test
    public void elBonoNoSeCombinaConElDescuentoDeEmpleado() {
        Entrenador empleado = new Entrenador("pepe", "x", "Pepe", "COD");

        VentaArticulosDeportivos conDescuento = ventaTienda(socio, new InventarioArticulos());
        conDescuento.setDescuento(new DescuentoEmpleado(empleado));
        assertThrows(IllegalStateException.class, () -> conDescuento.aplicarBono(bono));

        VentaArticulosDeportivos conBono = ventaTienda(socio, new InventarioArticulos());
        conBono.aplicarBono(bono);
        assertThrows(IllegalStateException.class, () -> conBono.setDescuento(new DescuentoEmpleado(empleado)));
    }
}
