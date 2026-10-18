package clubDeportivoSeneca.modelo.individual;

import clubDeportivoSeneca.modelo.deportes.DeporteIndividual;
import clubDeportivoSeneca.modelo.usuario.Jugador;

public class NivelDeporteIndividual implements java.io.Serializable {

    private Jugador jugador;
    private DeporteIndividual deporte;
    private NivelJugador nivel;

    public NivelDeporteIndividual(Jugador jugador, DeporteIndividual deporte, NivelJugador nivel) throws Exception {
        if (jugador == null || deporte == null || nivel == null) {
            throw new Exception("El nivel debe tener jugador, deporte y nivel");
        }
        this.jugador = jugador;
        this.deporte = deporte;
        this.nivel = nivel;
    }

    public Jugador getJugador() {
        return this.jugador;
    }

    public DeporteIndividual getDeporte() {
        return this.deporte;
    }

    public NivelJugador getNivel() {
        return this.nivel;
    }

    public void setNivel(NivelJugador nivel) {
        this.nivel = nivel;
    }

    public boolean esCompatibleCon(NivelDeporteIndividual otro) {
        return this.deporte.equals(otro.getDeporte()) && this.nivel.esAdyacenteA(otro.getNivel());
    }
}