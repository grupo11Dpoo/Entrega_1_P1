package clubDeportivoSeneca.modelo.instalaciones;

import java.time.LocalTime;

public class HorarioBloque implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

    private LocalTime horaInicio;
    private LocalTime horaFin;

    public HorarioBloque(LocalTime horaInicio, LocalTime horaFin) throws Exception {
        if (horaInicio == null || horaFin == null) {
            throw new Exception("El horario debe tener hora de inicio y de fin");
        }
        if (!horaInicio.isBefore(horaFin)) {
            throw new Exception("La hora de inicio debe ser anterior a la hora de fin");
        }
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public LocalTime getHoraInicio() {
        return this.horaInicio;
    }

    public LocalTime getHoraFin() {
        return this.horaFin;
    }

    public boolean solapaCon(HorarioBloque otro) {
        return this.horaInicio.isBefore(otro.getHoraFin()) && this.horaFin.isAfter(otro.getHoraInicio());
    }

    public int getDuracionMinutos() {
        return (horaFin.toSecondOfDay() - horaInicio.toSecondOfDay()) / 60;
    }
}