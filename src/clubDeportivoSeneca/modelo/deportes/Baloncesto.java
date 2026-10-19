package clubDeportivoSeneca.modelo.deportes;

import clubDeportivoSeneca.modelo.partidos.EstadBaloncesto;
import clubDeportivoSeneca.modelo.partidos.EstadisticaJugadorPartido;
import clubDeportivoSeneca.modelo.partidos.PartidoConjuntos;
import clubDeportivoSeneca.modelo.usuario.Jugador;

public class Baloncesto extends DeporteDeConjunto implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	
	public static final int PUNTOS_VICTORIA = 2;
	
	public Baloncesto(String nombre, int minJugadoresHabilitados, boolean permiteEmpate) throws Exception{
		super("Baloncesto", 5, false);
	}
	
	@Override
	public int[] calcularPuntosLiga(int marcadorA, int marcadorB) throws Exception {
		
		validarEmpate(marcadorA, marcadorB);
		
		if(marcadorA > marcadorB) {
			return new int[] {PUNTOS_VICTORIA, 0};
			
		} else {
			return new int[] {0, PUNTOS_VICTORIA};
		}
	}
	
	@Override
	public EstadisticaJugadorPartido crearEstadistica(Jugador jugador, PartidoConjuntos partido) throws Exception {
		
		return new EstadBaloncesto(jugador, partido);
	}

}
