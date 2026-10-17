package clubDeportivoSeneca.modelo.ventas;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VentaPorFecha {
	
	private List<VentaServiciosAdicionales> ventas;
	 
    public void VentasPorFecha(List<VentaServiciosAdicionales> ventas) {
        if (ventas == null) {
            throw new IllegalArgumentException("La lista de ventas no puede ser nula.");
        }
        this.ventas = new ArrayList<>(ventas);
    }

    public List<VentaServiciosAdicionales> getVentas() {
        return Collections.unmodifiableList(new ArrayList<>(ventas));
    }
    
    public List<VentaServiciosAdicionales> generar(Periodo periodo, LocalDate referencia) {
        if (periodo == null || referencia == null) {
            throw new IllegalArgumentException("El periodo y la fecha de referencia son obligatorios.");
        }
        List<VentaServiciosAdicionales> resultado = new ArrayList<>();
        for (VentaServiciosAdicionales venta : ventas) {
            if (venta.isFinalizada() && periodo.contiene(referencia, venta.getFecha())) {
                resultado.add(venta);
            }
        }
        return Collections.unmodifiableList(resultado);
    }
    
    public int totalPorFecha(LocalDate fecha) {
        int total = 0;
        for (VentaServiciosAdicionales venta : generar(Periodo.DIARIO, fecha)) {
            total += venta.getPrecioTotal();
        }
        return total;
    }

}
