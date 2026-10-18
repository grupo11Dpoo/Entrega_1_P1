package clubDeportivoSeneca.modelo.partidos;

import clubDeportivoSeneca.modelo.usuario.Jugador;

public abstract class EstadisticaJugadorPartido implements java.io.Serializable {
	
	private Jugador jugador;
	private PartidoConjuntos partido;
	
	public EstadisticaJugadorPartido(Jugador jugador, PartidoConjuntos partido) throws Exception {

		if(jugador == null || partido == null) {
			throw new Exception("Jugador y partido son obligatorios.");
		}
		
		this.jugador = jugador;
		this.partido = partido;
	}
	public Jugador getJugador() {
		return jugador;
	}
	public PartidoConjuntos getPartido() {
		return partido;
	}
	
	public abstract boolean generaSuspension();
	
	public abstract String getResumen();

}
