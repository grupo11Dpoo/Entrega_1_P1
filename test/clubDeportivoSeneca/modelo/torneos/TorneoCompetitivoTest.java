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
import clubDeportivoSeneca.modelo.usuario.Entrenador;
import clubDeportivoSeneca.modelo.usuario.Jugador;

public class TorneoCompetitivoTest {

    private static final double TARIFA = 10000;

    private Tenis tenis;
    private LocalDate manana;

    @Before
    public void preparar() throws Exception {
        tenis = new Tenis("Tenis", 2);
        manana = LocalDate.now().plusDays(1);
    }

    private TorneoCompetitivo individual() throws Exception {
        return new TorneoCompetitivo("Abierto", tenis, null, NivelJugador.INTERMEDIO, false, 8, manana, TARIFA);
    }

    private TorneoCompetitivo dobles() throws Exception {
        return new TorneoCompetitivo("Dobles", tenis, null, NivelJugador.INTERMEDIO, true, 8, manana, TARIFA);
    }

    private TorneoCompetitivo individualIniciado() throws Exception {
        return new TorneoCompetitivo("Abierto", tenis, null, NivelJugador.INTERMEDIO, false, 8, LocalDate.now().minusDays(1), TARIFA);
    }

    private TorneoCompetitivo doblesIniciado() throws Exception {
        return new TorneoCompetitivo("Dobles", tenis, null, NivelJugador.INTERMEDIO, true, 8, LocalDate.now().minusDays(1), TARIFA);
    }

    private Inscripcion agregarInscripcion(Torneo t, Jugador... jugadores) throws Exception {
        Inscripcion inscripcion = new Inscripcion(List.of(jugadores), false);
        t.inscripcionesInternas().add(inscripcion);
        return inscripcion;
    }

    @Test
    public void constructor() throws Exception {
        assertEquals(TARIFA, individual().getTarifaInscripcion(), 0.001);

        assertThrows(Exception.class, () -> new TorneoCompetitivo("A", tenis, null, NivelJugador.INTERMEDIO, false, 8, manana, 0));
        assertThrows(Exception.class, () -> new TorneoCompetitivo("A", tenis, null, NivelJugador.INTERMEDIO, false, 8, manana, -5));
        assertThrows(Exception.class, () -> new TorneoCompetitivo("A", tenis, null, null, false, 8, manana, TARIFA)); 
    }

    @Test
    public void totalRecaudado() throws Exception {
        TorneoCompetitivo t = individual();
        assertEquals(0, t.totalRecaudado(), 0.001);

        Inscripcion ana = t.inscribirJugador(crearJugador("ana"));
        t.inscribirJugador(crearJugador("luis"));
        t.inscribirJugador(crearEmpleado("pepe"));
        assertEquals(2 * TARIFA, t.totalRecaudado(), 0.001);

        t.desinscribir(ana);
        assertEquals(TARIFA, t.totalRecaudado(), 0.001);

        TorneoCompetitivo d = dobles();
        d.inscribirPareja(crearJugador("a"), crearJugador("b"));
        d.inscribirPareja(crearJugador("c"), crearEmpleado("empleado"));
        assertEquals(3 * TARIFA, d.totalRecaudado(), 0.001);

        Futbol futbol = new Futbol("Fútbol", 7, true);
        CategoriaEdad categoria = new CategoriaEdad("Sub-30", 15, 30);
        TorneoCompetitivo conjunto = new TorneoCompetitivo("Liga", futbol, categoria, null, false, 8, manana, TARIFA);
        conjunto.inscribirEquipo(crearEquipoCompleto("Halcones", futbol, categoria));
        assertEquals(TARIFA, conjunto.totalRecaudado(), 0.001);
    }

    @Test
    public void premioPara() throws Exception {
        TorneoCompetitivo t = individualIniciado();
        Inscripcion primero = agregarInscripcion(t, crearJugador("ana"));
        Inscripcion segundo = agregarInscripcion(t, crearJugador("luis"));
        Inscripcion tercero = agregarInscripcion(t, crearJugador("eva"));
        Inscripcion ajena = individual().inscribirJugador(crearJugador("otro"));
        assertEquals(0, t.premioPara(primero), 0.001);

        t.finalizar(List.of(primero, segundo, tercero));
        assertEquals(3 * TARIFA, t.premioPara(primero), 0.001);
        assertEquals(0, t.premioPara(segundo), 0.001);
        assertEquals(0, t.premioPara(tercero), 0.001);
        assertEquals(0, t.premioPara(ajena), 0.001);
        assertEquals(0, t.premioPara(null), 0.001);
    }

    @Test
    public void premioParaConEmpleados() throws Exception {

        TorneoCompetitivo t = individualIniciado();
        Inscripcion deEmpleado = agregarInscripcion(t, crearEmpleado("pepe"));
        Inscripcion deAna = agregarInscripcion(t, crearJugador("ana"));
        t.finalizar(List.of(deEmpleado, deAna));
        assertEquals(0, t.premioPara(deEmpleado), 0.001);

        TorneoCompetitivo d = doblesIniciado();
        Inscripcion mixta = agregarInscripcion(d, crearJugador("ana"), crearEmpleado("pepe"));
        Inscripcion otra = agregarInscripcion(d, crearJugador("luis"), crearJugador("eva"));
        d.finalizar(List.of(mixta, otra));
        assertEquals(1.5 * TARIFA, d.premioPara(mixta), 0.001);

        TorneoCompetitivo d2 = doblesIniciado();
        Inscripcion campeona = agregarInscripcion(d2, crearJugador("a"), crearJugador("b"));
        Inscripcion rival = agregarInscripcion(d2, crearJugador("c"), crearJugador("d"));
        d2.finalizar(List.of(campeona, rival));
        assertEquals(4 * TARIFA, d2.premioPara(campeona), 0.001);
    }


    private Jugador crearJugador(String nombre) throws Exception {
        Jugador jugador = new Jugador(nombre, "x", nombre, LocalDate.of(2000, 1, 1));
        jugador.asignarNivel(tenis, NivelJugador.INTERMEDIO);
        return jugador;
    }

    private Jugador crearEmpleado(String nombre) throws Exception {
        Entrenador entrenador = new Entrenador(nombre, "x", nombre, "COD");
        Jugador perfil = crearJugador(nombre + "J");
        entrenador.setPerfilJugador(perfil);
        perfil.setPerfilEmpleado(entrenador);
        return perfil;
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
