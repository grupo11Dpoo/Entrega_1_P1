package clubDeportivoSeneca.modelo.entrenamiento;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import clubDeportivoSeneca.modelo.equipos.Equipo;
import clubDeportivoSeneca.modelo.instalaciones.Instalacion;
import clubDeportivoSeneca.modelo.usuario.Entrenador;
import clubDeportivoSeneca.modelo.usuario.Jugador;

public class Entrenamiento implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

    private LocalDate fecha;
    private LocalTime hora;
    private int duracionMinutos;
    private Entrenador entrenador;
    private Instalacion instalacion;
    private Equipo equipoConvocado;
    private List<Jugador> convocados;
    private List<Asistencia> asistencias;

    public Entrenamiento(LocalDate fecha, LocalTime hora, int duracionMinutos, Entrenador entrenador, Instalacion instalacion) throws Exception {
        if (fecha == null) {
            throw new Exception("fecha no puede ser null");
        }
        if (hora == null) {
            throw new Exception("hora no puede ser null");
        }
        if (duracionMinutos <= 0) {
            throw new Exception("La duración debe ser mayor a 0 minutos");
        }
        if (entrenador == null) {
            throw new Exception("entrenador no puede ser null");
        }
        this.fecha = fecha;
        this.hora = hora;
        this.duracionMinutos = duracionMinutos;
        this.entrenador = entrenador;
        this.instalacion = instalacion;
        this.convocados = new ArrayList<>();
        this.asistencias = new ArrayList<>();
    }

    public LocalDate getFecha() {
        return this.fecha;
    }

    public LocalTime getHora() {
        return this.hora;
    }

    public int getDuracionMinutos() {
        return this.duracionMinutos;
    }

    public Entrenador getEntrenador() {
        return this.entrenador;
    }

    public Instalacion getInstalacion() {
        return this.instalacion;
    }

    public Equipo getEquipoConvocado() {
        return this.equipoConvocado;
    }

    public List<Jugador> getConvocados() {
        return Collections.unmodifiableList(this.convocados);
    }

    public List<Asistencia> getAsistencias() {
        return Collections.unmodifiableList(this.asistencias);
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public void setDuracionMinutos(int duracionMinutos) throws Exception {
        if (duracionMinutos <= 0) {
            throw new Exception("La duración debe ser mayor a 0 minutos");
        }
        this.duracionMinutos = duracionMinutos;
    }

    public void setInstalacion(Instalacion instalacion) {
        this.instalacion = instalacion;
    }

    public void convocar(Jugador jugador) throws Exception {
        if (jugador == null) {
            throw new Exception("No hay un jugador para convocar");
        }
        if (convocados.contains(jugador)) {
            throw new Exception("El jugador ya está convocado");
        }
        convocados.add(jugador);
    }

    public void convocarEquipo(Equipo equipo) throws Exception {
        if (equipo == null) {
            throw new Exception("No hay un equipo para convocar");
        }
        this.equipoConvocado = equipo;
        for (Jugador jugador : equipo.getJugadores()) {
            if (!convocados.contains(jugador)) {
                convocados.add(jugador);
            }
        }
    }

    public Asistencia registrarAsistencia(Jugador jugador, EstadoAsistencia estado) throws Exception {
        if (!convocados.contains(jugador)) {
            throw new Exception("Jugador no convocado");
        }
        Asistencia existente = getAsistenciaDe(jugador);
        if (existente != null) {
            existente.setEstado(estado);
            return existente;
        }
        Asistencia nueva = new Asistencia(jugador, estado);
        asistencias.add(nueva);
        return nueva;
    }

    public void registrarEvaluacion(Jugador jugador, int nota, String comentario) throws Exception {
        Asistencia asistencia = getAsistenciaDe(jugador);
        if (asistencia == null) {
            throw new Exception("Primero debe registrar la asistencia del jugador");
        }
        if (asistencia.getEstado() == EstadoAsistencia.FALTO) {
            throw new Exception("No se puede evaluar a un jugador que faltó");
        }
        asistencia.setEvaluacion(new EvaluacionDesempeno(nota, comentario));
    }

    public Asistencia getAsistenciaDe(Jugador jugador) {
        for (Asistencia asistencia : asistencias) {
            if (asistencia.getJugador().equals(jugador)) {
                return asistencia;
            }
        }
        return null;
    }

    public double getPorcentajeAsistencia() {
        if (convocados.isEmpty()) {
            return 0;
        }
        int presentes = 0;
        for (Asistencia asistencia : asistencias) {
            if (asistencia.getEstado() != EstadoAsistencia.FALTO) {
                presentes++;
            }
        }
        return presentes * 100.0 / convocados.size();
    }
}