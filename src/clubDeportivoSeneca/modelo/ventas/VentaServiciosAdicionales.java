package clubDeportivoSeneca.modelo.ventas;

import java.time.LocalDate;

import clubDeportivoSeneca.modelo.torneos.BonoDescuento;
import clubDeportivoSeneca.modelo.usuario.Comprador;
import clubDeportivoSeneca.modelo.usuario.Empleado;
import clubDeportivoSeneca.modelo.usuario.Jugador;

public abstract class VentaServiciosAdicionales implements java.io.Serializable{
	private static final long serialVersionUID = 1L;	
	public static final int PORCENTAJE_PUNTOS = 2;
	
	private final LocalDate fecha;
	private final Comprador comprador;
	private final Rubro rubro;
	private int puntosRedimidos;
	private int precioTotal;
	private boolean finalizada;
	private BonoDescuento bono;
	
	protected VentaServiciosAdicionales(LocalDate fecha, Comprador comprador, Rubro rubro) {
		if (fecha == null) {
			throw new IllegalArgumentException("La fecha de la venta es obligatoria.");
		}
		if (comprador == null) {
            throw new IllegalArgumentException("La venta debe tener un comprador.");
        }
        if (rubro == null) {
            throw new IllegalArgumentException("La venta debe tener un rubro.");
        }
        this.fecha = fecha;
        this.comprador = comprador;
        this.rubro = rubro;
        this.puntosRedimidos = 0;
        this.precioTotal = 0;
        this.finalizada = false;
        this.bono = null;
	}
	
	public LocalDate getFecha() {
		return fecha;
	}
	
	public Comprador getComprador () {
		return comprador;
	}
	
	public Rubro getRubro() {
		return rubro;
	}
	
	public int getPuntosRedimidos() {
		return puntosRedimidos;
	}
	
	public int getPrecioTotal() {
		return precioTotal;
	}
	
	public boolean isFinalizada() {
		return finalizada;
	}
	
	public BonoDescuento getBono() {
		return bono;
	}
	
	public abstract int calcularSubtotal();
	
	protected abstract int calcularTotalSinPuntos();
	
	 public abstract int calcularTotal();
	
	public void aplicarPuntos(int puntos) {
		validarNoFinalizada();
		
		// para proyecto2
		if (bono != null && puntos > 0) {
			throw new IllegalStateException("No se pueden redimir puntos en una venta con bono.");
		}
		
		if (puntos < 0) {
            throw new IllegalArgumentException("Los puntos no pueden ser negativos.");
        }
        if (puntos > comprador.getPuntosFidelidad()) {
            throw new IllegalStateException("Puntos insuficientes: el saldo es "
                    + comprador.getPuntosFidelidad() + " y se piden " + puntos + ".");
        }
        if (puntos > calcularTotalSinPuntos()) {
            throw new IllegalArgumentException("No se pueden redimir más puntos que el total de la venta.");
        }
        this.puntosRedimidos = puntos;
	}
	
	public int generarPuntosFidelidad () {
		return calcularTotal() * PORCENTAJE_PUNTOS / 100;
	}
	
	public void finalizar() {
        validarCierre();
        ejecutarCierre();
    }
 
    protected void validarNoFinalizada() {
        if (finalizada) {
            throw new IllegalStateException("La venta ya fue finalizada.");
        }
    }
 
    protected void validarCierre() {
        validarNoFinalizada();
        if (calcularSubtotal() <= 0) {
            throw new IllegalStateException("La venta no tiene productos.");
        }
        if (puntosRedimidos > comprador.getPuntosFidelidad()) {
            throw new IllegalStateException("El comprador ya no tiene los puntos que quiere redimir.");
        }
        if (puntosRedimidos > calcularTotalSinPuntos()) {
            throw new IllegalStateException("Los puntos redimidos superan el total de la venta.");
        }
        
        //proyecto2
        if(bono != null) {
            validarBono(bono);
        }
    }
    
    protected void ejecutarCierre() {
        int total = calcularTotal();
        int puntosGanados = total * PORCENTAJE_PUNTOS / 100;
        if (puntosRedimidos > 0) {
        	try {
                comprador.redimirPuntos(puntosRedimidos);
            } catch (Exception e) {
                throw new IllegalStateException("No fue posible redimir los puntos de fidelidad.", e);
            }
        }
        
        //proyecto2
        if (bono != null) {
            try {
                bono.redimir();
            } catch (Exception e) {
                throw new IllegalStateException("No fue posible redimir el bono.", e);
            }
        }
        //
        
        this.precioTotal = total;
        this.finalizada = true;
        if (puntosGanados > 0) {
            comprador.acumularPuntos(puntosGanados);
        }
    }
    
    //cambio para proyecto2
    protected Jugador obtenerJugador() {
        Comprador comprador = getComprador();
        if (comprador instanceof Jugador) {
            return (Jugador) comprador;
        }
        if (comprador instanceof Empleado) {
            return ((Empleado) comprador).getPerfilJugador();
        }
        return null;
    }
    
    //nuevas funcipnes proyecto2
    protected boolean hayDescuentoDeEmpleado() {
		return false;
	}
    
    private void validarBono(BonoDescuento bono) {
		Jugador jugador = obtenerJugador();
		if (jugador == null || !jugador.getBonos().contains(bono)) {
			throw new IllegalStateException("El bono no pertenece al comprador.");
		}
		if (bono.isRedimido()) {
			throw new IllegalStateException("El bono ya fue redimido.");
		}
		if (!bono.estaVigente()) {
			throw new IllegalStateException("El bono ya no está vigente.");
		}
	}
    
    public void aplicarBono(BonoDescuento bono) {
		validarNoFinalizada();
		if (bono == null) {
			throw new IllegalArgumentException("El bono no puede ser nulo.");
		}
		if (puntosRedimidos > 0) {
			throw new IllegalStateException("El bono no se combina con la redención de puntos.");
		}
		if (hayDescuentoDeEmpleado()) {
			throw new IllegalStateException("El bono no se combina con el descuento de empleado.");
		}
		validarBono(bono);
		this.bono = bono;
	}
    
    public void quitarBono() {
		validarNoFinalizada();
		this.bono = null;
	}
    
    public int calcularDescuentoBono() {
		if (bono == null) {
			return 0;
		}
		return (int) Math.round(calcularSubtotal() * bono.getPorcentaje());
	}

}
