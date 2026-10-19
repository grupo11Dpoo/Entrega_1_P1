package clubDeportivoSeneca.modelo.usuario;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import clubDeportivoSeneca.modelo.deportes.CategoriaEdad;
import clubDeportivoSeneca.modelo.deportes.DeporteDeConjunto;
import clubDeportivoSeneca.modelo.entrenamiento.Entrenamiento;
import clubDeportivoSeneca.modelo.equipos.Equipo;
import clubDeportivoSeneca.modelo.individual.PartidoIndividual;
import clubDeportivoSeneca.modelo.inventario.Articulo;
import clubDeportivoSeneca.modelo.inventario.InventarioArticulos;
import clubDeportivoSeneca.modelo.inventario.SugerenciaProducto;
import clubDeportivoSeneca.modelo.partidos.PartidoConjuntos;
import clubDeportivoSeneca.modelo.partidos.PartidoInterno;
import clubDeportivoSeneca.modelo.turnos.Turno;
import clubDeportivoSeneca.modelo.ventas.Periodo;
import clubDeportivoSeneca.modelo.ventas.Rubro;
import clubDeportivoSeneca.modelo.ventas.VentaServiciosAdicionales;
import clubDeportivoSeneca.modelo.turnos.SolicitudCambioTurno;
import clubDeportivoSeneca.modelo.instalaciones.InventarioInstalaciones;
import clubDeportivoSeneca.modelo.instalaciones.Reserva;
import clubDeportivoSeneca.sistema.ClubDeportivoSeneca;

public class Administrador extends Usuario implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	private ClubDeportivoSeneca cds;
	
	public Administrador(String login, String password) {
		super(login, password);
	}
	
	 public ClubDeportivoSeneca getClub() { 
		 return cds; 
	 }
	 
	 public void setClub(ClubDeportivoSeneca cds) { 
	    this.cds = cds; 
	 }
	 
	 public ClubDeportivoSeneca cds() {
	    
		 if (cds == null) {
			 throw new IllegalStateException("El administrador no está asociado a un club");
		 }
	    return cds;
	 }
	
	//Equipos y jugadores
	public Equipo crearEquipo(String nombre, DeporteDeConjunto deporte, CategoriaEdad categoriaEdad, int cupo) {
		return cds().crearEquipo(nombre, deporte, categoriaEdad, cupo);
		
	}

	public void darBajaEquipo(Equipo equipo) {
		cds().darBajaEquipo(equipo);	
	}
	
	public Jugador crearJugador(String login, String password, String nombre, LocalDate fechaNacimiento) {
		
		return cds().crearJugador(login, password, nombre, fechaNacimiento);
	}
	
	public void darBajaJugador(Jugador jugador) {
		cds().darBajaJugador(jugador);
	}
	
	//Inventario
	public void moverArticulo(Articulo articulo, String origen, String destino, int cantidad) {
		cds().getInventarioArticulos().mover(articulo, origen, destino, cantidad);
	}
	
	public void reabastecer(Articulo articulo, int cantidad) {
		cds().getInventarioArticulos().reabastecer(articulo, cantidad, "TIENDA");
	}
	
	public void aprobarSugerencia(SugerenciaProducto sugerencia) {
		sugerencia.aprobar();
	}
	
	//Turnos
	public Turno asignarTurno(Empleado empleado, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) throws Exception {
		return cds().getGestorTurnos().asignarTurno(empleado, fecha, horaInicio, horaFin);
	}
	
	public boolean aprobarCambio(SolicitudCambioTurno solicitud) throws Exception {
		return cds().getGestorTurnos().aprobarCambio(solicitud);
	}
	
	public void rechazarCambioTurno(SolicitudCambioTurno solicitud) {
        cds().getGestorTurnos().rechazarCambio(solicitud);
    }
	
	//Consultas
	public InventarioInstalaciones verInventarioInstalaciones(InventarioInstalaciones instalaciones) {
        return instalaciones;
	}
	
	public Map<String, Map<Articulo, Integer>> verInventarioArticulos() {
        return cds.getInventarioArticulos().getStock();
    }
	
	public void verHistorialReservas() {
        if (cds == null) return;
        cds.getInventarioInstalaciones().getHistorial().getReservas().forEach(System.out::println);
	}
		
	public void verHistorialPartidos() {
        if (cds == null) return;
        cds.getPartidosInternos().forEach(System.out::println);
        cds.getPartidosCompetencia().forEach(System.out::println);
        cds.getPartidosIndividuales().forEach(System.out::println);
	}
 
    public List<PartidoIndividual> verHistorialPartidosIndividuales() {
        return cds().getPartidosIndividuales();
    }
    
	public void verHistorialEntrenamientos() {
        if (cds == null) return;
        cds.getEntrenamientos().forEach(System.out::println);
	}
	
    //Reportes
	
	public void reporteVentas() {
        if (cds == null) return;
        java.util.Map<clubDeportivoSeneca.modelo.ventas.Rubro, Integer> totales = new java.util.EnumMap<>(clubDeportivoSeneca.modelo.ventas.Rubro.class);
        for (clubDeportivoSeneca.modelo.ventas.VentaServiciosAdicionales venta : cds.getVentas()) {
            if (venta.isFinalizada()) totales.merge(venta.getRubro(), venta.getPrecioTotal(), Integer::sum);
        }
        for (clubDeportivoSeneca.modelo.ventas.Rubro rubro : clubDeportivoSeneca.modelo.ventas.Rubro.values())
            System.out.println(rubro + ": $" + totales.getOrDefault(rubro, 0));
	}
	
	public void reporteAsistencia() {
        if (cds == null) return;
        for (clubDeportivoSeneca.modelo.entrenamiento.Entrenamiento entrenamiento : cds.getEntrenamientos()) {
            System.out.println("Entrenamiento " + entrenamiento.getFecha() + " - " + entrenamiento.getAsistencias().size() + " registros de asistencia");
        }
	}
	
	public void reporteResultados() {
        if (cds == null) return;
        cds.getPartidosInternos().forEach(p -> System.out.println(p.getFecha() + " | " + p.getEquipoLocal().getNombre() + " vs " + p.getEquipoVisitante().getNombre() + " | " + p.getMarcadorLocal() + "-" + p.getMarcadorVisitante()));
        cds.getPartidosCompetencia().forEach(p -> System.out.println(p.getFecha() + " | " + p.getEquipoLocal().getNombre() + " vs " + p.getInstitucionRival() + " | " + p.getMarcadorLocal() + "-" + p.getMarcadorVisitante()));
	}
		
	public void reporteRankings() {
        if (cds == null) return;
        cds.getRankings().forEach(System.out::println);
	}

}

