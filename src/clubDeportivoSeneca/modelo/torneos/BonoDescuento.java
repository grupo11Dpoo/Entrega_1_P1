package clubDeportivoSeneca.modelo.torneos;

import java.io.Serializable;
import java.time.LocalDate;

@SuppressWarnings("serial")
public class BonoDescuento implements Serializable {
	
	private String torneoOrigen;
    private double porcentaje;
    private LocalDate vigenteHasta;
    private boolean redimido;

    public BonoDescuento(String torneoOrigen, double porcentaje, LocalDate vigenteHasta) throws Exception {
        if (torneoOrigen == null || torneoOrigen.isBlank()) {
        	throw new Exception("Torneo de origen obligatorio.");
        }
        if (porcentaje <= 0 || porcentaje > 1) {
        	throw new Exception("El porcentaje debe estar entre 0 y 1.");
        }
        if (vigenteHasta == null) {
        	throw new Exception("La vigencia es obligatoria.");
        }
        this.torneoOrigen = torneoOrigen;
        this.porcentaje = porcentaje;
        this.vigenteHasta = vigenteHasta;
        this.redimido = false;
    }

    public String getTorneoOrigen() { 
    	return torneoOrigen; 
    }
    
    public double getPorcentaje() { 
    	return porcentaje; 
    }
    
    public LocalDate getVigenteHasta() { 
    	return vigenteHasta; 
    }
    public boolean isRedimido() { 
    	return redimido; 
    	}

    public void redimir() throws Exception {
        
    	LocalDate fechaActual = LocalDate.now();
    	
    	if(fechaActual.isAfter(vigenteHasta)) {
    		throw new Exception("El bono ya no está vigente.");
    	}
    	
    	if(this.isRedimido()) {
    		throw new Exception("El bono ya fue redimido.");
    	}
    	
    	this.redimido = true;
    	
    }

}
