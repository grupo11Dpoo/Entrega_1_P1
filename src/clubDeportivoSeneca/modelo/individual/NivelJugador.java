package clubDeportivoSeneca.modelo.individual;

public enum NivelJugador {

    PRINCIPIANTE, INTERMEDIO, AVANZADO;

    public boolean esAdyacenteA(NivelJugador otro) {
        return Math.abs(this.ordinal() - otro.ordinal()) <= 1;
    }
}