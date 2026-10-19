package clubDeportivoSeneca.modelo.usuario;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import clubDeportivoSeneca.modelo.inventario.SugerenciaProducto;
import clubDeportivoSeneca.modelo.inventario.TipoSugerencia;
import clubDeportivoSeneca.modelo.turnos.Turno;
import clubDeportivoSeneca.modelo.turnos.SolicitudCambioTurno;

public abstract class Empleado extends Usuario implements java.io.Serializable, Comprador {
	private static final long serialVersionUID = 1L;
	
	private String nombre;
	private String codigoDescuento;
	private int puntosFidelidad;
	private List<Turno> turnosSemanales;
	private Jugador perfilJugador;
	
	protected Empleado(String login, String password, String nombre, String codigoDescuento) {
		super(login, password);
		this.nombre = nombre;
		this.codigoDescuento = codigoDescuento;
		this.puntosFidelidad = 0;
		this.turnosSemanales = new ArrayList<Turno>();
		this.perfilJugador = null;
		
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Jugador getPerfilJugador() {
		return perfilJugador;
	}

	public void setPerfilJugador(Jugador perfilJugador) {
		this.perfilJugador = perfilJugador;
	}

	public String getCodigoDescuento() {
		return codigoDescuento;
	}

	public int getPuntosFidelidad() {
		return puntosFidelidad;
	}

	public List<Turno> getTurnosSemanales() {
		return turnosSemanales;
	}
	
	public void agregarTurno(Turno turno) throws Exception{
		
		if(turno == null) {
			throw new Exception("No hay un turno para agregar");
		}
		
		for(Turno t : turnosSemanales) {
			if(t.solapaCon(turno)) {
				throw new Exception("El turno se cruza con otro de este empleado.");
			}
		}
		
		turnosSemanales.add(turno);
	}
	
	public void quitarTurno(Turno turno) throws Exception {
		
		if (!turnosSemanales.contains(turno)) {
            throw new Exception("El turno que desea eliminar no pertenece a este empleado.");
        }
        turnosSemanales.remove(turno);
	}
	
	public SolicitudCambioTurno solicitarCambioTurno(Turno turnoActual, LocalDate fecha, LocalTime horaInicio, 
			LocalTime horaFin) throws Exception {
		
		if(!turnosSemanales.contains(turnoActual)) {
			throw new Exception("El turno que desea cambiar no le pertence a este empleado.");
		}
		
		return new SolicitudCambioTurno(this, turnoActual, fecha, horaInicio, horaFin);
	}
	
	public SugerenciaProducto sugerirproducto(String nombre, String descripcion, TipoSugerencia tipo) {
		
		return new SugerenciaProducto(this, nombre, descripcion, tipo);
	}

	public boolean esTambienJugador() {
		return perfilJugador != null;	
	}
	
	public void acumularPuntos(int valorCompra) {
		
		puntosFidelidad += (valorCompra * 0.02);
		
	}
	
	@Override
	public void redimirPuntos(int valorCompra) throws Exception {
		
		if(puntosFidelidad == 0) {
			throw new Exception("No hay puntos para redimir.");
		} else if(valorCompra >= puntosFidelidad){
			valorCompra -= puntosFidelidad;
			puntosFidelidad = 0;
		} else {
			puntosFidelidad -= valorCompra;
			valorCompra = 0;
		}
		
	}
	

}
