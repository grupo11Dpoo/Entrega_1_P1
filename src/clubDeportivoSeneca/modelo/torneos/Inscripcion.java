package clubDeportivoSeneca.modelo.torneos;
 
import java.util.ArrayList;
import java.util.List;
 
import clubDeportivoSeneca.modelo.equipos.Equipo;
import clubDeportivoSeneca.modelo.usuario.Jugador;

public class Inscripcion implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
 
    private Equipo equipo;            
    private List<Jugador> jugadores;    
    private boolean cupoPreferencial;           
 

    public Inscripcion(Equipo equipo, boolean cupoPreferencial) throws Exception {
        if (equipo == null) {
        	throw new Exception("El equipo es obligatorio.");
        }
        
        this.equipo = equipo;
        this.jugadores = new ArrayList<Jugador>();
        this.cupoPreferencial = cupoPreferencial;
    }
 

    public Inscripcion(List<Jugador> jugadores, boolean cupoPreferencial) throws Exception {
        
    	if (jugadores == null || jugadores.isEmpty() || jugadores.size() > 2) {
        	
        	throw new Exception("Se inscribe un jugador o una pareja.");
        }
         
        this.equipo = null;
        this.jugadores = new ArrayList<Jugador>(jugadores);
        this.cupoPreferencial = cupoPreferencial;
    }
 
    public Equipo getEquipo() { 
    	return equipo; 
    }
    
    public List<Jugador> getJugadores() { 
    	return jugadores;
    }
    
    public boolean isCupoPreferencial() { 
    	return cupoPreferencial; 
    }
    
    public void setCupoPreferencial(boolean cupoPreferencial) { 
    	this.cupoPreferencial = cupoPreferencial; 
    }
    
    public boolean esEquipo() { 
    	return equipo != null; 
    }
 

    public String descripcion() {
        if (esEquipo()) {
            return equipo.getNombre();
        }
        
        String texto = "";
        for (Jugador j : jugadores) {
            if (!texto.isEmpty()) {
                texto += " / ";
            }
            texto += j.getNombre();
        }
        return texto;
    }
 
}
    
    
    
    
    
    
    
    
    
    
    
    

