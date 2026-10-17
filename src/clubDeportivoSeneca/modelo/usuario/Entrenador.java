package clubDeportivoSeneca.modelo.usuario;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import clubDeportivoSeneca.modelo.entrenamiento.Entrenamiento;

public class Entrenador extends Empleado {
	
	private List<Entrenamiento> entrenamientos;

	public Entrenador(String login, String password, String nombre, String codigoDescuento) {
		super(login, password, nombre, codigoDescuento);
		this.entrenamientos = new ArrayList<Entrenamiento>();
	}

	public List<Entrenamiento> getEntrenamientos() {
		return entrenamientos;
	}
	
	private void verificarEntrenador(Entrenamiento entrenamiento) throws Exception {
		
		if(entrenamiento == null || entrenamiento.getEntrenador() != this) {
			throw new Exception("Solo el entrenador responsable puede registrar datos de esta sesión.");
		}
	}
	
	public Entrenamiento planearEntrenamiento(LocalDate fecha, LocalTime hora, int duracioMinutos, this, Instalacion instalacion) {
		
		Entrenamiento nuevoEntrenamiento = new Entrenamiento(fecha, hora, duracionMinutos, this, instalacion);
		
		entrenamientos.add(nuevoEntrenamiento);
		
		return nuevoEntrenamiento;
	}
	
	public void registrarAsistencia(Entrenamiento entrenamiento, Jugador jugador, EstadoAsistencia estado) {
		
		verificarEntrenador(entrenamiento);
		entrenamiento.registrarAsistencia(jugador, estado);
	}
	
	public void registrarEvaluacion(Entrenamiento entrenamiento, Jugador jugador, int nota, String comentario) {
		
		verificarEntrenador(entrenamiento);
		entrenamiento.registrarEvaluacion(jugador, nota, comentario);
	}

}
