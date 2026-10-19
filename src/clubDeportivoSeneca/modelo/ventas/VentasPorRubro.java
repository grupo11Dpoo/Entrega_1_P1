package clubDeportivoSeneca.modelo.ventas;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VentasPorRubro implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	
	private final List<VentaServiciosAdicionales> ventas;
	
	public VentasPorRubro(List<VentaServiciosAdicionales> ventas) {
        if (ventas == null) {
            throw new IllegalArgumentException("La lista de ventas no puede ser nula.");
        }
        this.ventas = new ArrayList<>(ventas);
    }
 
    public List<VentaServiciosAdicionales> getVentas() {
        return Collections.unmodifiableList(new ArrayList<>(ventas));
    }
    
    public List<VentaServiciosAdicionales> generar(Rubro rubro, Periodo periodo, LocalDate referencia) {
        if (rubro == null || periodo == null || referencia == null) {
            throw new IllegalArgumentException("Rubro, periodo y fecha de referencia son obligatorios.");
        }
        List<VentaServiciosAdicionales> resultado = new ArrayList<>();
        for (VentaServiciosAdicionales venta : ventas) {
            if (venta.isFinalizada() && venta.getRubro() == rubro
                    && periodo.contiene(referencia, venta.getFecha())) {
                resultado.add(venta);
            }
        }
        return Collections.unmodifiableList(resultado);
    }
    public int totalPorRubro(Rubro rubro, Periodo periodo, LocalDate referencia) {
        int total = 0;
        for (VentaServiciosAdicionales venta : generar(rubro, periodo, referencia)) {
            total += venta.getPrecioTotal();
        }
        return total;
    }
}
