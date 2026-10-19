package clubDeportivoSeneca.modelo.partidos;

import java.time.LocalDate; 
import java.util.ArrayList;
import java.util.List;

import clubDeportivoSeneca.modelo.competencias.Circuito;
import clubDeportivoSeneca.modelo.deportes.DeporteDeConjunto;
import clubDeportivoSeneca.modelo.equipos.Equipo;
import clubDeportivoSeneca.modelo.usuario.Jugador;

public abstract class PartidoConjuntos implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	
	private LocalDate fecha;
	private DeporteDeConjunto deporte;
	private Equipo equipoLocal;
	private int marcadorLocal;
	private int marcadorVisitante;
	private boolean marcadorRegistrado;
	private boolean finalizado;
	private List<EstadisticaJugadorPartido> estadisticas;
	
	public PartidoConjuntos(LocalDate fecha, DeporteDeConjunto deporte, Equipo equipoLocal) throws Exception {
		
		if(fecha == null || deporte == null || equipoLocal == null) {
			throw new Exception("Fecha, deporte y equipo local son obligatorios.");
		}
		
		if (!List.of("Fútbol", "Baloncesto", "Voleibol").contains(deporte.getNombre())) {
            throw new Exception("El deporte " + deporte + " no es válido.");
		}
		
		if(!equipoLocal.getDeporte().equals(deporte)) {
			throw new Exception("Este equipo local no juega " + deporte + ".");
		}

		this.fecha = fecha;
		this.deporte = deporte;
		this.equipoLocal = equipoLocal;
		this.marcadorLocal = 0;
		this.marcadorVisitante = 0;
		this.marcadorRegistrado = false;
		this.finalizado = false;
		this.estadisticas = new ArrayList<EstadisticaJugadorPartido>();
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) throws Exception {
		if(fecha == null) {
			throw new Exception("Fecha nula.");
		}
		this.fecha = fecha;
	}

	public DeporteDeConjunto getDeporte() {
		return deporte;
	}

	public Equipo getEquipoLocal() {
		return equipoLocal;
	}

	public int getMarcadorLocal() {
		return marcadorLocal;
	}
	
	public int getMarcadorVisitante() {
		return marcadorVisitante;
	}
	
	public boolean isMarcadorRegistrado() {
		return marcadorRegistrado;
	}

	public boolean isFinalizado() {
		return finalizado;
	}

	public List<EstadisticaJugadorPartido> getEstadisticas() {
		return estadisticas;
	}
	
	protected List<Equipo> getEquiposDelClub(){
		return List.of(equipoLocal);
	}
	
	protected boolean seDebeSuspender(EstadisticaJugadorPartido estadistica) {
		return estadistica.generaSuspension();
	}
	
	public void registrarMarcador(int local, int visitante) throws Exception {
		partidoAbierto();
		if(!deporte.isPermiteEmpate() && local == visitante) {
			throw new Exception("El deporte" + deporte + " no permite empates.");
		}
		this.marcadorLocal = local;
		this.marcadorVisitante = visitante;
		this.marcadorRegistrado = true;
	}
	
	public void registrarEstadistica(EstadisticaJugadorPartido estadistica) throws Exception {
		partidoAbierto();
		if(estadistica == null || estadistica.getPartido() != this) {
			throw new Exception("La estadística no corresponde a este partido.");
		}
		
		Jugador jugador = estadistica.getJugador();
		if(!perteneceAEquipoDelClub(jugador)) {
			throw new Exception(jugador.getNombre() + "no pertenece a un equipo de este partido.");
		}
		
		if(jugador.estaSuspendido(getCircuito())) {
			throw new Exception(jugador.getNombre() + " está suspendido.");
		}
		
		if(getEstadisticasJugador(jugador) != null) {
			throw new Exception("Ya hay una estadística registrada para " + jugador.getNombre() + ".");
		}
		
		estadisticas.add(estadistica);
	}
	
	public void finalizarPartido() throws Exception {
		partidoAbierto();
		if(!marcadorRegistrado) {
			throw new Exception("Falta registrar el marcador final.");
		}
		
		for(Equipo equipo : getEquiposDelClub()) {
			for(Jugador jugador : equipo.getJugadores()) {
				for(Sancion sancion : jugador.getSanciones()) {
					if(sancion.estaVigente() && sancion.aplicaCircuito(getCircuito())) {
						sancion.cumplir();
					}
				}
			}
		}
		
		for(EstadisticaJugadorPartido estadistica : estadisticas) {
			if(seDebeSuspender(estadistica)) {
				Sancion nuevaSancion = new Sancion(estadistica.getJugador(), this, getCircuito(), "Tarjetas en el partido de " + fecha);
				estadistica.getJugador().agregarSancion(nuevaSancion);
			}
		}
		
		finalizado = true;
	}
	
	public ResultadoPartido getResultadoLocal() throws Exception {
		
		if(!marcadorRegistrado) {
			throw new Exception("No hay marcador registrado.");
		}
		
		if(marcadorLocal > marcadorVisitante) {
			return ResultadoPartido.VICTORIA_LOCAL;
		}
		
		if(marcadorLocal < marcadorVisitante) {
			return ResultadoPartido.DERROTA_LOCAL;
		}
		
		return ResultadoPartido.EMPATE;
	}
	
	public EstadisticaJugadorPartido getEstadisticasJugador(Jugador jugador) {
		for(EstadisticaJugadorPartido estadistica : estadisticas) {
			if(estadistica.getJugador().equals(jugador)) {
				return estadistica;
			}
		}
		
		return null;
	}
	
	private boolean perteneceAEquipoDelClub(Jugador jugador) {
		for(Equipo equipo : getEquiposDelClub()) {
			if(equipo.contieneJugador(jugador)) {
				return true;
			}
		}
		
		return false;
	}
	
	protected Circuito getCircuito() {
		return null;
	}
	
	private void partidoAbierto() throws Exception {
		if(finalizado) {
			throw new Exception("El partido ya finalizó.");
		}
	}
	
	public abstract void validarRequisitos() throws Exception;

}

