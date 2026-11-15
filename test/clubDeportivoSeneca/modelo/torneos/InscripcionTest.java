package clubDeportivoSeneca.modelo.torneos;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import clubDeportivoSeneca.modelo.deportes.CategoriaEdad;
import clubDeportivoSeneca.modelo.deportes.Futbol;
import clubDeportivoSeneca.modelo.deportes.Tenis;
import clubDeportivoSeneca.modelo.equipos.Equipo;
import clubDeportivoSeneca.modelo.individual.NivelJugador;
import clubDeportivoSeneca.modelo.usuario.Jugador;

public class InscripcionTest {

    private Jugador ana;
    private Jugador luis;

    @Before
    public void preparar() throws Exception {
        ana = crearJugador("Ana");
        luis = crearJugador("Luis");
    }

    @Test
    public void constructorConEquipo() throws Exception {
        Equipo equipo = new Equipo("Halcones", new Futbol("Fútbol", 7, true), new CategoriaEdad("Sub-17", 15, 30), 11);
        Inscripcion inscripcion = new Inscripcion(equipo, true);

        assertTrue(inscripcion.esEquipo());
        assertSame(equipo, inscripcion.getEquipo());
        assertTrue(inscripcion.getJugadores().isEmpty());
        assertTrue(inscripcion.isCupoPreferencial());
        assertThrows(Exception.class, () -> new Inscripcion((Equipo) null, false));
    }

    @Test
    public void constructorConJugadores() throws Exception {
        Inscripcion individual = new Inscripcion(List.of(ana), false);
        assertFalse(individual.esEquipo());
        assertNull(individual.getEquipo());
        assertEquals(1, individual.getJugadores().size());

        assertEquals(2, new Inscripcion(List.of(ana, luis), false).getJugadores().size());

        assertThrows(Exception.class, () -> new Inscripcion((List<Jugador>) null, false));
        assertThrows(Exception.class, () -> new Inscripcion(new ArrayList<Jugador>(), false));
        assertThrows(Exception.class, () -> new Inscripcion(List.of(ana, luis, ana), false));

        List<Jugador> original = new ArrayList<>();
        original.add(ana);
        Inscripcion copia = new Inscripcion(original, false);
        original.add(luis);
        assertEquals(1, copia.getJugadores().size());
    }

    @Test
    public void descripcion() throws Exception {
        Equipo equipo = new Equipo("Halcones", new Futbol("Fútbol", 7, true), new CategoriaEdad("Sub-30", 15, 30), 11);
        assertEquals("Halcones", new Inscripcion(equipo, false).descripcion());
        assertEquals("Ana", new Inscripcion(List.of(ana), false).descripcion());
        assertEquals("Ana / Luis", new Inscripcion(List.of(ana, luis), false).descripcion());
    }

    @Test
    public void setCupoPreferencial() throws Exception {
        Inscripcion inscripcion = new Inscripcion(List.of(ana), false);
        inscripcion.setCupoPreferencial(true);
        assertTrue(inscripcion.isCupoPreferencial());
    }

    private static Jugador crearJugador(String nombre) throws Exception {
        Jugador jugador = new Jugador(nombre, "x", nombre, LocalDate.of(2000, 1, 1));
        jugador.asignarNivel(new Tenis("Tenis", 2), NivelJugador.INTERMEDIO);
        return jugador;
    }
}
