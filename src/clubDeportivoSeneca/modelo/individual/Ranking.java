package clubDeportivoSeneca.modelo.individual;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import clubDeportivoSeneca.modelo.deportes.DeporteIndividual;
import clubDeportivoSeneca.modelo.usuario.Jugador;

public class Ranking {

    public static final int FACTOR_K = 32;
    public static final int PUNTAJE_INICIAL = 1000;

    private Jugador jugador;
    private DeporteIndividual disciplina;
    private int puntaje;
    private List<RegistroRanking> historial;

    public Ranking(Jugador jugador, DeporteIndividual disciplina) throws Exception {
        if (jugador == null || disciplina == null) {
            throw new Exception("El ranking debe tener jugador y disciplina");
        }
        this.jugador = jugador;
        this.disciplina = disciplina;
        this.puntaje = PUNTAJE_INICIAL;
        this.historial = new ArrayList<>();
    }

    public Jugador getJugador() {
        return this.jugador;
    }

    public DeporteIndividual getDisciplina() {
        return this.disciplina;
    }

    public int getPuntaje() {
        return this.puntaje;
    }

    public List<RegistroRanking> getHistorial() {
        return Collections.unmodifiableList(this.historial);
    }

    public boolean tienePartidos() {
        return !historial.isEmpty();
    }

    public void actualizarElo(int puntajeRival, boolean gano, LocalDate fecha) throws Exception {
        double esperado = 1.0 / (1.0 + Math.pow(10, (puntajeRival - this.puntaje) / 400.0));
        double resultado = gano ? 1.0 : 0.0;
        this.puntaje = (int) Math.round(this.puntaje + FACTOR_K * (resultado - esperado));
        historial.add(new RegistroRanking(fecha, this.puntaje));
    }
}