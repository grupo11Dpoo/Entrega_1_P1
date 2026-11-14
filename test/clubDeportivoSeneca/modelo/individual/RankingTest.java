package clubDeportivoSeneca.modelo.individual;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import clubDeportivoSeneca.modelo.deportes.Tenis;
import clubDeportivoSeneca.modelo.usuario.Jugador;


public class RankingTest {

    private Tenis tenis;
    private Ranking ranking;

    @BeforeEach
    public void setUp() throws Exception {
        tenis = new Tenis("Tenis", 4);
        Jugador ana = new Jugador("ana", "1234", "Ana", LocalDate.of(2000, 1, 1));
        ranking = new Ranking(ana, tenis);
    }

    @Test
    @DisplayName("Un ranking nuevo empieza en 1000 y sin partidos")
    public void puntajeInicial() {
        assertEquals(1000, ranking.getPuntaje());
        assertFalse(ranking.tienePartidos());
    }

    @Test
    @DisplayName("Ganar contra un rival del mismo puntaje suma 16 puntos")
    public void ganarContraIgualSuma16() throws Exception {
        ranking.actualizarElo(1000, true, LocalDate.now());

        assertEquals(1016, ranking.getPuntaje());
        assertTrue(ranking.tienePartidos());
        assertEquals(1, ranking.getHistorial().size());
    }

    @Test
    @DisplayName("Perder contra un rival del mismo puntaje resta 16 puntos")
    public void perderContraIgualResta16() throws Exception {
        ranking.actualizarElo(1000, false, LocalDate.now());

        assertEquals(984, ranking.getPuntaje());
    }

    @Test
    @DisplayName("Ganarle a un rival mas fuerte suma mas que ganarle a uno igual")
    public void ganarContraMasFuerteSumaMas() throws Exception {
        ranking.actualizarElo(1200, true, LocalDate.now());

        assertTrue(ranking.getPuntaje() > 1016, "Debe sumar mas de 16 puntos");
    }

    @Test
    @DisplayName("Rechaza crear un ranking sin jugador")
    public void rechazaRankingSinJugador() {
        assertThrows(Exception.class, () -> new Ranking(null, tenis));
    }
}