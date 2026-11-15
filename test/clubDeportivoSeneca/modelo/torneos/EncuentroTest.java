package clubDeportivoSeneca.modelo.torneos;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import clubDeportivoSeneca.modelo.deportes.Tenis;
import clubDeportivoSeneca.modelo.individual.NivelJugador;
import clubDeportivoSeneca.modelo.usuario.Jugador;

public class EncuentroTest {

    private Inscripcion local;
    private Inscripcion visitante;
    private LocalDateTime fecha;

    @Before
    public void preparar() throws Exception {
        local = crearInscripcion("Ana");
        visitante = crearInscripcion("Luis");
        fecha = LocalDateTime.now().plusDays(2);
    }

    @Test
    public void constructor() throws Exception {
        Encuentro encuentro = new Encuentro(local, visitante, fecha);

        assertSame(local, encuentro.getLocal());
        assertSame(visitante, encuentro.getVisitante());
        assertEquals(fecha, encuentro.getFecha());
        assertFalse(encuentro.estaJugado());
        assertNull(encuentro.getGanador());
        assertNull(encuentro.getResultado());

        assertThrows(Exception.class, () -> new Encuentro(null, visitante, fecha));
        assertThrows(Exception.class, () -> new Encuentro(local, null, fecha));
        assertThrows(Exception.class, () -> new Encuentro(local, visitante, null));
    }

    @Test
    public void registrarResultado() throws Exception {
        Encuentro encuentro = new Encuentro(local, visitante, fecha);

        assertThrows(Exception.class, () -> encuentro.registrarResultado(crearInscripcion("Ajeno"), "6-0"));
        assertThrows(Exception.class, () -> encuentro.registrarResultado(null, "6-0"));
        assertThrows(Exception.class, () -> encuentro.registrarResultado(local, "  "));
        assertFalse(encuentro.estaJugado());

        encuentro.registrarResultado(visitante, "2-6");
        assertTrue(encuentro.estaJugado());
        assertSame(visitante, encuentro.getGanador());
        assertEquals("2-6", encuentro.getResultado());

        assertThrows(Exception.class, () -> encuentro.registrarResultado(local, "7-5"));
        assertSame(visitante, encuentro.getGanador());
    }

    private static Inscripcion crearInscripcion(String nombre) throws Exception {
        Jugador jugador = new Jugador(nombre, "x", nombre, LocalDate.of(2000, 1, 1));
        jugador.asignarNivel(new Tenis("Tenis", 2), NivelJugador.INTERMEDIO);
        return new Inscripcion(List.of(jugador), false);
    }
}
