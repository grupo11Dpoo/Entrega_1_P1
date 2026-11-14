package clubDeportivoSeneca.modelo.ventas;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import clubDeportivoSeneca.modelo.deportes.Padel;
import clubDeportivoSeneca.modelo.instalaciones.CanchaPadel;
import clubDeportivoSeneca.modelo.usuario.Jugador;

public class VentaCafeteriaTest {

    private Jugador ana;
    private Bebida jugo;
    private Bebida cafe;
    private Snack galletas;

    @BeforeEach
    public void setUp() {
        ana = new Jugador("ana", "1234", "Ana", LocalDate.of(2000, 1, 1));
        jugo = new Bebida("Jugo", 5000, Temperatura.FRIA);
        cafe = new Bebida("Cafe", 4000, Temperatura.CALIENTE);
        galletas = new Snack("Galletas", 3000);
        galletas.agregarAlergeno(new Alergeno("Gluten"));
    }

    @Test
    @DisplayName("Calcula impuesto del 8 %, propina sugerida del 10 % y total")
    public void calculaImpuestoYPropina() {
        VentaCafeteria venta = new VentaCafeteria(LocalDate.now(), ana);
        venta.agregarBebida(jugo, 2);

        assertEquals(10000, venta.calcularSubtotal());
        assertEquals(800, venta.calcularImpuesto());
        assertEquals(1000, venta.calcularPropina());
        assertEquals(11800, venta.calcularTotal());
    }

    @Test
    @DisplayName("El socio puede cambiar la propina, incluso a cero")
    public void propinaEnCero() {
        VentaCafeteria venta = new VentaCafeteria(LocalDate.now(), ana);
        venta.agregarBebida(jugo, 2);

        venta.setPorcentajePropina(0);

        assertEquals(0, venta.calcularPropina());
        assertEquals(10800, venta.calcularTotal());
    }

    @Test
    @DisplayName("Rechaza una propina negativa")
    public void rechazaPropinaNegativa() {
        VentaCafeteria venta = new VentaCafeteria(LocalDate.now(), ana);

        assertThrows(IllegalArgumentException.class, () -> venta.setPorcentajePropina(-5));
        assertEquals(10, venta.getPorcentajePropina(), "Debe quedar la propina sugerida");
    }

    @Test
    @DisplayName("Con snacks, no se puede finalizar sin informar los alergenos")
    public void rechazaFinalizarSinInformarAlergenos() {
        VentaCafeteria venta = new VentaCafeteria(LocalDate.now(), ana);
        venta.agregarSnack(galletas, 1);

        assertThrows(IllegalStateException.class, () -> venta.finalizar());
        assertFalse(venta.isFinalizada());
    }

    @Test
    @DisplayName("Con los alergenos informados, la venta se finaliza")
    public void finalizaConAlergenosInformados() {
        VentaCafeteria venta = new VentaCafeteria(LocalDate.now(), ana);
        venta.agregarSnack(galletas, 1);

        assertEquals(1, venta.getAlergenosAInformar().size());
        venta.confirmarAlergenos();
        venta.finalizar();

        assertTrue(venta.isFinalizada());
    }

    @Test
    @DisplayName("Rechaza vender bebida caliente a un socio que esta en una cancha de padel")
    public void rechazaBebidaCalienteEnPadel() throws Exception {
        CanchaPadel cancha = new CanchaPadel("P1", new Padel("Padel", 2));
        ana.setInstalacionActual(cancha);
        VentaCafeteria venta = new VentaCafeteria(LocalDate.now(), ana);

        assertThrows(IllegalStateException.class, () -> venta.agregarBebida(cafe, 1));
        assertEquals(0, venta.calcularSubtotal());
    }

    @Test
    @DisplayName("Fuera de una instalacion restringida, la bebida caliente se vende")
    public void permiteBebidaCalienteSinRestriccion() {
        VentaCafeteria venta = new VentaCafeteria(LocalDate.now(), ana);

        venta.agregarBebida(cafe, 1);
        venta.finalizar();

        assertTrue(venta.isFinalizada());
        assertTrue(ana.isBebidaCaliente(), "El socio queda marcado con bebida caliente");
    }
}