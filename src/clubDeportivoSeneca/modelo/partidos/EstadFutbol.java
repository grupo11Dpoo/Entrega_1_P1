package clubDeportivoSeneca.modelo.partidos;

import clubDeportivoSeneca.modelo.usuario.Jugador;

public class EstadFutbol extends EstadisticaJugadorPartido {
	
	private int goles;
	private int asistencias;
	private int amarillas;
	private int rojas;
	
	public EstadFutbol(Jugador jugador, PartidoConjuntos partido) throws Exception {
		super(jugador, partido);
		
		this.goles = 0;
		this.asistencias = 0;
		this.amarillas = 0;
		this.rojas = 0;
	}

	public int getGoles() {
		return goles;
	}

	public int getAsistencias() {
		return asistencias;
	}

	public int getAmarillas() {
		return amarillas;
	}

	public int getRojas() {
		return rojas;
	}
	
	public void registrarGol() {
		this.goles += 1;
	}
	
	public void registrarAsistencia() {
		this.asistencias += 1;
	}
	
	public void registrarAmarilla() {
		this.amarillas += 1;
	}
	
	public void registrarRoja() {
		this.rojas += 1;
	}

	public boolean generaSuspension() {
		
		return amarillas >= 2 || rojas >= 1;
		
	}

	@Override
	public String getResumen() {
		
		return "Resumen de " + getJugador().getNombre() + ": " + goles + " goles, " + asistencias
				+ " asistencias, " + amarillas + " tarjetas amarillas, " + rojas + " tarjetas rojas.";
	}

}
