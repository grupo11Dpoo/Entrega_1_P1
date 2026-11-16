package clubDeportivoSeneca.integracion;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import clubDeportivoSeneca.modelo.competencias.Ascun;
import clubDeportivoSeneca.modelo.competencias.RequisitosInscripcionCircuito;
import clubDeportivoSeneca.modelo.entrenamiento.Entrenamiento;
import clubDeportivoSeneca.modelo.entrenamiento.EstadoAsistencia;
import clubDeportivoSeneca.modelo.equipos.Equipo;
import clubDeportivoSeneca.modelo.individual.NivelJugador;
import clubDeportivoSeneca.modelo.individual.PartidoIndividual;
import clubDeportivoSeneca.modelo.instalaciones.Modalidad;
import clubDeportivoSeneca.modelo.inventario.EstadoSugerencia;
import clubDeportivoSeneca.modelo.inventario.InventarioArticulos;
import clubDeportivoSeneca.modelo.inventario.SugerenciaProducto;
import clubDeportivoSeneca.modelo.inventario.TipoSugerencia;
import clubDeportivoSeneca.modelo.partidos.PartidoInterno;
import clubDeportivoSeneca.modelo.turnos.SolicitudCambioTurno;
import clubDeportivoSeneca.modelo.turnos.Turno;
import clubDeportivoSeneca.modelo.usuario.Entrenador;
import clubDeportivoSeneca.modelo.usuario.Jugador;
import clubDeportivoSeneca.modelo.ventas.Bebida;
import clubDeportivoSeneca.modelo.ventas.Periodo;
import clubDeportivoSeneca.modelo.ventas.Rubro;
import clubDeportivoSeneca.modelo.ventas.Temperatura;
import clubDeportivoSeneca.modelo.ventas.VentaArticulosDeportivos;
import clubDeportivoSeneca.modelo.ventas.VentaCafeteria;
import clubDeportivoSeneca.modelo.ventas.VentasPorRubro;


public class IntegracionAdministradorTest {

    private Escenario e;
    private InventarioArticulos inv;
    private LocalDate proximaSemana;

    @BeforeEach
    public void setUp() throws Exception {
        e = new Escenario();
        inv = e.club.getInventarioArticulos();
        proximaSemana = LocalDate.now().plusWeeks(1);
    }

    @Test
    @DisplayName("A1 - El administrador consulta las existencias del inventario por ubicacion")
    public void a1ConsultarInventario() {
        assertEquals(10, (int) e.admin.verInventarioArticulos().get(InventarioArticulos.UBICACION_TIENDA).get(e.balon));
    }

    @Test
    @DisplayName("A2 - El administrador consulta el historial de reservas por fecha")
    public void a2HistorialReservas() throws Exception {
        Jugador ana = e.socio("ana", 20);
        e.club.crearReserva(ana, e.cancha, e.manana, Escenario.bloque(10, 11), 2, Modalidad.SENCILLOS);

        assertEquals(1, e.club.getInventarioInstalaciones().getHistorial().consultarPorFecha(e.manana).size());
        assertTrue(e.club.getInventarioInstalaciones().getHistorial().consultarPorFecha(LocalDate.now()).isEmpty());
    }

    @Test
    @DisplayName("A3 - El administrador consulta el historial de partidos internos e individuales")
    public void a3HistorialPartidos() throws Exception {
        Equipo a = e.equipo("Locales", e.sub15, 14, 7);
        Equipo b = e.equipo("Visitantes", e.sub15, 14, 7);
        e.club.registrarPartidoInterno(new PartidoInterno(proximaSemana, e.futbol, a, b));
        Jugador ana = e.socio("ana", 20);
        Jugador juan = e.socio("juan", 20);
        ana.asignarNivel(e.tenis, NivelJugador.INTERMEDIO);
        juan.asignarNivel(e.tenis, NivelJugador.INTERMEDIO);
        e.club.registrarPartidoIndividual(new PartidoIndividual(e.manana, e.tenis, ana, juan));

        assertEquals(1, e.club.getPartidosInternos().size());
        assertEquals(1, e.admin.verHistorialPartidosIndividuales().size());
    }

    @Test
    @DisplayName("A4 - El administrador consulta el historial de sesiones de entrenamiento")
    public void a4HistorialSesiones() throws Exception {
        Entrenador carlos = e.entrenador("carlos");
        Entrenamiento sesion = e.club.planearEntrenamiento(carlos, proximaSemana, LocalTime.of(16, 0), 60, e.cancha);
        sesion.convocar(e.socio("ana", 20));

        assertEquals(1, e.club.getEntrenamientos().size());
        assertSame(carlos, e.club.getEntrenamientos().get(0).getEntrenador());
        assertEquals(1, e.club.getEntrenamientos().get(0).getConvocados().size());
    }

    @Test
    @DisplayName("A5 - El administrador crea un equipo activo y sin jugadores")
    public void a5CrearEquipo() {
        Equipo equipo = e.admin.crearEquipo("Seneca Mayores", e.futbol, e.mayores, 18);

        assertTrue(e.club.getEquipos().contains(equipo));
        assertTrue(equipo.isActivo());
        assertTrue(equipo.getJugadores().isEmpty());
        assertThrows(IllegalArgumentException.class, () -> e.admin.crearEquipo("Sin cupo", e.futbol, e.mayores, 0));
    }

    @Test
    @DisplayName("A6 - El administrador registra un jugador que luego puede iniciar sesion")
    public void a6RegistrarJugador() {
        Jugador nuevo = e.admin.crearJugador("luis", "clave", "Luis", LocalDate.of(2001, 5, 10));

        assertSame(nuevo, e.club.iniciarSesion("luis", "clave"));
        assertTrue(e.club.consultarJugadoresActivos().contains(nuevo));
    }

    @Test
    @DisplayName("A7 - El administrador da de baja un jugador y un equipo sin borrar su historial")
    public void a7DarDeBaja() throws Exception {
        Jugador ana = e.socio("ana", 20);
        Equipo equipo = e.admin.crearEquipo("Seneca Mayores", e.futbol, e.mayores, 18);

        e.admin.darBajaJugador(ana);
        e.admin.darBajaEquipo(equipo);

        assertFalse(ana.isActivo());
        assertFalse(e.club.consultarJugadoresActivos().contains(ana));
        assertTrue(e.club.getEquipos().contains(equipo), "El equipo se conserva en el historial");
        assertThrows(Exception.class, () -> e.club.inscribirJugadorEnEquipo(equipo, e.socio("juan", 20), "Defensa", 4));
    }

    @Test
    @DisplayName("A8 - El administrador mueve articulos entre ubicaciones sin superar la existencia")
    public void a8MoverArticulos() {
        inv.agregarUbicacion("BODEGA");

        e.admin.moverArticulo(e.balon, InventarioArticulos.UBICACION_TIENDA, "BODEGA", 3);

        assertEquals(7, inv.cantidadDisponible(e.balon, InventarioArticulos.UBICACION_TIENDA));
        assertEquals(3, inv.cantidadDisponible(e.balon, "BODEGA"));
        assertThrows(IllegalStateException.class,
                () -> e.admin.moverArticulo(e.balon, InventarioArticulos.UBICACION_TIENDA, "BODEGA", 50));
    }

    @Test
    @DisplayName("A9 - El administrador reabastece la tienda")
    public void a9Reabastecer() {
        e.admin.reabastecer(e.balon, 5);

        assertEquals(15, inv.cantidadDisponible(e.balon, InventarioArticulos.UBICACION_TIENDA));
    }

    @Test
    @DisplayName("A10 - El administrador aprueba la sugerencia de un empleado")
    public void a10AprobarSugerencia() {
        Entrenador carlos = e.entrenador("carlos");
        SugerenciaProducto s = carlos.sugerirproducto("Rodillera", "Rodillera para voleibol", TipoSugerencia.ARTICULO);
        e.club.registrarSugerencia(s);

        e.admin.aprobarSugerencia(s);

        assertEquals(EstadoSugerencia.APROBADA, s.getEstado());
    }

    @Test
    @DisplayName("A11 - El administrador no aprueba un cambio de turno que deja el dia sin personal minimo")
    public void a11ResolverCambioTurno() throws Exception {
        LocalTime apertura = LocalTime.of(6, 0), cierre = LocalTime.of(22, 0);
        Entrenador e1 = e.entrenador("e1");
        Entrenador e2 = e.entrenador("e2");
        Turno turnoE1 = e.admin.asignarTurno(e1, proximaSemana, apertura, cierre);
        e.admin.asignarTurno(e2, proximaSemana, apertura, cierre);
        e.admin.asignarTurno(e.fisioterapeuta("f1"), proximaSemana, apertura, cierre);
        assertTrue(e.club.getGestorTurnos().cumpleMinimos(proximaSemana));

        SolicitudCambioTurno s = e1.solicitarCambioTurno(turnoE1, proximaSemana.plusDays(1), apertura, cierre);
        e.club.getGestorTurnos().registrarSolicitud(s);
        boolean aprobada = e.admin.aprobarCambio(s);

        assertFalse(aprobada, "Sin e1 el dia queda con un solo entrenador");
        assertEquals(proximaSemana, turnoE1.getFecha(), "El turno vuelve a su fecha original");
    }

    @Test
    @DisplayName("A12 - El administrador registra una competencia ASCUN")
    public void a12CrearCompetencia() throws Exception {
        Ascun ascun = new Ascun("ASCUN Futbol", "ASCUN", "2026-2", e.futbol, e.mayores,
                new RequisitosInscripcionCircuito(7, e.mayores));

        e.club.registrarCircuito(ascun);

        assertTrue(e.club.getCircuitos().contains(ascun));
        assertThrows(Exception.class, () -> new Ascun("Mal", "ASCUN", "2026-2", e.futbol, e.mayores,
                new RequisitosInscripcionCircuito(7, e.sub15)), "La categoria de los requisitos debe coincidir");
    }

    @Test
    @DisplayName("A13 - El reporte de ventas discrimina por rubro y periodo")
    public void a13ReporteVentas() {
        Jugador ana = e.socio("ana", 20);
        VentaArticulosDeportivos tienda = new VentaArticulosDeportivos(LocalDate.now(), ana);
        tienda.agregarArticulo(e.balon, 1, inv);
        tienda.finalizar(inv);
        e.club.registrarVenta(tienda);
        VentaCafeteria cafe = new VentaCafeteria(LocalDate.now(), ana);
        cafe.agregarBebida(new Bebida("Jugo", 5000, Temperatura.FRIA), 2);
        cafe.finalizar();
        e.club.registrarVenta(cafe);

        VentasPorRubro reporte = new VentasPorRubro(e.club.getVentas());

        assertEquals(1, reporte.generar(Rubro.TIENDA, Periodo.DIARIO, LocalDate.now()).size());
        assertEquals(1, reporte.generar(Rubro.CAFETERIA, Periodo.DIARIO, LocalDate.now()).size());
        assertDoesNotThrow(() -> e.admin.reporteVentas());
    }

    @Test
    @DisplayName("A14 - El reporte deportivo calcula la asistencia a los entrenamientos")
    public void a14ReporteDeportivo() throws Exception {
        Entrenador carlos = e.entrenador("carlos");
        Jugador ana = e.socio("ana", 20);
        Jugador juan = e.socio("juan", 20);
        Entrenamiento sesion = e.club.planearEntrenamiento(carlos, proximaSemana, LocalTime.of(16, 0), 60, e.cancha);
        sesion.convocar(ana);
        sesion.convocar(juan);
        carlos.registrarAsistencia(sesion, ana, EstadoAsistencia.ASISTIO);
        carlos.registrarAsistencia(sesion, juan, EstadoAsistencia.FALTO);

        assertEquals(50.0, sesion.getPorcentajeAsistencia(), 0.001);
        assertDoesNotThrow(() -> e.admin.reporteAsistencia());
        assertDoesNotThrow(() -> e.admin.reporteResultados());
    }
}