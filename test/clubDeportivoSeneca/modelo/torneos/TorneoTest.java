package clubDeportivoSeneca.modelo.torneos;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import clubDeportivoSeneca.modelo.deportes.CategoriaEdad;
import clubDeportivoSeneca.modelo.deportes.Futbol;
import clubDeportivoSeneca.modelo.deportes.Tenis;
import clubDeportivoSeneca.modelo.equipos.Equipo;
import clubDeportivoSeneca.modelo.individual.NivelJugador;
import clubDeportivoSeneca.modelo.individual.Ranking;
import clubDeportivoSeneca.modelo.turnos.GestorTurnos;
import clubDeportivoSeneca.modelo.usuario.Entrenador;
import clubDeportivoSeneca.modelo.usuario.Jugador;

public class TorneoTest {

    private Tenis tenis;
    private LocalDate manana;
    private TorneoRecreativo torneo;
    private Jugador ana;
    private Jugador luis;

    @Before
    public void setUp() throws Exception {
        tenis = new Tenis("Tenis", 2);
        manana = LocalDate.now().plusDays(1);
        torneo = new TorneoRecreativo("Copa", tenis, null, NivelJugador.INTERMEDIO, false, 5, manana, 0.2);
        ana = crearJugador("Ana");
        luis = crearJugador("Luis");
    }

    @Test
    public void testConstructor() throws Exception {
        assertEquals("Copa", torneo.getNombre());
        assertEquals(5, torneo.getCupos());
        assertEquals(EstadoTorneo.EN_CURSO, torneo.getEstado());
        assertFalse(torneo.esDeConjunto());
        assertTrue(torneo.getInscripciones().isEmpty());

        assertThrows(Exception.class, () -> new TorneoRecreativo(" ", tenis, null, NivelJugador.INTERMEDIO, false, 5, manana, 0.2));
        assertThrows(Exception.class, () -> new TorneoRecreativo("Copa", tenis, null, NivelJugador.INTERMEDIO, false, 0, manana, 0.2));
        assertThrows(Exception.class, () -> new TorneoRecreativo("Copa", tenis, null, null, false, 5, manana, 0.2));
    }

    @Test
    public void testCuposPreferenciales() throws Exception {
        assertEquals(1, torneo.cuposPreferenciales());
        TorneoRecreativo grande = new TorneoRecreativo("Grande", tenis, null, NivelJugador.INTERMEDIO, false, 11, manana, 0.2);
        assertEquals(3, grande.cuposPreferenciales());
    }

    @Test
    public void testInscribirJugador() throws Exception {
        torneo.inscribirJugador(ana);

        assertEquals(1, torneo.getInscripciones().size());
        assertEquals(4, torneo.cuposDisponibles());

        assertThrows(Exception.class, () -> torneo.inscribirJugador(ana));
        
        Jugador novato = crearJugador("Novato");
        novato.asignarNivel(tenis, NivelJugador.PRINCIPIANTE);
        
        assertThrows(Exception.class, () -> torneo.inscribirJugador(novato));
        assertThrows(Exception.class, () -> torneo.inscribirJugador(null));
    }

    @Test
    public void testInscribirJugadorSinCupos() throws Exception {
        TorneoRecreativo pequeno = new TorneoRecreativo("Pequeno", tenis, null, NivelJugador.INTERMEDIO, false, 1, manana, 0.2);
        pequeno.inscribirJugador(ana);

        assertThrows(Exception.class, () -> pequeno.inscribirJugador(luis));
    }

    @Test
    public void testInscribirJugadorEmpleado() throws Exception {
        GestorTurnos gestor = new GestorTurnos(LocalTime.of(6, 0), LocalTime.of(22, 0));

        Entrenador conTurno = crearEntrenador("pepe");
        gestor.asignarTurno(conTurno, manana, LocalTime.of(8, 0), LocalTime.of(12, 0));
        assertThrows(Exception.class, () -> torneo.inscribirJugador(conTurno.getPerfilJugador()));

        Entrenador sinTurno = crearEntrenador("maria");
        torneo.inscribirJugador(sinTurno.getPerfilJugador());
        assertEquals(1, torneo.getInscripciones().size());
    }

    @Test
    public void testInscribirPareja() throws Exception {
        TorneoRecreativo dobles = new TorneoRecreativo("Dobles", tenis, null, NivelJugador.INTERMEDIO, true, 5, manana, 0.2);

        Inscripcion pareja = dobles.inscribirPareja(ana, luis);
        assertEquals(2, pareja.getJugadores().size());

        Jugador eva = crearJugador("Eva");
        assertThrows(Exception.class, () -> dobles.inscribirPareja(eva, eva));
        assertThrows(Exception.class, () -> torneo.inscribirPareja(eva, crearJugador("Sara")));
    }

    @Test
    public void testInscribirEquipo() throws Exception {
        Futbol futbol = new Futbol("Futbol", 7, true);
        CategoriaEdad categoria = new CategoriaEdad("Sub-30", 15, 30);
        TorneoRecreativo liga = new TorneoRecreativo("Liga", futbol, categoria, null, false, 5, manana, 0.2);
        Equipo equipo = crearEquipo("Halcones", futbol, categoria);

        Inscripcion inscripcion = liga.inscribirEquipo(equipo);
        assertTrue(inscripcion.esEquipo());

        assertThrows(Exception.class, () -> liga.inscribirEquipo(equipo));
        assertThrows(Exception.class, () -> torneo.inscribirEquipo(equipo));
    }

    @Test
    public void testDesinscribir() throws Exception {
        Inscripcion inscripcion = torneo.inscribirJugador(ana);

        torneo.desinscribir(inscripcion);

        assertEquals(5, torneo.cuposDisponibles());
        assertTrue(torneo.getInscripciones().isEmpty());
        assertThrows(Exception.class, () -> torneo.desinscribir(inscripcion));
    }

    @Test
    public void testDespuesDeIniciar() throws Exception {
        TorneoRecreativo iniciado = crearTorneoIniciado();
        Inscripcion inscripcion = agregarInscripcion(iniciado, ana);

        assertFalse(iniciado.admiteInscripciones());
        assertThrows(Exception.class, () -> iniciado.inscribirJugador(luis));
        assertThrows(Exception.class, () -> iniciado.desinscribir(inscripcion));
    }

    @Test
    public void testCupoPreferencialParaDestacados() throws Exception {

        Ranking rankingAna = new Ranking(ana, tenis);
        rankingAna.actualizarElo(1000, true, LocalDate.now());
        List<Ranking> rankings = new ArrayList<>();
        rankings.add(rankingAna);
        rankings.add(new Ranking(luis, tenis));

        torneo.definirDestacadosPorRanking(rankings);

        assertEquals(1, torneo.getJugadoresDestacados().size());
        assertEquals(ana, torneo.getJugadoresDestacados().get(0));

        for (int i = 0; i < 4; i++) {
            torneo.inscribirJugador(crearJugador("Regular" + i));
        }

        assertThrows(Exception.class, () -> torneo.inscribirJugador(luis));

        Inscripcion deAna = torneo.inscribirJugador(ana);
        assertTrue(deAna.isCupoPreferencial());
    }

    @Test
    public void testDefinirEquiposDestacados() throws Exception {
        Futbol futbol = new Futbol("Futbol", 7, true);
        CategoriaEdad categoria = new CategoriaEdad("Sub-30", 15, 30);
        TorneoRecreativo liga = new TorneoRecreativo("Liga", futbol, categoria, null, false, 5, manana, 0.2);
        Equipo mejor = crearEquipo("Mejor", futbol, categoria);
        Equipo otro = crearEquipo("Otro", futbol, categoria);

        liga.definirEquiposDestacados(List.of(mejor, otro));

        assertEquals(1, liga.getEquiposDestacados().size());
        assertTrue(liga.inscribirEquipo(mejor).isCupoPreferencial());
        assertFalse(liga.inscribirEquipo(otro).isCupoPreferencial());
        assertThrows(Exception.class, () -> torneo.definirEquiposDestacados(List.of(mejor)));
    }

    @Test
    public void testRegistrarEncuentro() throws Exception {
        Inscripcion a = torneo.inscribirJugador(ana);
        Inscripcion b = torneo.inscribirJugador(luis);

        Encuentro encuentro = torneo.registrarEncuentro(a, b, LocalDateTime.now().plusDays(2));

        assertEquals(1, torneo.getEncuentros().size());
        assertEquals(a, encuentro.getLocal());
        assertThrows(Exception.class, () -> torneo.registrarEncuentro(a, a, LocalDateTime.now().plusDays(2)));
        Inscripcion ajena = new Inscripcion(List.of(crearJugador("Ajeno")), false);
        assertThrows(Exception.class, () -> torneo.registrarEncuentro(a, ajena, LocalDateTime.now().plusDays(2)));
    }

    @Test
    public void testFinalizar() throws Exception {

        Inscripcion x = torneo.inscribirJugador(ana);
        assertThrows(Exception.class, () -> torneo.finalizar(List.of(x)));

        TorneoRecreativo iniciado = crearTorneoIniciado();
        Inscripcion a = agregarInscripcion(iniciado, ana);
        Inscripcion b = agregarInscripcion(iniciado, luis);

        assertThrows(Exception.class, () -> iniciado.finalizar(List.of(a)));
        assertEquals(EstadoTorneo.EN_CURSO, iniciado.getEstado());

        iniciado.finalizar(List.of(b, a));

        assertEquals(EstadoTorneo.FINALIZADO, iniciado.getEstado());
        assertEquals(b, iniciado.getPosiciones().get(0));
        assertEquals(a, iniciado.getPosiciones().get(1));
    }

    @Test
    public void testHistorialDetallado() throws Exception {
        TorneoRecreativo iniciado = crearTorneoIniciado();
        Inscripcion a = agregarInscripcion(iniciado, ana);
        Inscripcion b = agregarInscripcion(iniciado, luis);
        Encuentro encuentro = iniciado.registrarEncuentro(a, b, LocalDateTime.now().plusDays(2));
        encuentro.registrarResultado(a, "6-4");
        iniciado.finalizar(List.of(a, b));

        String historial = iniciado.historialDetallado();

        assertTrue(historial.contains("Iniciado"));
        assertTrue(historial.contains("FINALIZADO"));
        assertTrue(historial.contains("Ana vs Luis"));
        assertTrue(historial.contains("6-4"));
        assertTrue(historial.contains("1. Ana"));
    }


    private Jugador crearJugador(String nombre) throws Exception {
        Jugador jugador = new Jugador(nombre, "x", nombre, LocalDate.of(2000, 1, 1));
        jugador.asignarNivel(tenis, NivelJugador.INTERMEDIO);
        return jugador;
    }

    private Entrenador crearEntrenador(String nombre) throws Exception {
        Entrenador entrenador = new Entrenador(nombre, "x", nombre, "COD" + nombre);
        Jugador perfil = crearJugador(nombre + "J");
        entrenador.setPerfilJugador(perfil);
        perfil.setPerfilEmpleado(entrenador);
        return entrenador;
    }

    private Equipo crearEquipo(String nombre, Futbol futbol, CategoriaEdad categoria) throws Exception {
        Equipo equipo = new Equipo(nombre, futbol, categoria, 11);
        for (int i = 1; i <= 7; i++) {
            Jugador jugador = new Jugador(nombre + i, "x", nombre + i, LocalDate.now().minusYears(20));
            equipo.inscribirJugador(jugador, "delantero", i);
        }
        return equipo;
    }

    private TorneoRecreativo crearTorneoIniciado() throws Exception {
        LocalDate ayer = LocalDate.now().minusDays(1);
        return new TorneoRecreativo("Iniciado", tenis, null, NivelJugador.INTERMEDIO, false, 5, ayer, 0.2);
    }

    private Inscripcion agregarInscripcion(Torneo t, Jugador jugador) throws Exception {
        Inscripcion inscripcion = new Inscripcion(List.of(jugador), false);
        t.inscripcionesInternas().add(inscripcion);
        return inscripcion;
    }
}
