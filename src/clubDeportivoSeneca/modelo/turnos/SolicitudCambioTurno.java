package clubDeportivoSeneca.modelo.turnos;
import java.time.LocalDate;
import java.time.LocalTime;
import clubDeportivoSeneca.modelo.usuario.Empleado;

public class SolicitudCambioTurno implements java.io.Serializable{
	 private Empleado empleado;
	    private Turno turnoActual;
	    private LocalDate nuevaFecha;
	    private LocalTime nuevaHoraInicio;
	    private LocalTime nuevaHoraFin;
	    private EstadoSolicitud estado;

	    public SolicitudCambioTurno(Empleado empleado, Turno turnoActual, LocalDate nuevaFecha, LocalTime nuevaHoraInicio, LocalTime nuevaHoraFin) throws Exception {
	        if (empleado == null) {
	            throw new Exception("empleado no puede ser null");
	        }
	        if (turnoActual == null) {
	            throw new Exception("turnoActual no puede ser null");
	        }
	        if (nuevaFecha == null) {
	            throw new Exception("nuevaFecha no puede ser null");
	        }
	        if (nuevaHoraInicio == null) {
	            throw new Exception("nuevaHoraInicio no puede ser null");
	        }
	        if (nuevaHoraFin == null) {
	            throw new Exception("nuevaHoraFin no puede ser null");
	        }
	        this.empleado = empleado;
	        this.turnoActual = turnoActual;
	        this.nuevaFecha = nuevaFecha;
	        this.nuevaHoraInicio = nuevaHoraInicio;
	        this.nuevaHoraFin = nuevaHoraFin;
	        this.estado = EstadoSolicitud.PENDIENTE;
	    }

	    public Empleado getEmpleado() {
	        return this.empleado;
	    }

	    public Turno getTurnoActual() {
	        return this.turnoActual;
	    }

	    public LocalDate getNuevaFecha() {
	        return this.nuevaFecha;
	    }

	    public LocalTime getNuevaHoraInicio() {
	        return this.nuevaHoraInicio;
	    }

	    public LocalTime getNuevaHoraFin() {
	        return this.nuevaHoraFin;
	    }

	    public EstadoSolicitud getEstado() {
	        return this.estado;
	    }

	    public void aprobar() {
	    	this.estado = EstadoSolicitud.APROBADA;
	    }

	    public void rechazar() {
	    	this.estado = EstadoSolicitud.RECHAZADA;
	    }

}
