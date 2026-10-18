package clubDeportivoSeneca.modelo.partidos;

import clubDeportivoSeneca.modelo.usuario.Jugador;

public class EstadVoleibol extends EstadisticaJugadorPartido implements java.io.Serializable {
	
	private int aces;
	private int bloqueos;
	
	public EstadVoleibol(Jugador jugador, PartidoConjuntos partido) throws Exception {
		super(jugador, partido);
		
		this.aces = 0;
		this.bloqueos = 0;
	}

	public int getAces() {
		return aces;
	}

	public int getBloqueos() {
		return bloqueos;
	}
	//TODO:
	public void registrarAs() {
		
		aces ++;
	}
	
	//TODO:
	public void registrarBloqueo() {
		
		bloqueos ++;
	}
	
	@Override
	public boolean generaSuspension() {
		
		return false;
	}
	
	@Override
	public  String getResumen() {
		
		return "Resumen de " + getJugador().getNombre() + ": " + aces + " aces, " + bloqueos + " bloqueos.";
	}

}
