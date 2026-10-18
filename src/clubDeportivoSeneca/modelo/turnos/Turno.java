package clubDeportivoSeneca.modelo.turnos;
import java.time.LocalDate;
import java.time.LocalTime;
import clubDeportivoSeneca.modelo.usuario.Empleado;

public class Turno implements java.io.Serializable {
	private Empleado empleado;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    public Turno(Empleado empleado, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) throws Exception {
        if (empleado == null) {
            throw new Exception("empleado no puede ser null");
        }
        if (fecha == null) {
            throw new Exception("fecha no puede ser null");
        }
        if (horaInicio == null) {
            throw new Exception("horaInicio no puede ser null");
        }
        if (horaFin == null) {
            throw new Exception("horaFin no puede ser null");
        }
        if (!horaInicio.isBefore(horaFin)) {
            throw new Exception("La hora de inicio debe ser anterior a la hora de fin");
        }
        this.empleado = empleado;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public Empleado getEmpleado() {
        return this.empleado;
    }

    public LocalDate getFecha() {
        return this.fecha;
    }

    public LocalTime getHoraInicio() {
        return this.horaInicio;
    }

    public LocalTime getHoraFin() {
        return this.horaFin;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public boolean solapaCon(Turno otro) {
        if (!this.fecha.equals(otro.getFecha())) {
            return false;
        }
        return this.horaInicio.isBefore(otro.getHoraFin())
            && this.horaFin.isAfter(otro.getHoraInicio());
    }
    public boolean cubre(LocalDate fecha, LocalTime hora) {
        if (!this.fecha.equals(fecha)) {
            return false;
        }
        return !hora.isBefore(this.horaInicio) && hora.isBefore(this.horaFin);
    }

}
