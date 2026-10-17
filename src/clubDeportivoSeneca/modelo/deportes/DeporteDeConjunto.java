package clubDeportivoSeneca.modelo.deportes;

import clubDeportivoSeneca.modelo.partidos.EstadisticaJugadorPartido;
import clubDeportivoSeneca.modelo.partidos.PartidoConjuntos;
import clubDeportivoSeneca.modelo.usuario.Jugador;

public abstract class DeporteDeConjunto extends Deporte {
	
	private int minJugadoresHabilitados;
	private boolean permiteEmpate;
	
	public DeporteDeConjunto(String nombre, int minJugadoresHabilitados, boolean permiteEmpate) throws Exception {
		super(nombre);
		this.minJugadoresHabilitados = minJugadoresHabilitados;
		this.permiteEmpate = permiteEmpate;
	}
	
	public int getMinJugadoresHabilitados() {
		return minJugadoresHabilitados;
	}

	public boolean isPermiteEmpate() {
		return permiteEmpate;
	}
	
	public abstract int[] calcularPuntosLiga(int marcadorA, int marcadorB) throws Exception;
	
	public abstract EstadisticaJugadorPartido crearEstadistica(Jugador jugador, PartidoConjuntos partido) throws Exception;
	
	protected void validarEmpate(int marcadorA, int marcadorB) throws Exception {
		
		if(!permiteEmpate && marcadorA == marcadorB) {
			throw new Exception("Este deporte (" + getNombre() + ") no permite empates.");
		}
	}
}
