package clubDeportivoSeneca.modelo.individual;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import clubDeportivoSeneca.modelo.deportes.Tenis;
import clubDeportivoSeneca.modelo.usuario.Jugador;

public class PartidoIndividualTest {

    private Tenis tenis;
    private Jugador ana;
    private Jugador juan;

    @BeforeEach
    public void setUp() throws Exception {
        tenis = new Tenis("Tenis", 4);
        ana = new Jugador("ana", "1234", "Ana", LocalDate.of(2000, 1, 1));
        juan = new Jugador("juan", "1234", "Juan", LocalDate.of(2000, 1, 1));
        ana.asignarNivel(tenis, NivelJugador.INTERMEDIO);
        juan.asignarNivel(tenis, NivelJugador.INTERMEDIO);
    }

    @Test
    @DisplayName("Se acepta un partido entre niveles adyacentes")
    public void aceptaNivelesAdyacentes() throws Exception {
        juan.asignarNivel(tenis, NivelJugador.PRINCIPIANTE);

        PartidoIndividual partido = new PartidoIndividual(LocalDate.now(), tenis, ana, juan);

        assertFalse(partido.isFinalizado());
    }

    @Test
    @DisplayName("Rechaza un partido entre Principiante y Avanzado")
    public void rechazaNivelesNoAdyacentes() throws Exception {
        ana.asignarNivel(tenis, NivelJugador.AVANZADO);
        juan.asignarNivel(tenis, NivelJugador.PRINCIPIANTE);

        assertThrows(Exception.class, () -> new PartidoIndividual(LocalDate.now(), tenis, ana, juan));
    }

    @Test
    @DisplayName("Rechaza un partido con un jugador no inscrito en la disciplina")
    public void rechazaJugadorNoInscrito() {
        Jugador pedro = new Jugador("pedro", "1234", "Pedro", LocalDate.of(2000, 1, 1));

        assertThrows(Exception.class, () -> new PartidoIndividual(LocalDate.now(), tenis, ana, pedro));
    }

    @Test
    @DisplayName("Rechaza que un jugador juegue contra si mismo")
    public void rechazaMismoJugador() {
        assertThrows(Exception.class, () -> new PartidoIndividual(LocalDate.now(), tenis, ana, ana));
    }

    @Test
    @DisplayName("Registrar el resultado finaliza el partido y actualiza el ELO de ambos")
    public void registrarResultadoActualizaRankings() throws Exception {
        PartidoIndividual partido = new PartidoIndividual(LocalDate.now(), tenis, ana, juan);
        Ranking rankingAna = new Ranking(ana, tenis);
        Ranking rankingJuan = new Ranking(juan, tenis);

        partido.registrarResultado(ana,
                new EstadisticaTyP(ana, 2, 12, 3, 5),
                new EstadisticaTyP(juan, 0, 7, 1, 9));
        partido.actualizarRankings(rankingAna, rankingJuan);

        assertTrue(partido.isFinalizado());
        assertEquals(ana, partido.getGanador());
        assertEquals(1016, rankingAna.getPuntaje());
        assertEquals(984, rankingJuan.getPuntaje());
    }

    @Test
    @DisplayName("Rechaza registrar un segundo resultado")
    public void rechazaResultadoDuplicado() throws Exception {
        PartidoIndividual partido = new PartidoIndividual(LocalDate.now(), tenis, ana, juan);
        partido.registrarResultado(ana,
                new EstadisticaTyP(ana, 2, 12, 3, 5),
                new EstadisticaTyP(juan, 0, 7, 1, 9));

        assertThrows(Exception.class, () -> partido.registrarResultado(juan,
                new EstadisticaTyP(ana, 0, 7, 1, 9),
                new EstadisticaTyP(juan, 2, 12, 3, 5)));
        assertEquals(ana, partido.getGanador(), "El primer resultado no debe cambiar");
    }

    @Test
    @DisplayName("Rechaza estadisticas de tenis de mesa en un partido de tenis")
    public void rechazaEstadisticaDeOtraDisciplina() throws Exception {
        PartidoIndividual partido = new PartidoIndividual(LocalDate.now(), tenis, ana, juan);

        assertThrows(Exception.class, () -> partido.registrarResultado(ana,
                new EstadisticaTMesa(ana, 3, 50),
                new EstadisticaTMesa(juan, 1, 40)));
        assertFalse(partido.isFinalizado());
    }

    @Test
    @DisplayName("Rechaza actualizar el ranking antes de registrar el resultado")
    public void rechazaRankingSinResultado() throws Exception {
        PartidoIndividual partido = new PartidoIndividual(LocalDate.now(), tenis, ana, juan);

        assertThrows(Exception.class,
                () -> partido.actualizarRankings(new Ranking(ana, tenis), new Ranking(juan, tenis)));
    }
}