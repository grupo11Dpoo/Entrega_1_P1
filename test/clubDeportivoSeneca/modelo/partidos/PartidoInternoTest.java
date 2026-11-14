package clubDeportivoSeneca.modelo.partidos;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import clubDeportivoSeneca.modelo.deportes.Baloncesto;
import clubDeportivoSeneca.modelo.deportes.CategoriaEdad;
import clubDeportivoSeneca.modelo.deportes.Futbol;
import clubDeportivoSeneca.modelo.equipos.Equipo;
import clubDeportivoSeneca.modelo.usuario.Jugador;

public class PartidoInternoTest {

    private Futbol futbol;
    private CategoriaEdad sub15;
    private Equipo locales;
    private Equipo visitantes;
    private LocalDate proximaSemana;

    @BeforeEach
    public void setUp() throws Exception {
        futbol = new Futbol("Futbol", 7, true);
        sub15 = new CategoriaEdad("Sub-15", 13, 15);
        locales = equipoCompleto("Locales", sub15, "L");
        visitantes = equipoCompleto("Visitantes", sub15, "V");
        proximaSemana = LocalDate.now().plusWeeks(1);
    }

    /** Crea un equipo de futbol con 7 jugadores de 14 anos (el minimo para jugar). */
    private Equipo equipoCompleto(String nombre, CategoriaEdad categoria, String prefijo) throws Exception {
        Equipo equipo = new Equipo(nombre, futbol, categoria, 15);
        for (int i = 1; i <= 7; i++) {
            Jugador jugador = new Jugador(prefijo + i, "1234", "Jugador " + prefijo + i,
                    LocalDate.now().minusYears(14).minusDays(1));
            equipo.inscribirJugador(jugador, "Jugador", i);
        }
        return equipo;
    }

    @Test
    @DisplayName("Programa un partido entre dos equipos completos del mismo deporte y categoria")
    public void programaPartidoValido() throws Exception {
        PartidoInterno partido = new PartidoInterno(proximaSemana, futbol, locales, visitantes);

        assertFalse(partido.isFinalizado());
        assertEquals(visitantes, partido.getEquipoVisitante());
    }

    @Test
    @DisplayName("Rechaza un partido de un equipo contra si mismo")
    public void rechazaMismoEquipo() {
        assertThrows(Exception.class, () -> new PartidoInterno(proximaSemana, futbol, locales, locales));
    }

    @Test
    @DisplayName("Rechaza un partido si un equipo no tiene el minimo de 7 habilitados")
    public void rechazaEquipoIncompleto() throws Exception {
        Equipo incompleto = new Equipo("Incompleto", futbol, sub15, 15);

        assertThrows(Exception.class, () -> new PartidoInterno(proximaSemana, futbol, locales, incompleto));
    }

    @Test
    @DisplayName("Rechaza un partido entre equipos de categorias distintas (HU-T04)")
    public void rechazaCategoriasDistintas() throws Exception {
        CategoriaEdad mayores = new CategoriaEdad("Mayores", 18, 40);
        Equipo equipoMayores = new Equipo("Mayores", futbol, mayores, 15);
        for (int i = 1; i <= 7; i++) {
            equipoMayores.inscribirJugador(new Jugador("m" + i, "1234", "Mayor " + i,
                    LocalDate.of(1995, 1, 1)), "Jugador", i);
        }

        assertThrows(Exception.class, () -> new PartidoInterno(proximaSemana, futbol, locales, equipoMayores));
    }

    @Test
    @DisplayName("Registrar el marcador define el ganador")
    public void registrarMarcadorDefineGanador() throws Exception {
        PartidoInterno partido = new PartidoInterno(proximaSemana, futbol, locales, visitantes);

        partido.registrarMarcador(2, 1);
        partido.finalizarPartido();

        assertTrue(partido.isFinalizado());
        assertEquals(locales, partido.getGanador());
    }

    @Test
    @DisplayName("En futbol se permite el empate y no hay ganador")
    public void futbolPermiteEmpate() throws Exception {
        PartidoInterno partido = new PartidoInterno(proximaSemana, futbol, locales, visitantes);

        partido.registrarMarcador(1, 1);

        assertNull(partido.getGanador());
    }

    @Test
    @DisplayName("Rechaza finalizar un partido sin marcador")
    public void rechazaFinalizarSinMarcador() throws Exception {
        PartidoInterno partido = new PartidoInterno(proximaSemana, futbol, locales, visitantes);

        assertThrows(Exception.class, () -> partido.finalizarPartido());
        assertFalse(partido.isFinalizado());
    }

    @Test
    @DisplayName("Rechaza registrar un marcador en un partido ya finalizado")
    public void rechazaMarcadorEnPartidoFinalizado() throws Exception {
        PartidoInterno partido = new PartidoInterno(proximaSemana, futbol, locales, visitantes);
        partido.registrarMarcador(2, 1);
        partido.finalizarPartido();

        assertThrows(Exception.class, () -> partido.registrarMarcador(0, 0));
        assertEquals(2, partido.getMarcadorLocal());
    }

    @Test
    @DisplayName("En futbol la victoria da 3 puntos y el empate 1 a cada equipo")
    public void puntosDeFutbol() throws Exception {
        assertArrayEquals(new int[] {3, 0}, futbol.calcularPuntosLiga(2, 1));
        assertArrayEquals(new int[] {1, 1}, futbol.calcularPuntosLiga(1, 1));
    }

    @Test
    @DisplayName("En baloncesto se rechaza el empate")
    public void baloncestoNoPermiteEmpate() throws Exception {
        Baloncesto baloncesto = new Baloncesto("Baloncesto", 5, false);

        assertThrows(Exception.class, () -> baloncesto.calcularPuntosLiga(80, 80));
    }

    @Test
    @DisplayName("Una sola amarilla no genera suspension; dos amarillas o una roja si")
    public void reglasDeTarjetas() throws Exception {
        PartidoInterno partido = new PartidoInterno(proximaSemana, futbol, locales, visitantes);
        Jugador jugador = locales.getJugadores().get(0);

        EstadFutbol unaAmarilla = new EstadFutbol(jugador, partido);
        unaAmarilla.registrarAmarilla();
        assertFalse(unaAmarilla.generaSuspension());

        EstadFutbol dosAmarillas = new EstadFutbol(jugador, partido);
        dosAmarillas.registrarAmarilla();
        dosAmarillas.registrarAmarilla();
        assertTrue(dosAmarillas.generaSuspension());

        EstadFutbol roja = new EstadFutbol(jugador, partido);
        roja.registrarRoja();
        assertTrue(roja.generaSuspension());
    }

    @Test
    @DisplayName("Un jugador con roja en un partido interno queda suspendido (HU-T08)")
    public void rojaSuspendeAlJugador() throws Exception {
        PartidoInterno partido = new PartidoInterno(proximaSemana, futbol, locales, visitantes);
        Jugador jugador = locales.getJugadores().get(0);
        EstadFutbol estadistica = new EstadFutbol(jugador, partido);
        estadistica.registrarRoja();
        partido.registrarEstadistica(estadistica);
        partido.registrarMarcador(1, 0);

        partido.finalizarPartido();

        assertEquals(1, jugador.getSanciones().size());
        assertTrue(jugador.estaSuspendido(null), "Debe quedar suspendido para el siguiente partido interno");
    }
}