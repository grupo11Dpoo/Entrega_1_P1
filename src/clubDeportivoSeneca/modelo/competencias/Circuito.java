package clubDeportivoSeneca.modelo.competencias;

import java.util.ArrayList;
import java.util.List;

import clubDeportivoSeneca.modelo.deportes.CategoriaEdad;
import clubDeportivoSeneca.modelo.deportes.DeporteDeConjunto;
import clubDeportivoSeneca.modelo.equipos.Equipo;
import clubDeportivoSeneca.modelo.usuario.Jugador;

public abstract class Circuito {
	
	private String nombre;
	private String entidadOrganizadora;
	private String temporada;
	private DeporteDeConjunto deporte;
	private CategoriaEdad categoriaEdad;
	private String formato;
	private RequisitosInscripcionCircuito requisitosInscripcion;
	private List<InscripcionCompetencia> inscritos;
	private List<PartidoCompetencia> partidos;
	private EstadisticasCompetencia estadisticas;
	
	protected Circuito(String nombre, String entidadOrganizadora, String temporada, DeporteDeConjunto deporte,
			CategoriaEdad categoriaEdad, String formato, RequisitosInscripcionCircuito requisitosInscripcion) 
			throws Exception {
		
		if(nombre == null || entidadOrganizadora == null || temporada == null || formato == null ||
				deporte == null || categoriaEdad == null) {
			throw new Exception("Todos los campos son obligatorios.");
		}
		
		verificarRequisitos(requisitosInscripcion, categoriaEdad);

		this.nombre = nombre;
		this.entidadOrganizadora = entidadOrganizadora;
		this.temporada = temporada;
		this.deporte = deporte;
		this.categoriaEdad = categoriaEdad;
		this.formato = formato;
		this.requisitosInscripcion = requisitosInscripcion;
		this.inscritos = new ArrayList<InscripcionCompetencia>();
		this.partidos = new ArrayList<PartidoCompetencia>();
		this.estadisticas = null;
	}

	public RequisitosInscripcionCircuito getRequisitosInscripcion() {
		return requisitosInscripcion;
	}

	public void setRequisitosInscripcion(RequisitosInscripcionCircuito requisitosInscripcion) {
		this.requisitosInscripcion = requisitosInscripcion;
	}

	public String getNombre() {
		return nombre;
	}
	
	public String getEntidadOrganizadora() {
		return entidadOrganizadora;
	}

	public String getTemporada() {
		return temporada;
	}

	public DeporteDeConjunto getDeporte() {
		return deporte;
	}

	public CategoriaEdad getCategoriaEdad() {
		return categoriaEdad;
	}

	public String getFormato() {
		return formato;
	}

	public List<InscripcionCompetencia> getInscritos() {
		return inscritos;
	}

	public List<PartidoCompetencia> getPartidos() {
		return partidos;
	}

	public EstadisticasCompetencia getEstadisticas() {
		return estadisticas;
	}
	
	public InscripcionCompetencia getInscripcionDeEquipo(Equipo equipo) {
		
		for(InscripcionCompetencia equipoInscrito : inscritos) {
			if(equipoInscrito.getEquipo().equals(equipo)) {
				return equipoInscrito;
			}
		}
		
		return null;
	}
	
	public InscripcionCompetencia inscribirEquipo(Equipo equipo, List<Jugador> nomina) throws Exception {
		
		if(equipo == null) {
			throw new Exception("Equipo nulo.");
		}
		
		if(!equipo.getDeporte().equals(deporte)) {
			throw new Exception("Este circuito es de " + deporte + ", el equipo " + " juega" + equipo.getDeporte() + ".");
		}
		
		if(getInscripcionDeEquipo(equipo) != null) {
			throw new Exception("Este equipo ya está inscrito.");
		}
		
		InscripcionCompetencia nuevaInscripcion = new InscripcionCompetencia(equipo, this, nomina);
		
		String motivo = requisitosInscripcion.motivoIncumplimiento(equipo, nuevaInscripcion.getNomina(), this);
		
		if(motivo != null) {
			throw new Exception(motivo);
		}
		
		return nuevaInscripcion;
	}

	public void registrarPartido(PartidoCompetencia partido) throws Exception {
		
		if(partido == null || !this.equals(partido.getCircuito())) {
			throw new Exception("El partido no pertenece a este circuito.");
		}
		
		if(getInscripcionDeEquipo(partido.getEquipoLocal()) == null) {
			throw new Exception("El equipo del partido no está inscrito en este circuito.");
		}
		
		if(partidos.contains(partido)) {
			throw new Exception("Este equipo ya está inscrito en este circuito.");
		}
		
		partidos.add(partido);
	}
	
	public boolean puedeJugar(Jugador jugador) {
		
		for(InscripcionCompetencia inscripcion : inscritos) {
			if(inscripcion.estaEnNomina(jugador) && inscripcion.getEquipo().contieneJugador(jugador)) {
				
				return jugador.isActivo() && !jugador.estaSuspendido(this);
			}
		}
		
		return false;
	}
	
	public List<Jugador> getClasificacionGoleador(){
		
		return estadisticas.getClasificacionGoleadores();
	}
	
	public Jugador getJugadorMasDestacado() {
		
		return estadisticas.getJugadorDestacado();
		
	}
	
	public abstract List<String> calcularClasificacion();
	
	public void verificarRequisitos(RequisitosInscripcionCircuito requisitos, 
			CategoriaEdad categoriaEdad) throws Exception {
		
		if(requisitos == null) {
			throw new Exception("Los requisitos son obligatorios.");
		}
		
		if(!requisitos.getCategoriaPermitida().equals(categoriaEdad)) {
			throw new Exception("La categoría de edad permitida por los requisitos no conicide con la del circuito.");
		}
	}
	
	@Override
	public boolean equals(Object objeto) {
		return objeto instanceof Circuito circuito && nombre.equals(circuito.nombre) && temporada.equals(circuito.temporada);
	}
	
	@Override
	public String toString() {
		return nombre + " " + temporada;
	}

}


















