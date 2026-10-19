package clubDeportivoSeneca.modelo.deportes;

import clubDeportivoSeneca.modelo.partidos.EstadFutbol;
import clubDeportivoSeneca.modelo.partidos.EstadisticaJugadorPartido;
import clubDeportivoSeneca.modelo.partidos.PartidoConjuntos;
import clubDeportivoSeneca.modelo.usuario.Jugador;

public class Futbol extends DeporteDeConjunto implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final int PUNTOS_VICTORIA = 3;
	public static final int PUNTOS_EMPATE = 1;

	public Futbol(String nombre, int minJugadoresHabilitados, boolean permiteEmpate) throws Exception {
		super("Fútbol", 7, true);
	}
	
	@Override
	public int[] calcularPuntosLiga(int marcadorA, int marcadorB) throws Exception {
		
		validarEmpate(marcadorA, marcadorB);
		
		if(marcadorA > marcadorB) {
			return new int[] {PUNTOS_VICTORIA, 0};
		
		} else if(marcadorA < marcadorB) {
			return new int[] {0, PUNTOS_VICTORIA};
			
		} else {
			return new int[] {PUNTOS_EMPATE, PUNTOS_EMPATE};
		}
		
	}
	
	@Override
	public EstadisticaJugadorPartido crearEstadistica(Jugador jugador, PartidoConjuntos partido) throws Exception {
		
		return new EstadFutbol(jugador, partido);
	}

}
