package clubDeportivoSeneca.modelo.competencias;

public class FilaTabla {
	
	 private final String equipo;
	    private final int puntos;
	    private final int partidosJugados;
	 
	    public FilaTabla(String equipo, int puntos, int partidosJugados) throws Exception {
	        if (equipo == null || equipo.isBlank()) {
	        	throw new Exception("El nombre del equipo es obligatorio");
	        }
	        
	        if (puntos < 0 || partidosJugados < 0) {
	        	throw new Exception("Puntos y partidos no pueden ser negativos");
	        }
	        
	        this.equipo = equipo;
	        this.puntos = puntos;
	        this.partidosJugados = partidosJugados;
	    }
	 
	    public String getEquipo() { 
	    	return equipo; 
	    }
	    
	    public int getPuntos() { 
	    	return puntos; 
	    }
	    
	    public int getPartidosJugados() { 
	    	return partidosJugados; 
	    }
	 
	    @Override
	    public String toString() {
	        return equipo + ": " + puntos + " pts, " + partidosJugados + " PJ";
	    }

}
