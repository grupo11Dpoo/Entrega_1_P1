package clubDeportivoSeneca.modelo.partidos;

import clubDeportivoSeneca.modelo.usuario.Jugador;

public class EstadBaloncesto extends EstadisticaJugadorPartido implements java.io.Serializable {
	
	private int puntos;
	private int rebotes;
	
	
	public EstadBaloncesto(Jugador jugador, PartidoConjuntos partido) throws Exception {
		super(jugador, partido);
		
		this.puntos = 0;
		this.rebotes = 0;
	}


	public int getPuntos() {
		return puntos;
	}


	public int getRebotes() {
		return rebotes;
	}

	public void registrarPuntos(int cantidad) throws Exception {
		
		if(cantidad < 0) {
			throw new Exception("La cantidad de puntos no puede ser negativa.");
		}
		
		puntos += cantidad;
		
	}
	
	public void registrarRebote() {
		rebotes ++;
	}

	@Override
	public boolean generaSuspension() {
		return false;
	}
	
	@Override
	public String getResumen() {
		
		return "Resumen de " + getJugador().getNombre() + ": " + puntos + " puntos, " + rebotes + " rebotes.";
	}

}
