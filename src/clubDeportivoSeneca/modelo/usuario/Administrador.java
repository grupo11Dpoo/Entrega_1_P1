package clubDeportivoSeneca.modelo.usuario;

import java.time.LocalDate
import java.time.LocalTime;

import clubDeportivoSeneca.modelo.deportes.CategoriaEdad;
import clubDeportivoSeneca.modelo.deportes.DeporteDeConjunto;
import clubDeportivoSeneca.modelo.equipos.Equipo;
import clubDeportivoSeneca.modelo.inventario.Articulo;
import clubDeportivoSeneca.modelo.inventario.InventarioArticulos;
import clubDeportivoSeneca.modelo.inventario.SugerenciaProducto;
import clubDeportivoSeneca.modelo.turnos.Turno;
import clubDeportivoSeneca.modelo.turnos.SolicitudCambioTurno;
import clubDeportivoSeneca.modelo.instalaciones.InventarioInstalaciones;
import clubDeportivoSeneca.sistema.ClubDeportivoSeneca;

public class Administrador extends Usuario implements java.io.Serializable {

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
	public Turno asignarTurno(Empleado empleado, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
		return cds().getGestorTurnos().asignarTurno(empleado, fecha, horaInicio, horaFin);
	}
	
	public boolean aprobarCambio(SolicitudCambioTurno solicitud) throws Exception {
		return cds().getGestorTurnos().aprobarCambio(solicitud);
	}
	
	//Consultas y reportes:
	public InventarioInstalaciones verInventarioInstalaciones(InventarioInstalaciones instalaciones) {
		return instalaciones;
	}
	
	//TODO:
	public InventarioArticulos verInventarioArticulos(InventarioArticulos articulos) {
		return articulos;
	}
	
	//TODO:
	public void verHistorialReservas() {
		if (cds == null) return;
        cds.getInventarioInstalaciones().getHistorial().getReservas().forEach(System.out::println);
	}
		
	//TODO:
	public void verHistorialPartidos() {
			
	}
		
	//TODO:
	public void verHistorialEntrenamientos() {
			
	}
		
	//TODO:
	public void reporteVentas() {
			
	}
		
	//TODO:
	public void reporteAsistencia() {
			
	}
		
	//TODO:
	public void reporteResultados() {
			
	}
		
	//TODO:
	public void reporteRankings() {
			
	}

}




























