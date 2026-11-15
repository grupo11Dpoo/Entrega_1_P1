package clubDeportivoSeneca.modelo.torneos;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import clubDeportivoSeneca.modelo.deportes.CategoriaEdad;
import clubDeportivoSeneca.modelo.deportes.Futbol;
import clubDeportivoSeneca.modelo.deportes.Tenis;
import clubDeportivoSeneca.modelo.equipos.Equipo;
import clubDeportivoSeneca.modelo.individual.NivelJugador;
import clubDeportivoSeneca.modelo.usuario.Jugador;

public class TorneoRecreativoTest {

    private Tenis tenis;
    private LocalDate manana;

    @Before
    public void preparar() throws Exception {
        tenis = new Tenis("Tenis", 2);
        manana = LocalDate.now().plusDays(1);
    }

    private TorneoRecreativo individual(double porcentaje) throws Exception {
        return new TorneoRecreativo("Social", tenis, null, NivelJugador.INTERMEDIO, false, 8, manana, porcentaje);
    }

    @Test
    public void constructor() throws Exception {
        assertEquals(0.25, individual(0.25).getPorcentajeBono(), 0.0001);
        assertEquals(1.0, individual(1).getPorcentajeBono(), 0.0001);

        assertThrows(Exception.class, () -> individual(0));
        assertThrows(Exception.class, () -> individual(-0.1));
        assertThrows(Exception.class, () -> individual(1.5));
    }

    private TorneoRecreativo individualIniciado() throws Exception {
        return new TorneoRecreativo("Social", tenis, null, NivelJugador.INTERMEDIO, false, 8, LocalDate.now().minusDays(1), 0.2);
    }

    private Inscripcion agregarInscripcion(Torneo t, Jugador... jugadores) throws Exception {
        Inscripcion inscripcion = new Inscripcion(List.of(jugadores), false);
        t.inscripcionesInternas().add(inscripcion);
        return inscripcion;
    }

    @Test
    public void entregarPremiosAlFinalizar() throws Exception {
        TorneoRecreativo t = individualIniciado();
        Jugador ana = crearJugador("ana");
        Jugador luis = crearJugador("luis");
        Inscripcion primero = agregarInscripcion(t, ana);
        Inscripcion segundo = agregarInscripcion(t, luis);

        t.finalizar(List.of(primero, segundo));

        assertEquals(1, ana.getBonos().size());
        BonoDescuento bono = ana.getBonos().get(0);
        assertEquals("Social", bono.getTorneoOrigen());
        assertEquals(0.2, bono.getPorcentaje(), 0.0001);
        assertEquals(LocalDate.now().plusMonths(3), bono.getVigenteHasta());
        assertFalse(bono.isRedimido());
        assertEquals(0, luis.getBonos().size());
    }

    @Test
    public void entregarPremiosEnDoblesYConjunto() throws Exception {
        LocalDate ayer = LocalDate.now().minusDays(1);

        TorneoRecreativo dobles = new TorneoRecreativo("Dobles", tenis, null, NivelJugador.INTERMEDIO, true, 8, ayer, 0.3);
        Jugador ana = crearJugador("ana");
        Jugador luis = crearJugador("luis");
        Jugador eva = crearJugador("eva");
        Inscripcion campeona = agregarInscripcion(dobles, ana, luis);
        Inscripcion otra = agregarInscripcion(dobles, eva, crearJugador("sara"));
        dobles.finalizar(List.of(campeona, otra));
        assertEquals(1, ana.getBonos().size());
        assertEquals(1, luis.getBonos().size());
        assertEquals(0, eva.getBonos().size());

        Futbol futbol = new Futbol("Fútbol", 7, true);
        CategoriaEdad categoria = new CategoriaEdad("Sub-30", 15, 30);
        TorneoRecreativo liga = new TorneoRecreativo("Liga", futbol, categoria, null, false, 8, ayer, 0.15);
        Equipo campeon = crearEquipoCompleto("Halcones", futbol, categoria);
        Equipo subcampeon = crearEquipoCompleto("Aguilas", futbol, categoria);
        Inscripcion iCampeon = new Inscripcion(campeon, false);
        Inscripcion iSub = new Inscripcion(subcampeon, false);
        liga.inscripcionesInternas().add(iCampeon);
        liga.inscripcionesInternas().add(iSub);
        liga.finalizar(List.of(iCampeon, iSub));
        for (Jugador j : campeon.getJugadores()) {
            assertEquals(1, j.getBonos().size());
        }
        for (Jugador j : subcampeon.getJugadores()) {
            assertEquals(0, j.getBonos().size());
        }
    }

    @Test
    public void entregarBonos() throws Exception {
        TorneoRecreativo t = individualIniciado();
        Jugador ana = crearJugador("ana");
        Inscripcion campeon = agregarInscripcion(t, ana);
        Inscripcion otro = agregarInscripcion(t, crearJugador("luis"));

        assertThrows(Exception.class, () -> t.entregarBonos("Social", 0.2, LocalDate.now().plusMonths(1)));

        assertThrows(Exception.class, () -> t.finalizar(List.of(campeon)));
        assertEquals(0, ana.getBonos().size());
        assertEquals(EstadoTorneo.EN_CURSO, t.getEstado());

        t.finalizar(List.of(campeon, otro));
        BonoDescuento bono = ana.getBonos().get(0);
        bono.redimir();
        assertTrue(bono.isRedimido());
        assertThrows(Exception.class, bono::redimir);
    }


    private Jugador crearJugador(String nombre) throws Exception {
        Jugador jugador = new Jugador(nombre, "x", nombre, LocalDate.of(2000, 1, 1));
        jugador.asignarNivel(tenis, NivelJugador.INTERMEDIO);
        return jugador;
    }

    private Equipo crearEquipoCompleto(String nombre, Futbol futbol, CategoriaEdad categoria) throws Exception {
        Equipo equipo = new Equipo(nombre, futbol, categoria, 11);
        for (int k = 1; k <= 7; k++) {
            Jugador j = new Jugador(nombre + k, "x", nombre + " " + k, LocalDate.now().minusYears(20));
            equipo.inscribirJugador(j, "delantero", k);
        }
        return equipo;
    }
}
