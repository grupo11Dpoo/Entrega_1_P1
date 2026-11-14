package clubDeportivoSeneca.modelo.ventas;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import clubDeportivoSeneca.modelo.inventario.Articulo;
import clubDeportivoSeneca.modelo.inventario.InventarioArticulos;
import clubDeportivoSeneca.modelo.usuario.Entrenador;
import clubDeportivoSeneca.modelo.usuario.Jugador;

public class VentaTiendaTest {

    private InventarioArticulos inventario;
    private Articulo balon;
    private Jugador ana;
    private Entrenador carlos;

    @BeforeEach
    public void setUp() {
        inventario = new InventarioArticulos();
        balon = new Articulo("Balon", "Futbol", 50000);
        inventario.registrarArticulo(balon, 10);

        ana = new Jugador("ana", "1234", "Ana", LocalDate.of(2000, 1, 1));
        carlos = new Entrenador("carlos", "1234", "Carlos", "CARLOS15");
    }

    @Test
    @DisplayName("Calcula subtotal, IVA del 19 % y total")
    public void calculaIvaYTotal() {
        VentaArticulosDeportivos venta = new VentaArticulosDeportivos(LocalDate.now(), ana);
        venta.agregarArticulo(balon, 2, inventario);

        assertEquals(100000, venta.calcularSubtotal());
        assertEquals(19000, venta.calcularIva());
        assertEquals(119000, venta.calcularTotal());
    }

    @Test
    @DisplayName("Finalizar la compra descuenta el inventario y guarda el total")
    public void finalizarDescuentaInventario() {
        VentaArticulosDeportivos venta = new VentaArticulosDeportivos(LocalDate.now(), ana);
        venta.agregarArticulo(balon, 2, inventario);

        venta.finalizar(inventario);

        assertTrue(venta.isFinalizada());
        assertEquals(119000, venta.getPrecioTotal());
        assertEquals(8, inventario.cantidadDisponible(balon, InventarioArticulos.UBICACION_TIENDA));
    }

    @Test
    @DisplayName("Rechaza pedir mas unidades de las que hay en la tienda")
    public void rechazaStockInsuficiente() {
        VentaArticulosDeportivos venta = new VentaArticulosDeportivos(LocalDate.now(), ana);

        assertThrows(IllegalStateException.class, () -> venta.agregarArticulo(balon, 11, inventario));
        assertEquals(0, venta.calcularSubtotal(), "Un rechazo no debe agregar nada a la venta");
        assertEquals(10, inventario.cantidadDisponible(balon, InventarioArticulos.UBICACION_TIENDA));
    }

    @Test
    @DisplayName("Rechaza una cantidad menor o igual a cero")
    public void rechazaCantidadNoPositiva() {
        VentaArticulosDeportivos venta = new VentaArticulosDeportivos(LocalDate.now(), ana);

        assertThrows(IllegalArgumentException.class, () -> venta.agregarArticulo(balon, 0, inventario));
    }

    @Test
    @DisplayName("Rechaza finalizar una venta sin articulos")
    public void rechazaVentaVacia() {
        VentaArticulosDeportivos venta = new VentaArticulosDeportivos(LocalDate.now(), ana);

        assertThrows(IllegalStateException.class, () -> venta.finalizar(inventario));
    }

    @Test
    @DisplayName("El empleado con su propio codigo recibe 15 % de descuento antes del IVA")
    public void descuentoEmpleadoPropio() {
        VentaArticulosDeportivos venta = new VentaArticulosDeportivos(LocalDate.now(), carlos);
        venta.agregarArticulo(balon, 2, inventario);

        venta.setDescuento(new DescuentoEmpleado(carlos));

        assertEquals(15000, venta.calcularDescuento());
        assertEquals(16150, venta.calcularIva(), "El IVA se calcula sobre 85.000");
        assertEquals(101150, venta.calcularTotal());
    }

    @Test
    @DisplayName("Un socio con el codigo de un empleado recibe 8 % de descuento")
    public void descuentoCodigoCompartido() {
        VentaArticulosDeportivos venta = new VentaArticulosDeportivos(LocalDate.now(), ana);
        venta.agregarArticulo(balon, 2, inventario);

        venta.setDescuento(new DescuentoEmpleado(carlos));

        assertEquals(8000, venta.calcularDescuento());
    }

    @Test
    @DisplayName("Rechaza redimir mas puntos de los que tiene el socio")
    public void rechazaPuntosInsuficientes() {
        VentaArticulosDeportivos venta = new VentaArticulosDeportivos(LocalDate.now(), ana);
        venta.agregarArticulo(balon, 1, inventario);

        assertThrows(IllegalStateException.class, () -> venta.aplicarPuntos(100));
        assertEquals(0, venta.getPuntosRedimidos());
    }

    @Test
    @DisplayName("El descuento de empleado no se acumula con la redencion de puntos")
    public void descuentoNoSeAcumulaConPuntos() {
        VentaArticulosDeportivos venta = new VentaArticulosDeportivos(LocalDate.now(), ana);
        venta.agregarArticulo(balon, 1, inventario);
        venta.setDescuento(new DescuentoEmpleado(carlos));

        assertThrows(IllegalStateException.class, () -> venta.aplicarPuntos(1));
    }

    @Test
    @DisplayName("Una compra suma al socio el 2 % del total pagado (HU-S15)")
    public void acumulaDosPorCientoDelTotal() {
        VentaArticulosDeportivos venta = new VentaArticulosDeportivos(LocalDate.now(), ana);
        venta.agregarArticulo(balon, 2, inventario);

        venta.finalizar(inventario);
        assertEquals(2380, ana.getPuntosFidelidad());
    }
}