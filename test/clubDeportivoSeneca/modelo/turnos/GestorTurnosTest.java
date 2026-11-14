package clubDeportivoSeneca.modelo.turnos;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import clubDeportivoSeneca.modelo.usuario.Entrenador;
import clubDeportivoSeneca.modelo.usuario.Fisioterapeuta;

public class GestorTurnosTest {

    private static final LocalTime APERTURA = LocalTime.of(8, 0);
    private static final LocalTime CIERRE = LocalTime.of(18, 0);

    private GestorTurnos gestor;
    private Entrenador e1;
    private Entrenador e2;
    private Fisioterapeuta f1;
    private LocalDate lunes;
    private LocalDate martes;

    @BeforeEach
    public void setUp() throws Exception {
        gestor = new GestorTurnos(APERTURA, CIERRE);
        e1 = new Entrenador("e1", "1234", "Entrenador 1", "E1");
        e2 = new Entrenador("e2", "1234", "Entrenador 2", "E2");
        f1 = new Fisioterapeuta("f1", "1234", "Fisio 1", "F1");
        lunes = LocalDate.of(2026, 11, 16);
        martes = lunes.plusDays(1);

        // El lunes queda cubierto justo con el minimo: 2 entrenadores y 1 fisioterapeuta
        gestor.asignarTurno(e1, lunes, APERTURA, CIERRE);
        gestor.asignarTurno(e2, lunes, APERTURA, CIERRE);
        gestor.asignarTurno(f1, lunes, APERTURA, CIERRE);
    }

    @Test
    @DisplayName("Asignar un turno lo guarda en el gestor y en el empleado")
    public void asignarTurnoValido() throws Exception {
        Entrenador e3 = new Entrenador("e3", "1234", "Entrenador 3", "E3");

        Turno turno = gestor.asignarTurno(e3, martes, LocalTime.of(9, 0), LocalTime.of(13, 0));

        assertTrue(gestor.getTurnos().contains(turno));
        assertTrue(e3.getTurnosSemanales().contains(turno));
    }

    @Test
    @DisplayName("Rechaza un turno fuera del horario de atencion")
    public void rechazaTurnoFueraDeHorario() {
        assertThrows(Exception.class,
                () -> gestor.asignarTurno(e1, martes, LocalTime.of(6, 0), LocalTime.of(10, 0)));
    }

    @Test
    @DisplayName("Rechaza un turno que se cruza con otro del mismo empleado")
    public void rechazaTurnoCruzado() {
        assertThrows(Exception.class,
                () -> gestor.asignarTurno(e1, lunes, LocalTime.of(10, 0), LocalTime.of(12, 0)));
        assertEquals(1, e1.getTurnosSemanales().size());
    }

    @Test
    @DisplayName("Un dia con 2 entrenadores y 1 fisioterapeuta cumple los minimos")
    public void cumpleMinimos() {
        assertTrue(gestor.cumpleMinimos(lunes));
    }

    @Test
    @DisplayName("Un dia sin personal no cumple los minimos")
    public void noCumpleMinimosSinPersonal() {
        assertFalse(gestor.cumpleMinimos(martes));
    }

    @Test
    @DisplayName("Una solicitud de cambio queda pendiente y el turno original sigue igual")
    public void solicitudQuedaPendiente() throws Exception {
        Turno turno = e1.getTurnosSemanales().get(0);

        SolicitudCambioTurno solicitud = e1.solicitarCambioTurno(turno, martes, APERTURA, CIERRE);

        assertEquals(EstadoSolicitud.PENDIENTE, solicitud.getEstado());
        assertEquals(lunes, turno.getFecha());
    }

    @Test
    @DisplayName("Rechaza solicitar el cambio de un turno que no es propio")
    public void rechazaSolicitudDeTurnoAjeno() {
        Turno turnoDeE2 = e2.getTurnosSemanales().get(0);

        assertThrows(Exception.class, () -> e1.solicitarCambioTurno(turnoDeE2, martes, APERTURA, CIERRE));
    }

    @Test
    @DisplayName("Se aprueba un cambio que deja ambos dias con el minimo de personal")
    public void apruebaCambioValido() throws Exception {
        // Un tercer entrenador el lunes y el martes cubierto por otro equipo
        Entrenador e3 = new Entrenador("e3", "1234", "Entrenador 3", "E3");
        Turno turnoE3 = gestor.asignarTurno(e3, lunes, APERTURA, CIERRE);
        gestor.asignarTurno(new Entrenador("e4", "1234", "Entrenador 4", "E4"), martes, APERTURA, CIERRE);
        gestor.asignarTurno(new Entrenador("e5", "1234", "Entrenador 5", "E5"), martes, APERTURA, CIERRE);
        gestor.asignarTurno(new Fisioterapeuta("f2", "1234", "Fisio 2", "F2"), martes, APERTURA, CIERRE);

        SolicitudCambioTurno solicitud = e3.solicitarCambioTurno(turnoE3, martes, APERTURA, LocalTime.of(12, 0));
        boolean aprobada = gestor.aprobarCambio(solicitud);

        assertTrue(aprobada);
        assertEquals(EstadoSolicitud.APROBADA, solicitud.getEstado());
        assertEquals(martes, turnoE3.getFecha());
    }

    @Test
    @DisplayName("Se rechaza un cambio que deja el dia sin el minimo de personal")
    public void rechazaCambioQueDejaSinMinimo() throws Exception {
        Turno turnoE1 = e1.getTurnosSemanales().get(0);
        SolicitudCambioTurno solicitud = e1.solicitarCambioTurno(turnoE1, martes, APERTURA, CIERRE);

        boolean aprobada = gestor.aprobarCambio(solicitud);

        assertFalse(aprobada);
        assertEquals(EstadoSolicitud.RECHAZADA, solicitud.getEstado());
        assertEquals(lunes, turnoE1.getFecha(), "El turno debe volver a su horario original");
    }

    @Test
    @DisplayName("Rechaza responder dos veces la misma solicitud")
    public void rechazaSolicitudYaRespondida() throws Exception {
        Turno turnoE1 = e1.getTurnosSemanales().get(0);
        SolicitudCambioTurno solicitud = e1.solicitarCambioTurno(turnoE1, martes, APERTURA, CIERRE);
        gestor.rechazarCambio(solicitud);

        assertThrows(Exception.class, () -> gestor.aprobarCambio(solicitud));
    }
}