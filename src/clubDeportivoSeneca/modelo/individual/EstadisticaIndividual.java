package clubDeportivoSeneca.modelo.individual;

import clubDeportivoSeneca.modelo.usuario.Jugador;

public abstract class EstadisticaIndividual implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

    private Jugador jugador;

    protected EstadisticaIndividual(Jugador jugador) throws Exception {
        if (jugador == null) {
            throw new Exception("La estadística debe tener un jugador");
        }
        this.jugador = jugador;
    }

    public Jugador getJugador() {
        return this.jugador;
    }

    public abstract String getResumen();
}