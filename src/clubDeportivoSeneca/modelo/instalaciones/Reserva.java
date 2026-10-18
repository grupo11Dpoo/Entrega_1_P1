package clubDeportivoSeneca.modelo.instalaciones;

import java.time.LocalDate;

import clubDeportivoSeneca.modelo.usuario.Jugador;

public class Reserva implements java.io.Serializable {

    private Jugador jugador;
    private Instalacion instalacion;
    private LocalDate fecha;
    private HorarioBloque horario;
    private int cantidadJugadores;
    private Modalidad modalidad;
    private EstadoReserva estado;

    public Reserva(Jugador jugador, Instalacion instalacion, LocalDate fecha, HorarioBloque horario, int cantidadJugadores, Modalidad modalidad) throws Exception {
        if (jugador == null || instalacion == null || fecha == null || horario == null || modalidad == null) {
            throw new Exception("La reserva debe tener jugador, instalación, fecha, horario y modalidad");
        }
        this.jugador = jugador;
        this.instalacion = instalacion;
        this.fecha = fecha;
        this.horario = horario;
        this.cantidadJugadores = cantidadJugadores;
        this.modalidad = modalidad;
        this.estado = EstadoReserva.ACTIVA;
        validar();
    }

    public Jugador getJugador() {
        return this.jugador;
    }

    public Instalacion getInstalacion() {
        return this.instalacion;
    }

    public LocalDate getFecha() {
        return this.fecha;
    }

    public HorarioBloque getHorario() {
        return this.horario;
    }

    public int getCantidadJugadores() {
        return this.cantidadJugadores;
    }

    public Modalidad getModalidad() {
        return this.modalidad;
    }

    public EstadoReserva getEstado() {
        return this.estado;
    }

    public void validar() throws Exception {
        if (modalidad == Modalidad.SENCILLOS && cantidadJugadores != 2) {
            throw new Exception("Una reserva de sencillos debe ser para 2 jugadores");
        }
        if (modalidad == Modalidad.DOBLES && cantidadJugadores != 4) {
            throw new Exception("Una reserva de dobles debe ser para 4 jugadores");
        }
        if (!instalacion.admiteJugadores(cantidadJugadores)) {
            throw new Exception("La instalación no admite " + cantidadJugadores + " jugadores");
        }
        if (instalacion.restringeBebidaCaliente() && jugador.isBebidaCaliente()) {
            throw new Exception("No se puede ingresar a esta instalación con una bebida caliente");
        }
    }

    public void cancelar() throws Exception {
        if (estado == EstadoReserva.CANCELADA) {
            throw new Exception("La reserva ya está cancelada");
        }
        estado = EstadoReserva.CANCELADA;
    }

    public boolean estaActiva() {
        return estado == EstadoReserva.ACTIVA;
    }

    public boolean solapaCon(Reserva otra) {
        return this.fecha.equals(otra.getFecha()) && this.horario.solapaCon(otra.getHorario());
    }
}