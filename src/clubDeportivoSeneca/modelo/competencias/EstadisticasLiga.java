package clubDeportivoSeneca.modelo.competencias;

import java.util.HashMap;
import java.util.Map;

public class EstadisticasLiga implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private Map<String, Integer> puntos;
	private Map<String, Integer> partidosJugados;
	
	public EstadisticasLiga() {
		this.puntos = new HashMap<String, Integer>();
		this.partidosJugados = new HashMap<String, Integer>();
	}

	public Map<String, Integer> getPuntos() {
		return puntos;
	}

	public Map<String, Integer> getPartidosJugados() {
		return partidosJugados;
	}
	
	public void registrarResultado(String nombreEquipo, int puntosGanados) throws Exception {
		
		if(nombreEquipo == null) {
			throw new Exception("El nombre del equipo es obligatorio.");
		}
		
		if(puntos.containsKey(nombreEquipo)) {
			puntos.put(nombreEquipo, puntos.get(nombreEquipo) + puntosGanados);
			
		} else {
			puntos.put(nombreEquipo, puntosGanados);
		}
	}
	
	public int getPuntos(String nombreEquipo) {
		return puntos.get(nombreEquipo);
	}
	
	public int getPartidosJugados(String nombreEquipo) {
		return partidosJugados.get(nombreEquipo);
	}
	
}




