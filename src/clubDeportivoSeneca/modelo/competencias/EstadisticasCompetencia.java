package clubDeportivoSeneca.modelo.competencias;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import clubDeportivoSeneca.modelo.partidos.EstadBaloncesto;
import clubDeportivoSeneca.modelo.partidos.EstadFutbol;
import clubDeportivoSeneca.modelo.partidos.EstadVoleibol;
import clubDeportivoSeneca.modelo.partidos.EstadisticaJugadorPartido;
import clubDeportivoSeneca.modelo.usuario.Jugador;

public class EstadisticasCompetencia implements java.io.Serializable {
	
	private static final int PESO_GOL = 3;
	private static final int PESO_ASISTENCIA = 2;
	private static final int PESO_AMARILLA = 1;
	private static final int PESO_ROJA = 3;
	
	private Map<Jugador, List<EstadisticaJugadorPartido>> estadisticasAcumuladas;
	private Map<Jugador, Integer> amarillasConsumidas;
	
	public EstadisticasCompetencia() {
		
		this.estadisticasAcumuladas = new HashMap<Jugador, List<EstadisticaJugadorPartido>>();	
		this.amarillasConsumidas = new HashMap<Jugador, Integer>();
	}

	public Map<Jugador, List<EstadisticaJugadorPartido>> getEstadisticasAcumuladas() {
		return estadisticasAcumuladas;
	}
	
	public void acumularEstadistica(EstadisticaJugadorPartido estadisticaJugador) throws Exception {
		
		if(estadisticaJugador == null) {
			throw new Exception("Estadística nula.");
		}
		
		if(!estadisticasAcumuladas.containsKey(estadisticaJugador.getJugador())) {
			
			List<EstadisticaJugadorPartido> nuevaLista = new ArrayList<EstadisticaJugadorPartido>();
			nuevaLista.add(estadisticaJugador);
			estadisticasAcumuladas.put(estadisticaJugador.getJugador(), nuevaLista);
			
		} else {
			
			estadisticasAcumuladas.get(estadisticaJugador.getJugador()).add(estadisticaJugador);
		}
	}
	
	public List<EstadisticaJugadorPartido> getEstadisticasAcumuladas(Jugador jugador) {
		
		return estadisticasAcumuladas.get(jugador);
	}
	
	public int getAmarillasAcumuladas(Jugador jugador) {
		
		int total = 0;
		
		for(EstadisticaJugadorPartido estadistica : getEstadisticasAcumuladas(jugador)) {
			if(estadistica instanceof EstadFutbol estadFutbol) {
				total += estadFutbol.getAmarillas();
			}
		}
		
		return total;
	}
	
    public int getGolesAcumulados(Jugador jugador) {
		
		int total = 0;
		
		for(EstadisticaJugadorPartido estadistica : getEstadisticasAcumuladas(jugador)) {
			if(estadistica instanceof EstadFutbol estadFutbol) {
				total += estadFutbol.getGoles();
			}
		}
		
		return total;
	}
    
    public List<Jugador> getClasificacionGoleadores() {
    	
    	List<Jugador> lista = new ArrayList<Jugador>();
    	
    	for(Jugador jugador : estadisticasAcumuladas.keySet()) {
    		
    		if(getGolesAcumulados(jugador) > 0) {
    			lista.add(jugador);
    		}
    	}
    	
    	lista.sort(Comparator.comparingInt(this :: getGolesAcumulados).reversed());
    	
    	return lista;
    }
	
	public Jugador getMaxGoleador() throws Exception {
		
		List<Jugador> lista = getClasificacionGoleadores();
		
		if(lista.isEmpty()) {
			throw new Exception("No hay goleadores.");
		}
		
		return lista.get(0);
	}
	
	public Jugador getJugadorDestacado() {
		
		Jugador mejorJugador = null;
		
		int mejorPuntaje = 0;
		
		for(Jugador jugador : estadisticasAcumuladas.keySet()) {
			
			int puntaje = puntajeDestacado(jugador);
			
			if(puntaje > mejorPuntaje) {
				mejorPuntaje = puntaje;
				mejorJugador = jugador;
			}
		}
		
		return mejorJugador;
	}
	
	public int puntajeDestacado(Jugador jugador) {
		
		int total = 0;
		
		for(EstadisticaJugadorPartido estadistica : getEstadisticasAcumuladas(jugador)) {
			if(estadistica instanceof EstadFutbol estadFutbol) {
				total += estadFutbol.getGoles() * PESO_GOL + estadFutbol.getAsistencias() * PESO_ASISTENCIA - 
						estadFutbol.getAmarillas() * PESO_AMARILLA - estadFutbol.getRojas() * PESO_ROJA;
				
			} else if(estadistica instanceof EstadBaloncesto estadBal) {
				total += estadBal.getPuntos() + estadBal.getRebotes();
				
			} else if(estadistica instanceof EstadVoleibol estadVol) {
				total += estadVol.getAces() + estadVol.getBloqueos();
			}
		}
		
		return total;
	}
	
	public boolean generaSuspension(EstadisticaJugadorPartido estadistica) {
		
		if(!(estadistica instanceof EstadFutbol estadFutbol)) {
			return estadistica.generaSuspension();
		}
		
		Jugador jugador = estadFutbol.getJugador();
		int total = getAmarillasAcumuladas(jugador);
		int pendientes = total - amarillasConsumidas.get(jugador);
		boolean seSuspende = estadFutbol.getRojas() >= 1 || pendientes >= 2;
		
		if(seSuspende) {
			amarillasConsumidas.put(jugador, total);
		}
		
		return seSuspende;
	}

}
