package clubDeportivoSeneca.modelo.torneos;
 
import java.io.Serializable;
import java.time.LocalDateTime;
 
@SuppressWarnings("serial")
public class Encuentro implements Serializable {
 
    private Inscripcion local;
    private Inscripcion visitante;
    private LocalDateTime fecha;
    private Inscripcion ganador;
    private String resultado;
 
    public Encuentro(Inscripcion local, Inscripcion visitante, LocalDateTime fecha) throws Exception {
        
    	if (local == null || visitante == null || fecha == null) {
            throw new Exception("Local, visitante y fecha son obligatorios.");
        }
        
        this.local = local;
        this.visitante = visitante;
        this.fecha = fecha;
    }
 
    public Inscripcion getLocal() { 
    	return local; 
    }
    
    public Inscripcion getVisitante() { 
    	return visitante; 
    }
    
    public LocalDateTime getFecha() { 
    	return fecha; 
    }
    
    public Inscripcion getGanador() { 
    	return ganador; 
    }
    
    public String getResultado() { 
    	return resultado; 
    }
    
    public boolean estaJugado() { 
    	return ganador != null; 
    }
 

    public void registrarResultado(Inscripcion ganador, String resultado) throws Exception {
       
    	if (estaJugado()) {
            throw new Exception("El encuentro ya tiene resultado registrado.");
        }
    	
        if (ganador == null || (ganador != local && ganador != visitante)) {
            throw new Exception("El ganador debe ser el local o el visitante del encuentro.");
        }
        
        if (resultado == null || resultado.isBlank()) {
            throw new Exception("El resultado es obligatorio.");
        }
        
        this.ganador = ganador;
        this.resultado = resultado;
    }
 
}
