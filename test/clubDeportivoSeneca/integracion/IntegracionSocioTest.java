package clubDeportivoSeneca.integracion;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import clubDeportivoSeneca.modelo.entrenamiento.Entrenamiento;
import clubDeportivoSeneca.modelo.entrenamiento.EstadoAsistencia;
import clubDeportivoSeneca.modelo.equipos.Equipo;
import clubDeportivoSeneca.modelo.individual.EstadisticaIndividual;
import clubDeportivoSeneca.modelo.individual.EstadisticaTyP;
import clubDeportivoSeneca.modelo.individual.NivelJugador;
import clubDeportivoSeneca.modelo.individual.PartidoIndividual;
import clubDeportivoSeneca.modelo.individual.Ranking;
import clubDeportivoSeneca.modelo.instalaciones.Modalidad;
import clubDeportivoSeneca.modelo.instalaciones.Reserva;
import clubDeportivoSeneca.modelo.inventario.InventarioArticulos;
import clubDeportivoSeneca.modelo.usuario.Entrenador;
import clubDeportivoSeneca.modelo.usuario.Jugador;
import clubDeportivoSeneca.modelo.usuario.Usuario;
import clubDeportivoSeneca.modelo.ventas.Alergeno;
import clubDeportivoSeneca.modelo.ventas.Snack;
import clubDeportivoSeneca.modelo.ventas.VentaArticulosDeportivos;
import clubDeportivoSeneca.modelo.ventas.VentaCafeteria;


public class IntegracionSocioTest {

    private Escenario e;
    private Jugador ana;

    @BeforeEach
    public void setUp() throws Exception {
        e = new Escenario();
        ana = e.socio("ana", 14);
    }

    private Jugador prepararTenis() throws Exception {
        Jugador juan = e.socio("juan", 14);
        ana.asignarNivel(e.tenis, NivelJugador.INTERMEDIO);
        juan.asignarNivel(e.tenis, NivelJugador.INTERMEDIO);
        e.club.registrarRanking(new Ranking(ana, e.tenis));
        e.club.registrarRanking(new Ranking(juan, e.tenis));
        return juan;
    }

    private Ranking rankingDe(Jugador j) {
        return e.club.getRankings().stream().filter(r -> r.getJugador() == j).findFirst().orElseThrow();
    }

    private PartidoIndividual jugarPartido(Jugador juan) throws Exception {
        PartidoIndividual p = new PartidoIndividual(e.manana, e.tenis, ana, juan);
        e.club.registrarPartidoIndividual(p);
        p.registrarResultado(ana, new EstadisticaTyP(ana, 2, 12, 3, 5), new EstadisticaTyP(juan, 0, 7, 1, 9));
        p.actualizarRankings(rankingDe(ana), rankingDe(juan));
        return p;
    }

    @Test
    @DisplayName("G1 - Un usuario inicia sesion con su login y contrasena")
    public void g1IniciarSesion() {
        Usuario u = e.club.iniciarSesion("ana", "1234");

        assertSame(ana, u);
        assertNull(e.club.iniciarSesion("ana", "mala"), "Con contrasena errada se niega el acceso");
        assertNull(e.club.iniciarSesion("nadie", "1234"), "Con login inexistente se niega el acceso");
    }

    @Test
    @DisplayName("J1 - El socio queda inscrito en un equipo de su categoria")
    public void j1InscribirseEnEquipo() throws Exception {
        Equipo equipo = e.club.crearEquipo("Seneca Sub-15", e.futbol, e.sub15, 20);

        e.club.inscribirJugadorEnEquipo(equipo, ana, "Delantera", 9);

        assertTrue(equipo.contieneJugador(ana));
        assertEquals(equipo, ana.getEquipoPorDeporte(e.futbol));
        Jugador adulto = e.socio("pedro", 25);
        assertThrows(Exception.class, () -> e.club.inscribirJugadorEnEquipo(equipo, adulto, "Defensa", 4));
    }

    @Test
    @DisplayName("J2 - El socio se inscribe en una academia con nivel y ranking inicial de 1000")
    public void j2InscribirseEnAcademia() throws Exception {
        prepararTenis();

        assertEquals(NivelJugador.INTERMEDIO, ana.getNivelPorDeporteInd(e.tenis));
        assertEquals(1000, rankingDe(ana).getPuntaje());
    }

    @Test
    @DisplayName("J3 - El socio reserva una cancha y queda en el historial de reservas")
    public void j3ReservarInstalacion() throws Exception {
        Reserva r = e.club.crearReserva(ana, e.cancha, e.manana, Escenario.bloque(10, 11), 2, Modalidad.SENCILLOS);

        assertTrue(r.estaActiva());
        assertEquals(1, e.club.getInventarioInstalaciones().getHistorial().consultarPorJugador(ana).size());
        Jugador juan = e.socio("juan", 14);
        assertThrows(Exception.class,
                () -> e.club.crearReserva(juan, e.cancha, e.manana, Escenario.bloque(10, 11), 2, Modalidad.SENCILLOS));
    }

    @Test
    @DisplayName("J4 - El socio programa un partido de practica con alguien de su nivel")
    public void j4ProgramarPartidoPractica() throws Exception {
        Jugador juan = prepararTenis();

        PartidoIndividual p = new PartidoIndividual(e.manana, e.tenis, ana, juan);
        e.club.registrarPartidoIndividual(p);

        assertTrue(e.club.getPartidosIndividuales().contains(p));
        juan.asignarNivel(e.tenis, NivelJugador.AVANZADO);
        ana.asignarNivel(e.tenis, NivelJugador.PRINCIPIANTE);
        assertThrows(Exception.class, () -> new PartidoIndividual(e.manana, e.tenis, ana, juan));
    }

    @Test
    @DisplayName("J5 - Se registra el resultado de un partido de practica y se actualiza el ELO")
    public void j5RegistrarResultadoPractica() throws Exception {
        Jugador juan = prepararTenis();

        PartidoIndividual p = jugarPartido(juan);

        assertTrue(p.isFinalizado());
        assertEquals(1016, rankingDe(ana).getPuntaje());
        assertEquals(984, rankingDe(juan).getPuntaje());
    }

    @Test
    @DisplayName("J6 - El socio consulta sus estadisticas del partido")
    public void j6ConsultarEstadisticasPropias() throws Exception {
        Jugador juan = prepararTenis();
        PartidoIndividual p = jugarPartido(juan);

        EstadisticaIndividual mias = p.getEstadisticas().stream().filter(s -> s.getJugador() == ana).findFirst().orElseThrow();

        assertEquals(2, ((EstadisticaTyP) mias).getSetsGanados());
        assertEquals(3, ((EstadisticaTyP) mias).getAces());
    }

    @Test
    @DisplayName("J7 - El socio consulta su asistencia y la evaluacion de su entrenador")
    public void j7ConsultarAsistenciaYEvaluacion() throws Exception {
        Entrenador carlos = e.entrenador("carlos");
        Entrenamiento sesion = e.club.planearEntrenamiento(carlos, e.manana, LocalTime.of(9, 0), 60, e.cancha);
        sesion.convocar(ana);
        carlos.registrarAsistencia(sesion, ana, EstadoAsistencia.ASISTIO);
        carlos.registrarEvaluacion(sesion, ana, 4, "Buen saque");

        assertEquals(EstadoAsistencia.ASISTIO, sesion.getAsistenciaDe(ana).getEstado());
        assertEquals(4, sesion.getAsistenciaDe(ana).getEvaluacion().getNota());
        assertEquals("Buen saque", sesion.getAsistenciaDe(ana).getEvaluacion().getComentario());
    }

    @Test
    @DisplayName("J8 - El socio consulta su ranking y su posicion en la disciplina")
    public void j8ConsultarRanking() throws Exception {
        Jugador juan = prepararTenis();
        jugarPartido(juan);

        List<Ranking> tabla = e.club.getRankings().stream()
                .sorted(Comparator.comparingInt(Ranking::getPuntaje).reversed()).toList();

        assertSame(ana, tabla.get(0).getJugador(), "Ana debe quedar primera despues de ganar");
        assertTrue(rankingDe(ana).tienePartidos());
    }

    @Test
    @DisplayName("J9 - El socio compra en la tienda: se cobra IVA y se descuenta el inventario")
    public void j9ComprarEnTienda() {
        InventarioArticulos inv = e.club.getInventarioArticulos();
        VentaArticulosDeportivos venta = new VentaArticulosDeportivos(LocalDate.now(), ana);
        venta.agregarArticulo(e.balon, 2, inv);

        venta.finalizar(inv);
        e.club.registrarVenta(venta);

        assertEquals(119000, venta.getPrecioTotal());
        assertEquals(8, inv.cantidadDisponible(e.balon, InventarioArticulos.UBICACION_TIENDA));
        assertEquals(1, e.club.consultarVentas(LocalDate.now(), LocalDate.now()).size());
    }

    @Test
    @DisplayName("J10 - El socio compra en la cafeteria conociendo los alergenos")
    public void j10ComprarEnCafeteria() {
        Snack galletas = new Snack("Galletas", 3000);
        galletas.agregarAlergeno(new Alergeno("Gluten"));
        VentaCafeteria venta = new VentaCafeteria(LocalDate.now(), ana);
        venta.agregarSnack(galletas, 2);

        assertThrows(IllegalStateException.class, () -> venta.finalizar(), "Sin informar alergenos no se cierra");
        venta.confirmarAlergenos();
        venta.finalizar();
        e.club.registrarVenta(venta);

        assertEquals(6000 + 480 + 600, venta.getPrecioTotal(), "Subtotal + 8 % impuesto + 10 % propina");
    }

    @Test
    @DisplayName("J11 - Cada compra suma al socio el 2 % del total en puntos")
    public void j11AcumularPuntos() {
        VentaArticulosDeportivos venta = new VentaArticulosDeportivos(LocalDate.now(), ana);
        venta.agregarArticulo(e.balon, 2, e.club.getInventarioArticulos());

        venta.finalizar(e.club.getInventarioArticulos());

        assertEquals(2380, ana.getPuntosFidelidad(), "2 % de 119.000");
    }

    @Test
    @DisplayName("J12 - El socio redime puntos y el total baja un peso por punto")
    public void j12RedimirPuntos() {
        ana.acumularPuntos(100000);
        VentaArticulosDeportivos venta = new VentaArticulosDeportivos(LocalDate.now(), ana);
        venta.agregarArticulo(e.balon, 1, e.club.getInventarioArticulos());

        venta.aplicarPuntos(1000);
        venta.finalizar(e.club.getInventarioArticulos());

        assertEquals(1000, venta.getPuntosRedimidos());
        assertEquals(59500 - 1000, venta.getPrecioTotal());
        VentaArticulosDeportivos otra = new VentaArticulosDeportivos(LocalDate.now(), ana);
        otra.agregarArticulo(e.balon, 1, e.club.getInventarioArticulos());
        assertThrows(IllegalStateException.class, () -> otra.aplicarPuntos(10_000_000), "No se redime mas que el saldo");
    }

    @Test
    @DisplayName("J13 - El socio consulta su saldo de puntos")
    public void j13ConsultarSaldo() {
        assertEquals(0, ana.getPuntosFidelidad(), "Un socio nuevo no tiene puntos");
        VentaArticulosDeportivos venta = new VentaArticulosDeportivos(LocalDate.now(), ana);
        venta.agregarArticulo(e.balon, 1, e.club.getInventarioArticulos());

        venta.finalizar(e.club.getInventarioArticulos());

        assertTrue(ana.getPuntosFidelidad() > 0, "Despues de comprar el saldo aumenta");
    }
}