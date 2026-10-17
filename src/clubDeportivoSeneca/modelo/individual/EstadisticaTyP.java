package clubDeportivoSeneca.modelo.individual;

import clubDeportivoSeneca.modelo.usuario.Jugador;

public class EstadisticaTyP extends EstadisticaIndividual {

    private int setsGanados;
    private int gamesGanados;
    private int aces;
    private int erroresNoForzados;

    public EstadisticaTyP(Jugador jugador, int setsGanados, int gamesGanados, int aces, int erroresNoForzados) throws Exception {
        super(jugador);
        if (setsGanados < 0 || gamesGanados < 0 || aces < 0 || erroresNoForzados < 0) {
            throw new Exception("Las estadísticas no pueden ser negativas");
        }
        this.setsGanados = setsGanados;
        this.gamesGanados = gamesGanados;
        this.aces = aces;
        this.erroresNoForzados = erroresNoForzados;
    }

    public int getSetsGanados() {
        return this.setsGanados;
    }

    public int getGamesGanados() {
        return this.gamesGanados;
    }

    public int getAces() {
        return this.aces;
    }

    public int getErroresNoForzados() {
        return this.erroresNoForzados;
    }

    @Override
    public String getResumen() {
        return "Sets: " + setsGanados + ", games: " + gamesGanados + ", aces: " + aces
                + ", errores no forzados: " + erroresNoForzados;
    }
}