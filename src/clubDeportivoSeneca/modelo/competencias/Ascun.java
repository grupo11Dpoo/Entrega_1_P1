package clubDeportivoSeneca.modelo.competencias;

import java.util.List;

import clubDeportivoSeneca.modelo.deportes.CategoriaEdad;
import clubDeportivoSeneca.modelo.deportes.DeporteDeConjunto;

public class Ascun extends Circuito {
	
	private List<Fase> fases;

	public Ascun(String nombre, String entidadOrganizadora, String temporada, DeporteDeConjunto deporte,
			CategoriaEdad categoriaEdad, RequisitosInscripcionCircuito requisitosIncripcion) throws Exception {
		
		super(nombre, entidadOrganizadora, temporada, deporte, categoriaEdad, "Grupos + eliminación directa", requisitosIncripcion);
	}

	public List<Fase> getFase() {
		return fases;
	}
	
	public Fase crearFase(TipoFase tipo, String nombre) throws Exception {
		
		Fase nuevaFase = new Fase(tipo, nombre);
		
		for(Fase fase : fases) {
			if(fase.getNombre().equals(nombre)) {
				throw new Exception("Ya existe la fase " + nombre + ".");
			}
		}
		
		fases.add(nuevaFase);
		return nuevaFase;
	}
	
	@Override
	public void registrarPartido(PartidoCompetencia partido) throws Exception {
		
		if(partido == null || partido.getFase() == null || !fases.contains(partido.getFase())) {
			throw new Exception("En ASCUN todos los partidos deben pertecer a una fase.");
		}
		
		super.registrarPartido(partido);
	}
	
	public void registrarPartidoEnFase(Fase fase, PartidoCompetencia partido) throws Exception {
		
		if(!fases.contains(fase)) {
			throw new Exception("La fase no pertence a este circuito.");
		}
		
		fase.agregarPartido(partido);
	}
	
	public List<String> calcularClasificados(Fase fase) {
		//TODO:
	}
	
	public List<String> calcularClasificados(Fase fase, int cupos) {
		//TODO:
	}

	public String desdempatePorEnfrentamiento(String a, String b) {
		//TODO:
	}
	
	@Override
	public List<String> calcularClasificacion() {
		//TODO:
	}
	
	public List<String> clasificarGrupos(Fase fase) {
		//TODO:
	}
	
	public List<String> ganadoresEliminacion(Fase fase) {
		//TODO:
	}
	
	public int puntosEntre(String equipo, Set<String> grupo, List<PartidoCompetencia> partidos) {
		//TODO:
	}
}
