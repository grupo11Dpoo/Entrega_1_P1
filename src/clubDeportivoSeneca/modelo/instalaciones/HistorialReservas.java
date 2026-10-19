package clubDeportivoSeneca.modelo.instalaciones;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import clubDeportivoSeneca.modelo.usuario.Jugador;

public class HistorialReservas implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

    private List<Reserva> reservas;

    public HistorialReservas() {
        this.reservas = new ArrayList<>();
    }

    public List<Reserva> getReservas() {
        return Collections.unmodifiableList(this.reservas);
    }

    public void registrar(Reserva reserva) {
        reservas.add(reserva);
    }

    public List<Reserva> consultarPorJugador(Jugador jugador) {
        List<Reserva> resultado = new ArrayList<>();
        for (Reserva reserva : reservas) {
            if (reserva.getJugador().equals(jugador)) {
                resultado.add(reserva);
            }
        }
        return resultado;
    }

    public List<Reserva> consultarPorFecha(LocalDate fecha) {
        List<Reserva> resultado = new ArrayList<>();
        for (Reserva reserva : reservas) {
            if (reserva.getFecha().equals(fecha)) {
                resultado.add(reserva);
            }
        }
        return resultado;
    }
}