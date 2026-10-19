package clubDeportivoSeneca.modelo.competencias;

import java.time.LocalDate;

import clubDeportivoSeneca.modelo.equipos.Equipo;
import clubDeportivoSeneca.modelo.partidos.EstadisticaJugadorPartido;
import clubDeportivoSeneca.modelo.partidos.PartidoConjuntos;

public class PartidoCompetencia extends PartidoConjuntos implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private String institucionRival;
	private String escenario;
	private Circuito circuito;
	private Fase fase;
	
	public PartidoCompetencia(LocalDate fecha, Equipo equipoLocal, String institucionRival,
			String escenario, Circuito circuito) throws Exception {
		super(fecha, circuito.getDeporte(), equipoLocal);
		this.institucionRival = institucionRival;
		this.escenario = escenario;
		this.circuito = circuito;
		this.fase = null;
	}

	public String getEscenario() {
		return escenario;
	}

	public void setEscenario(String escenario) {
		this.escenario = escenario;
	}

	public Fase getFase() {
		return fase;
	}

	public void setFase(Fase fase) {
		this.fase = fase;
	}

	public String getInstitucionRival() {
		return institucionRival;
	}

	public Circuito getCircuito() {
		return circuito;
	}
	
	@Override
	public void validarRequisitos() throws Exception {
		
		InscripcionCompetencia inscripcion = circuito.getInscripcionDeEquipo(getEquipoLocal());
		
		if(inscripcion == null) {
			throw new Exception("El equipo " + getEquipoLocal().getNombre() + " no está inscrito en el circuito " + circuito);
		}
		
		int jugadoresHabilitados = inscripcion.getNominaHabilitada().size();
		int minimoJugadoresHabilitados = getDeporte().getMinJugadoresHabilitados();
		
		if(jugadoresHabilitados < minimoJugadoresHabilitados) {
			throw new Exception("La nómina tiene " + jugadoresHabilitados + " jugadores habilitados, y el " + getDeporte() + " exige mínimo " + minimoJugadoresHabilitados + " jugadores.");
		}
	}

	@Override
	public void registrarEstadistica(EstadisticaJugadorPartido estadistica) throws Exception {
		
		if(estadistica != null) {
			InscripcionCompetencia inscripcion = circuito.getInscripcionDeEquipo(getEquipoLocal());
			
			if(!inscripcion.estaEnNomina(estadistica.getJugador())) {
				throw new Exception("El jugador " + estadistica.getJugador() + "no está en la nómina de ésta competencia.");
			}
		}
		
		super.registrarEstadistica(estadistica);
		circuito.getEstadisticas().acumularEstadistica(estadistica);
	}
	
	@Override
	protected boolean seDebeSuspender(EstadisticaJugadorPartido estadistica) {
		return circuito.getEstadisticas().generaSuspension(estadistica);
	}
	
	@Override
	public void finalizarPartido() throws Exception {
		if(!circuito.getPartidos().contains(this)) {
			throw new Exception("Registre el partido con el circuito antes de finalizarlo.");
		}
		
		super.finalizarPartido();
	}
}



















