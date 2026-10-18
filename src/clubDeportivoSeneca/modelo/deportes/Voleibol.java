package clubDeportivoSeneca.modelo.deportes;

import clubDeportivoSeneca.modelo.partidos.EstadVoleibol;
import clubDeportivoSeneca.modelo.partidos.EstadisticaJugadorPartido;
import clubDeportivoSeneca.modelo.partidos.PartidoConjuntos;
import clubDeportivoSeneca.modelo.usuario.Jugador;

public class Voleibol extends DeporteDeConjunto implements java.io.Serializable {
	
	public static final int PUNTOS_VICTORIA = 2;
	
	public Voleibol(String nombre, int minJugadoresHabilitados, boolean permiteEmpate) throws Exception {
		super("Voleibol", 6, false);
	}
	
	@Override
	public int[] calcularPuntosLiga(int setsA, int setsB) throws Exception {
		
		validarEmpate(setsA, setsB);
		
		if(setsA > setsB) {
			return new int[] {PUNTOS_VICTORIA, 0};
			
		} else {
			return new int[] {0, PUNTOS_VICTORIA};
		}
	}
	
	@Override
	public EstadisticaJugadorPartido crearEstadistica(Jugador jugador, PartidoConjuntos partido) throws Exception {
		
		return new EstadVoleibol(jugador, partido);
	}

}
