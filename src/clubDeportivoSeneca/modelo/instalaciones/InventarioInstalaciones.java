package clubDeportivoSeneca.modelo.instalaciones;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import clubDeportivoSeneca.modelo.deportes.DeporteIndividual;
import clubDeportivoSeneca.modelo.usuario.Jugador;

public class InventarioInstalaciones {

    private List<Instalacion> instalaciones;
    private HistorialReservas historial;

    public InventarioInstalaciones(HistorialReservas historial) throws Exception {
        if (historial == null) {
            throw new Exception("El inventario necesita un historial de reservas");
        }
        this.instalaciones = new ArrayList<>();
        this.historial = historial;
    }

    public List<Instalacion> getInstalaciones() {
        return Collections.unmodifiableList(this.instalaciones);
    }

    public HistorialReservas getHistorial() {
        return this.historial;
    }

    public void agregarInstalacion(Instalacion instalacion) throws Exception {
        if (instalacion == null) {
            throw new Exception("No hay una instalación para agregar");
        }
        for (Instalacion i : instalaciones) {
            if (i.getId().equals(instalacion.getId())) {
                throw new Exception("Ya existe una instalación con el id " + instalacion.getId());
            }
        }
        instalaciones.add(instalacion);
    }

    public Reserva crearReserva(Jugador jugador, Instalacion instalacion, LocalDate fecha, HorarioBloque horario, int cantidadJugadores, Modalidad modalidad) throws Exception {
        if (!instalaciones.contains(instalacion)) {
            throw new Exception("La instalación no pertenece al club");
        }
        Reserva nueva = new Reserva(jugador, instalacion, fecha, horario, cantidadJugadores, modalidad);

        for (Reserva r : historial.getReservas()) {
            if (r.estaActiva() && r.solapaCon(nueva)) {
                if (r.getInstalacion() == instalacion) {
                    throw new Exception("La instalación ya está ocupada en ese horario");
                }
                if (r.getJugador().equals(jugador)) {
                    throw new Exception("El jugador ya tiene otra reserva en ese horario");
                }
            }
        }

        DeporteIndividual disciplina = instalacion.getDisciplina();
        if (contarReservasSimultaneas(disciplina, fecha, horario) >= disciplina.getMaxInstalacionesSimultaneas()) {
            throw new Exception("Cupo completo para " + disciplina.getNombre() + " en ese horario");
        }

        historial.registrar(nueva);
        return nueva;
    }

    public void cancelarReserva(Reserva reserva) throws Exception {
        reserva.cancelar();
    }

    public int contarReservasSimultaneas(DeporteIndividual disciplina, LocalDate fecha, HorarioBloque horario) {
        int contador = 0;
        for (Reserva r : historial.getReservas()) {
            if (r.estaActiva()
                    && r.getInstalacion().getDisciplina().equals(disciplina)
                    && r.getFecha().equals(fecha)
                    && r.getHorario().solapaCon(horario)) {
                contador++;
            }
        }
        return contador;
    }

    public List<Instalacion> getInstalacionesDisponibles(DeporteIndividual disciplina, LocalDate fecha, HorarioBloque horario) {
        List<Instalacion> disponibles = new ArrayList<>();
        if (contarReservasSimultaneas(disciplina, fecha, horario) >= disciplina.getMaxInstalacionesSimultaneas()) {
            return disponibles;
        }
        for (Instalacion instalacion : instalaciones) {
            if (!instalacion.getDisciplina().equals(disciplina)) {
                continue;
            }
            boolean ocupada = false;
            for (Reserva r : historial.getReservas()) {
                if (r.estaActiva() && r.getInstalacion() == instalacion
                        && r.getFecha().equals(fecha) && r.getHorario().solapaCon(horario)) {
                    ocupada = true;
                }
            }
            if (!ocupada) {
                disponibles.add(instalacion);
            }
        }
        return disponibles;
    }
}