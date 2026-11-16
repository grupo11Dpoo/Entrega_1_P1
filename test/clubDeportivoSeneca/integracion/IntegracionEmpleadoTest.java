package clubDeportivoSeneca.integracion;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import clubDeportivoSeneca.modelo.competencias.PartidoCompetencia;
import clubDeportivoSeneca.modelo.competencias.RequisitosInscripcionCircuito;
import clubDeportivoSeneca.modelo.competencias.TorneoCerros;
import clubDeportivoSeneca.modelo.entrenamiento.Entrenamiento;
import clubDeportivoSeneca.modelo.entrenamiento.EstadoAsistencia;
import clubDeportivoSeneca.modelo.equipos.Equipo;
import clubDeportivoSeneca.modelo.inventario.EstadoSugerencia;
import clubDeportivoSeneca.modelo.inventario.SugerenciaProducto;
import clubDeportivoSeneca.modelo.inventario.TipoSugerencia;
import clubDeportivoSeneca.modelo.partidos.EstadFutbol;
import clubDeportivoSeneca.modelo.partidos.PartidoInterno;
import clubDeportivoSeneca.modelo.turnos.EstadoSolicitud;
import clubDeportivoSeneca.modelo.turnos.SolicitudCambioTurno;
import clubDeportivoSeneca.modelo.turnos.Turno;
import clubDeportivoSeneca.modelo.usuario.Entrenador;
import clubDeportivoSeneca.modelo.usuario.Jugador;
import clubDeportivoSeneca.modelo.ventas.DescuentoEmpleado;
import clubDeportivoSeneca.modelo.ventas.VentaArticulosDeportivos;

public class IntegracionEmpleadoTest {

    private Escenario e;
    private Entrenador carlos;
    private LocalDate proximaSemana;

    @BeforeEach
    public void setUp() throws Exception {
        e = new Escenario();
        carlos = e.entrenador("carlos");
        proximaSemana = LocalDate.now().plusWeeks(1);
    }

    /** Torneo de los Cerros Sub-15 registrado en el club, con un equipo completo inscrito. */
    private TorneoCerros competenciaConEquipo(Equipo equipo) throws Exception {
        TorneoCerros cerros = new TorneoCerros("Torneo de los Cerros", "Liga de Bogota", "2026-2", e.futbol, e.sub15,
                new RequisitosInscripcionCircuito(7, e.sub15));
        e.club.registrarCircuito(cerros);
        e.club.inscribirEquipoEnCompetencia(cerros, equipo, new ArrayList<>(equipo.getJugadores()));
        return cerros;
    }

    @Test
    @DisplayName("E1 - El entrenador organiza un partido interno entre dos equipos del club")
    public void e1OrganizarPartidoInterno() throws Exception {
        Equipo a = e.equipo("Locales", e.sub15, 14, 7);
        Equipo b = e.equipo("Visitantes", e.sub15, 14, 7);

        PartidoInterno p = new PartidoInterno(proximaSemana, e.futbol, a, b);
        e.club.registrarPartidoInterno(p);

        assertTrue(e.club.getPartidosInternos().contains(p));
        Equipo incompleto = e.equipo("Incompleto", e.sub15, 14, 3);
        assertThrows(Exception.class, () -> new PartidoInterno(proximaSemana, e.futbol, a, incompleto));
    }

    @Test
    @DisplayName("E2 - Se registra el marcador y las estadisticas de un partido interno")
    public void e2RegistrarResultadoYEstadisticas() throws Exception {
        Equipo a = e.equipo("Locales", e.sub15, 14, 7);
        Equipo b = e.equipo("Visitantes", e.sub15, 14, 7);
        PartidoInterno p = new PartidoInterno(proximaSemana, e.futbol, a, b);
        e.club.registrarPartidoInterno(p);
        Jugador goleador = a.getJugadores().get(0);
        EstadFutbol stats = new EstadFutbol(goleador, p);
        stats.registrarGol();
        stats.registrarGol();

        p.registrarEstadistica(stats);
        p.registrarMarcador(2, 0);
        p.finalizarPartido();

        assertTrue(p.isFinalizado());
        assertEquals(a, p.getGanador());
        assertEquals(2, ((EstadFutbol) p.getEstadisticasJugador(goleador)).getGoles());
    }

    @Test
    @DisplayName("E3 - Dos amarillas en un partido generan una sancion al jugador")
    public void e3RegistrarTarjetas() throws Exception {
        Equipo a = e.equipo("Locales", e.sub15, 14, 7);
        Equipo b = e.equipo("Visitantes", e.sub15, 14, 7);
        PartidoInterno p = new PartidoInterno(proximaSemana, e.futbol, a, b);
        Jugador jugador = a.getJugadores().get(0);
        EstadFutbol stats = new EstadFutbol(jugador, p);
        stats.registrarAmarilla();
        stats.registrarAmarilla();

        p.registrarEstadistica(stats);
        p.registrarMarcador(1, 1);
        p.finalizarPartido();

        assertEquals(1, jugador.getSanciones().size());
        assertTrue(jugador.getSanciones().get(0).estaVigente());
    }

    @Test
    @DisplayName("E4 - El entrenador planea una sesion y convoca a su equipo")
    public void e4PlanearSesion() throws Exception {
        Equipo equipo = e.equipo("Seneca", e.sub15, 14, 7);

        Entrenamiento sesion = e.club.planearEntrenamiento(carlos, proximaSemana, LocalTime.of(16, 0), 90, e.cancha);
        sesion.convocarEquipo(equipo);

        assertTrue(e.club.getEntrenamientos().contains(sesion));
        assertEquals(7, sesion.getConvocados().size());
        assertThrows(Exception.class, () -> e.club.planearEntrenamiento(carlos, proximaSemana, LocalTime.of(16, 0), 0, e.cancha));
    }

    @Test
    @DisplayName("E5 - El entrenador registra la asistencia solo de los convocados")
    public void e5RegistrarAsistencia() throws Exception {
        Jugador ana = e.socio("ana", 14);
        Jugador intrusa = e.socio("intrusa", 14);
        Entrenamiento sesion = e.club.planearEntrenamiento(carlos, proximaSemana, LocalTime.of(16, 0), 60, e.cancha);
        sesion.convocar(ana);

        carlos.registrarAsistencia(sesion, ana, EstadoAsistencia.LLEGO_TARDE);

        assertEquals(EstadoAsistencia.LLEGO_TARDE, sesion.getAsistenciaDe(ana).getEstado());
        assertThrows(Exception.class, () -> carlos.registrarAsistencia(sesion, intrusa, EstadoAsistencia.ASISTIO));
    }

    @Test
    @DisplayName("E6 - El entrenador evalua con nota de 1 a 5 a quien asistio")
    public void e6RegistrarEvaluacion() throws Exception {
        Jugador ana = e.socio("ana", 14);
        Jugador juan = e.socio("juan", 14);
        Entrenamiento sesion = e.club.planearEntrenamiento(carlos, proximaSemana, LocalTime.of(16, 0), 60, e.cancha);
        sesion.convocar(ana);
        sesion.convocar(juan);
        carlos.registrarAsistencia(sesion, ana, EstadoAsistencia.ASISTIO);
        carlos.registrarAsistencia(sesion, juan, EstadoAsistencia.FALTO);

        carlos.registrarEvaluacion(sesion, ana, 5, "Excelente");

        assertEquals(5, sesion.getAsistenciaDe(ana).getEvaluacion().getNota());
        assertThrows(Exception.class, () -> carlos.registrarEvaluacion(sesion, juan, 3, "Falto"), "No se evalua a quien falto");
        assertThrows(Exception.class, () -> carlos.registrarEvaluacion(sesion, ana, 6, "Fuera de rango"));
    }

    @Test
    @DisplayName("E7 - Se inscribe un equipo en una competencia con su nomina")
    public void e7InscribirEquipoEnCompetencia() throws Exception {
        Equipo equipo = e.equipo("Seneca", e.sub15, 14, 7);

        TorneoCerros cerros = competenciaConEquipo(equipo);

        assertNotNull(cerros.getInscripcionDeEquipo(equipo));
        Equipo incompleto = e.equipo("Incompleto", e.sub15, 14, 5);
        assertThrows(Exception.class,
                () -> e.club.inscribirEquipoEnCompetencia(cerros, incompleto, new ArrayList<>(incompleto.getJugadores())));
    }

    @Test
    @DisplayName("E8 - Se registra un partido de competencia y suma puntos en la liga")
    public void e8RegistrarPartidoCompetencia() throws Exception {
        Equipo equipo = e.equipo("Seneca", e.sub15, 14, 7);
        TorneoCerros cerros = competenciaConEquipo(equipo);
        PartidoCompetencia p = new PartidoCompetencia(LocalDate.now(), equipo, "Colegio Andino", "Cancha 1", cerros);

        e.club.registrarPartidoCompetencia(p);
        p.registrarMarcador(3, 1);
        p.finalizarPartido();
        cerros.registrarResultado(p);

        assertTrue(e.club.getPartidosCompetencia().contains(p));
        assertEquals(3, cerros.getEstadisticasliga().getPuntos("Seneca"));
    }

    @Test
    @DisplayName("G2 - Se consulta la tabla de posiciones de una competencia")
    public void g2ConsultarClasificacion() throws Exception {
        Equipo equipo = e.equipo("Seneca", e.sub15, 14, 7);
        TorneoCerros cerros = competenciaConEquipo(equipo);
        cerros.registrarResultadoExterno("Colegio A", 2, "Colegio B", 0);
        cerros.registrarResultadoExterno("Seneca", 1, "Colegio A", 1);

        List<String> tabla = cerros.calcularClasificacion();

        assertEquals("Colegio A: 4 puntos", tabla.get(0));
        assertEquals(3, tabla.size());
    }

    @Test
    @DisplayName("EM1 - El empleado consulta los turnos que le asigno el administrador")
    public void em1ConsultarTurnos() throws Exception {
        assertTrue(carlos.getTurnosSemanales().isEmpty(), "Sin turnos asignados la lista esta vacia");

        Turno turno = e.admin.asignarTurno(carlos, proximaSemana, LocalTime.of(8, 0), LocalTime.of(12, 0));

        assertEquals(1, carlos.getTurnosSemanales().size());
        assertTrue(e.club.getGestorTurnos().getTurnos().contains(turno));
    }

    @Test
    @DisplayName("EM2 - El empleado solicita un cambio de turno y queda pendiente")
    public void em2SolicitarCambioTurno() throws Exception {
        Turno turno = e.admin.asignarTurno(carlos, proximaSemana, LocalTime.of(8, 0), LocalTime.of(12, 0));

        SolicitudCambioTurno s = carlos.solicitarCambioTurno(turno, proximaSemana, LocalTime.of(14, 0), LocalTime.of(18, 0));
        e.club.getGestorTurnos().registrarSolicitud(s);

        assertEquals(EstadoSolicitud.PENDIENTE, s.getEstado());
        assertEquals(LocalTime.of(8, 0), turno.getHoraInicio(), "El turno original sigue vigente");
        assertTrue(e.club.getGestorTurnos().getSolicitudes().contains(s));
    }

    @Test
    @DisplayName("EM3 - El empleado sugiere un producto y queda pendiente de aprobacion")
    public void em3SugerirProducto() {
        SugerenciaProducto s = carlos.sugerirproducto("Barra de proteina", "Snack sin azucar", TipoSugerencia.SNACK);
        e.club.registrarSugerencia(s);

        assertEquals(EstadoSugerencia.PENDIENTE, s.getEstado());
        assertSame(carlos, s.getEmpleado());
        assertTrue(e.club.getSugerencias().contains(s));
    }

    @Test
    @DisplayName("EM4 - El empleado compra con su propio codigo y recibe 15 % de descuento")
    public void em4ComprarConCodigo() {
        VentaArticulosDeportivos venta = new VentaArticulosDeportivos(LocalDate.now(), carlos);
        venta.agregarArticulo(e.balon, 2, e.club.getInventarioArticulos());
        venta.setDescuento(new DescuentoEmpleado(carlos));

        venta.finalizar(e.club.getInventarioArticulos());
        e.club.registrarVenta(venta);

        assertEquals(15000, venta.calcularDescuento());
        assertEquals(101150, venta.getPrecioTotal(), "(100.000 - 15.000) + 19 % de IVA");
    }
}