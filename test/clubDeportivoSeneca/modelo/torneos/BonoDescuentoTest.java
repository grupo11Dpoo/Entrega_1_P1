package clubDeportivoSeneca.modelo.torneos;

import static org.junit.jupiter.api.Assertions.*;  

import java.time.LocalDate;

import org.junit.Test;

public class BonoDescuentoTest {

    @Test
    public void constructor() throws Exception {
        LocalDate hasta = LocalDate.now().plusDays(30);
        BonoDescuento bono = new BonoDescuento("Copa", 0.25, hasta);

        assertEquals("Copa", bono.getTorneoOrigen());
        assertEquals(0.25, bono.getPorcentaje(), 0.0001);
        assertEquals(hasta, bono.getVigenteHasta());
        assertFalse(bono.isRedimido());
        assertEquals(1.0, new BonoDescuento("Copa", 1, hasta).getPorcentaje(), 0.0001);

        assertThrows(Exception.class, () -> new BonoDescuento(null, 0.2, hasta));
        assertThrows(Exception.class, () -> new BonoDescuento("  ", 0.2, hasta));
        assertThrows(Exception.class, () -> new BonoDescuento("Copa", 0, hasta));
        assertThrows(Exception.class, () -> new BonoDescuento("Copa", 1.01, hasta));
        assertThrows(Exception.class, () -> new BonoDescuento("Copa", 0.2, null));
    }

    @Test
    public void estaVigente() throws Exception {
        assertTrue(new BonoDescuento("Copa", 0.2, LocalDate.now().plusDays(5)).estaVigente());
        assertTrue(new BonoDescuento("Copa", 0.2, LocalDate.now()).estaVigente());
        assertFalse(new BonoDescuento("Copa", 0.2, LocalDate.now().minusDays(1)).estaVigente());
    }

    @Test
    public void redimir() throws Exception {
        BonoDescuento bono = new BonoDescuento("Copa", 0.2, LocalDate.now().plusDays(5));
        bono.redimir();
        assertTrue(bono.isRedimido());
        assertThrows(Exception.class, bono::redimir);

        BonoDescuento vencido = new BonoDescuento("Copa", 0.2, LocalDate.now().minusDays(1));
        assertThrows(Exception.class, vencido::redimir);
        assertFalse(vencido.isRedimido());
    }
}
