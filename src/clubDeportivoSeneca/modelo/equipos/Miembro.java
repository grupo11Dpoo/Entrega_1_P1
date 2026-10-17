package clubDeportivoSeneca.modelo.equipos;

import clubDeportivoSeneca.modelo.usuario.Jugador;

public class Miembro {
	
	private Jugador jugador;
	private Equipo equipo;
	private String posicion;
	private int numeroCamiseta;
	
	public Miembro(Jugador jugador, Equipo equipo, String posicion, int numeroCamiseta) {

		this.jugador = jugador;
		this.equipo = equipo;
		this.posicion = posicion;
		this.numeroCamiseta = numeroCamiseta;
	}

	public String getPosicion() {
		return posicion;
	}

	public void setPosicion(String posicion) {
		this.posicion = posicion;
	}

	public int getNumeroCamiseta() {
		return numeroCamiseta;
	}

	public void setNumeroCamiseta(int numeroCamiseta) {
		this.numeroCamiseta = numeroCamiseta;
	}

	public Jugador getJugador() {
		return jugador;
	}

	public Equipo getEquipo() {
		return equipo;
	}
	
	

}
