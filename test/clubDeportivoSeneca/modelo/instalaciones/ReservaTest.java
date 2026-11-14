package clubDeportivoSeneca.modelo.instalaciones;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import clubDeportivoSeneca.modelo.deportes.Padel;
import clubDeportivoSeneca.modelo.deportes.Tenis;
import clubDeportivoSeneca.modelo.usuario.Jugador;

public class ReservaTest {

    private HistorialReservas historial;
    private InventarioInstalaciones inventario;
    private Tenis tenis;
    private CanchaTenis cancha1;
    private CanchaTenis cancha2;
    private Jugador ana;
    private Jugador juan;
    private LocalDate manana;

    @BeforeEach
    public void setUp() throws Exception {
        historial = new HistorialReservas();
        inventario = new InventarioInstalaciones(historial);
        tenis = new Tenis("Tenis", 1); // maximo 1 cancha de tenis al tiempo
        cancha1 = new CanchaTenis("T1", tenis);
        cancha2 = new CanchaTenis("T2", tenis);
        inventario.agregarInstalacion(cancha1);
        inventario.agregarInstalacion(cancha2);

        ana = new Jugador("ana", "1234", "Ana", LocalDate.of(2000, 1, 1));
        juan = new Jugador("juan", "1234", "Juan", LocalDate.of(2000, 1, 1));
        manana = LocalDate.now().plusDays(1);
    }

    /** Crea un bloque horario entre dos horas en punto. */
    private HorarioBloque bloque(int horaInicio, int horaFin) throws Exception {
        return new HorarioBloque(LocalTime.of(horaInicio, 0), LocalTime.of(horaFin, 0));
    }

    @Test
    @DisplayName("Una reserva valida queda activa y registrada en el historial")
    public void reservaValida() throws Exception {
        Reserva reserva = inventario.crearReserva(ana, cancha1, manana, bloque(10, 11), 2, Modalidad.SENCILLOS);

        assertTrue(reserva.estaActiva());
        assertEquals(1, historial.consultarPorJugador(ana).size());
    }

    @Test
    @DisplayName("Rechaza una reserva que se cruza con otra en la misma instalacion")
    public void rechazaTraslape() throws Exception {
        inventario.crearReserva(ana, cancha1, manana, bloque(10, 12), 2, Modalidad.SENCILLOS);

        assertThrows(Exception.class,
                () -> inventario.crearReserva(juan, cancha1, manana, bloque(11, 13), 2, Modalidad.SENCILLOS));
        assertEquals(1, historial.getReservas().size(), "Un rechazo no debe registrar nada");
    }

    @Test
    @DisplayName("Acepta bloques seguidos en la misma instalacion (10-11 y 11-12)")
    public void aceptaBloquesSeguidos() throws Exception {
        inventario.crearReserva(ana, cancha1, manana, bloque(10, 11), 2, Modalidad.SENCILLOS);

        Reserva segunda = inventario.crearReserva(juan, cancha1, manana, bloque(11, 12), 2, Modalidad.SENCILLOS);

        assertTrue(segunda.estaActiva());
    }

    @Test
    @DisplayName("Rechaza superar el maximo de instalaciones simultaneas de la disciplina")
    public void rechazaCupoDeDisciplina() throws Exception {
        inventario.crearReserva(ana, cancha1, manana, bloque(10, 11), 2, Modalidad.SENCILLOS);

        assertThrows(Exception.class,
                () -> inventario.crearReserva(juan, cancha2, manana, bloque(10, 11), 2, Modalidad.SENCILLOS));
    }

    @Test
    @DisplayName("Rechaza sencillos con un numero de jugadores distinto de 2")
    public void rechazaSencillosConCuatro() {
        assertThrows(Exception.class,
                () -> inventario.crearReserva(ana, cancha1, manana, bloque(10, 11), 4, Modalidad.SENCILLOS));
    }

    @Test
    @DisplayName("Rechaza padel con un numero de jugadores distinto de 4")
    public void rechazaPadelConDos() throws Exception {
        Padel padel = new Padel("Padel", 2);
        CanchaPadel canchaPadel = new CanchaPadel("P1", padel);
        inventario.agregarInstalacion(canchaPadel);

        assertThrows(Exception.class,
                () -> inventario.crearReserva(ana, canchaPadel, manana, bloque(10, 11), 2, Modalidad.SENCILLOS));
    }

    @Test
    @DisplayName("Cancelar una reserva libera el bloque para otra persona")
    public void cancelarLiberaElBloque() throws Exception {
        Reserva reserva = inventario.crearReserva(ana, cancha1, manana, bloque(10, 11), 2, Modalidad.SENCILLOS);

        inventario.cancelarReserva(reserva);
        Reserva nueva = inventario.crearReserva(juan, cancha1, manana, bloque(10, 11), 2, Modalidad.SENCILLOS);

        assertFalse(reserva.estaActiva());
        assertTrue(nueva.estaActiva());
    }

    @Test
    @DisplayName("Rechaza cancelar dos veces la misma reserva")
    public void rechazaCancelarDosVeces() throws Exception {
        Reserva reserva = inventario.crearReserva(ana, cancha1, manana, bloque(10, 11), 2, Modalidad.SENCILLOS);
        inventario.cancelarReserva(reserva);

        assertThrows(Exception.class, () -> inventario.cancelarReserva(reserva));
    }

    @Test
    @DisplayName("Rechaza un bloque cuya hora de fin es anterior a la de inicio")
    public void rechazaHorarioInvalido() {
        assertThrows(Exception.class, () -> bloque(12, 10));
    }
}