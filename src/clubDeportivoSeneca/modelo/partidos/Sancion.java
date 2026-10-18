package clubDeportivoSeneca.modelo.partidos;

import clubDeportivoSeneca.modelo.competencias.Circuito;
import clubDeportivoSeneca.modelo.usuario.Jugador;

public class Sancion implements java.io.Serializable {
	
	private Jugador jugador;
	private PartidoConjuntos partidoOrigen;
	private Circuito circuito;
	private String motivo;
	private boolean cumplida;
	
	public Sancion(Jugador jugador, PartidoConjuntos partidoOrigen, Circuito circuito, String motivo) {

		this.jugador = jugador;
		this.partidoOrigen = partidoOrigen;
		this.circuito = circuito;
		this.motivo = motivo;
		this.cumplida = false;
	}

	public Jugador getJugador() {
		return jugador;
	}

	public PartidoConjuntos getPartidoOrigen() {
		return partidoOrigen;
	}

	public Circuito getCircuito() {
		return circuito;
	}

	public String getMotivo() {
		return motivo;
	}

	public boolean isCumplida() {
		return cumplida;
	}
	
	public void cumplir() {
		this.cumplida = true;
	}
	
	public boolean estaVigente() {
		return !cumplida;
	}
	
	public boolean aplicaCircuito(Circuito otroCircuito) {
		if(otroCircuito.equals(this.circuito)) {
			return true;
		} else {
			return false;
		}
	}

}
