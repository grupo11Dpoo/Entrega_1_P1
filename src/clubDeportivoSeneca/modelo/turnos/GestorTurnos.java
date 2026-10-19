package clubDeportivoSeneca.modelo.turnos;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import clubDeportivoSeneca.modelo.usuario.Empleado;
import clubDeportivoSeneca.modelo.usuario.Entrenador;
import clubDeportivoSeneca.modelo.usuario.Fisioterapeuta;

public class GestorTurnos implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	 public static final int MIN_ENTRENADORES = 2;
	    public static final int MIN_FISIOTERAPEUTAS = 1;

	    private List<Turno> turnos;
	    private List<SolicitudCambioTurno> solicitudes;
	    private LocalTime horaApertura;
	    private LocalTime horaCierre;

	    public GestorTurnos(LocalTime horaApertura, LocalTime horaCierre) throws Exception {
	        if (horaApertura == null) {
	            throw new Exception("horaApertura no puede ser null");
	        }
	        if (horaCierre == null) {
	            throw new Exception("horaCierre no puede ser null");
	        }
	        this.turnos = new ArrayList<>();
	        this.solicitudes = new ArrayList<>();
	        this.horaApertura = horaApertura;
	        this.horaCierre = horaCierre;
	    }

	    public List<Turno> getTurnos() {
	        return Collections.unmodifiableList(this.turnos);
	    }

	    public List<SolicitudCambioTurno> getSolicitudes() {
	        return Collections.unmodifiableList(this.solicitudes);
	    }

	    public LocalTime getHoraApertura() {
	        return this.horaApertura;
	    }

	    public LocalTime getHoraCierre() {
	        return this.horaCierre;
	    }

	    public void setHoraApertura(LocalTime horaApertura) {
	        this.horaApertura = horaApertura;
	    }

	    public void setHoraCierre(LocalTime horaCierre) {
	        this.horaCierre = horaCierre;
	    }

	    public Turno asignarTurno(Empleado empleado, LocalDate fecha, LocalTime inicio, LocalTime fin) throws Exception {
	        if (inicio.isBefore(horaApertura) || fin.isAfter(horaCierre)) {
	            throw new Exception("El turno debe estar dentro del horario de atención");
	        }
	        Turno nuevo = new Turno(empleado, fecha, inicio, fin);
	        empleado.agregarTurno(nuevo);
	        turnos.add(nuevo);
	        return nuevo;
	    }

	    public boolean cumpleMinimos(LocalDate fecha) {
	        List<LocalTime> momentos = new ArrayList<>();
	        momentos.add(horaApertura);
	        for (Turno t : turnos) {
	            if (t.getFecha().equals(fecha)) {
	                momentos.add(t.getHoraInicio());
	                momentos.add(t.getHoraFin());
	            }
	        }

	        for (LocalTime hora : momentos) {
	            if (hora.isBefore(horaApertura) || !hora.isBefore(horaCierre)) {
	                continue;
	            }
	            int entrenadores = 0;
	            int fisioterapeutas = 0;
	            for (Turno t : turnos) {
	                if (t.cubre(fecha, hora)) {
	                    if (t.getEmpleado() instanceof Entrenador) {
	                        entrenadores++;
	                    } else if (t.getEmpleado() instanceof Fisioterapeuta) {
	                        fisioterapeutas++;
	                    }
	                }
	            }
	            if (entrenadores < MIN_ENTRENADORES || fisioterapeutas < MIN_FISIOTERAPEUTAS) {
	                return false;
	            }
	        }
	        return true;
	    }

	    public void registrarSolicitud(SolicitudCambioTurno solicitud) {
	        solicitudes.add(solicitud);
	    }

	    public boolean aprobarCambio(SolicitudCambioTurno solicitud) throws Exception {
	        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
	            throw new Exception("La solicitud ya fue respondida");
	        }
	        Turno turno = solicitud.getTurnoActual();

	        LocalDate fechaAnterior = turno.getFecha();
	        LocalTime inicioAnterior = turno.getHoraInicio();
	        LocalTime finAnterior = turno.getHoraFin();

	        turno.setFecha(solicitud.getNuevaFecha());
	        turno.setHoraInicio(solicitud.getNuevaHoraInicio());
	        turno.setHoraFin(solicitud.getNuevaHoraFin());

	        boolean valido = !turno.getHoraInicio().isBefore(horaApertura)
	                && !turno.getHoraFin().isAfter(horaCierre)
	                && turno.getHoraInicio().isBefore(turno.getHoraFin());
	        for (Turno otro : turnos) {
	            if (otro != turno && otro.getEmpleado() == turno.getEmpleado() && otro.solapaCon(turno)) {
	                valido = false;
	            }
	        }
	        if (valido) {
	            valido = cumpleMinimos(fechaAnterior) && cumpleMinimos(turno.getFecha());
	        }

	        if (valido) {
	            solicitud.aprobar();
	            return true;
	        }
	        turno.setFecha(fechaAnterior);
	        turno.setHoraInicio(inicioAnterior);
	        turno.setHoraFin(finAnterior);
	        solicitud.rechazar();
	        return false;
	    }

	    public void rechazarCambio(SolicitudCambioTurno solicitud) {
	        solicitud.rechazar();
	    }
}
