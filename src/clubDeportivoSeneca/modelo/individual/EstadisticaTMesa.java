package clubDeportivoSeneca.modelo.individual;

import clubDeportivoSeneca.modelo.usuario.Jugador;

public class EstadisticaTMesa extends EstadisticaIndividual implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

    private int setsGanados;
    private int puntosTotales;

    public EstadisticaTMesa(Jugador jugador, int setsGanados, int puntosTotales) throws Exception {
        super(jugador);
        if (setsGanados < 0 || puntosTotales < 0) {
            throw new Exception("Las estadísticas no pueden ser negativas");
        }
        this.setsGanados = setsGanados;
        this.puntosTotales = puntosTotales;
    }

    public int getSetsGanados() {
        return this.setsGanados;
    }

    public int getPuntosTotales() {
        return this.puntosTotales;
    }

    @Override
    public String getResumen() {
        return "Sets: " + setsGanados + ", puntos totales: " + puntosTotales;
    }
}