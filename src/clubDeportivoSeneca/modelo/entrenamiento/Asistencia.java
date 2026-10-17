package clubDeportivoSeneca.modelo.entrenamiento;

import clubDeportivoSeneca.modelo.usuario.Jugador;

public class Asistencia {

    private Jugador jugador;
    private EstadoAsistencia estado;
    private EvaluacionDesempeno evaluacion;

    public Asistencia(Jugador jugador, EstadoAsistencia estado) throws Exception {
        if (jugador == null) {
            throw new Exception("jugador no puede ser null");
        }
        if (estado == null) {
            throw new Exception("estado no puede ser null");
        }
        this.jugador = jugador;
        this.estado = estado;
    }

    public Jugador getJugador() {
        return this.jugador;
    }

    public EstadoAsistencia getEstado() {
        return this.estado;
    }

    public EvaluacionDesempeno getEvaluacion() {
        return this.evaluacion;
    }

    public void setEstado(EstadoAsistencia estado) {
        this.estado = estado;
    }

    public void setEvaluacion(EvaluacionDesempeno evaluacion) {
        this.evaluacion = evaluacion;
    }

    public boolean tieneEvaluacion() {
        return this.evaluacion != null;
    }
}