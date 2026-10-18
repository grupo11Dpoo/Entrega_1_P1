package clubDeportivoSeneca.modelo.partidos;

import java.time.LocalDate;
import java.util.List;

import clubDeportivoSeneca.modelo.deportes.DeporteDeConjunto;
import clubDeportivoSeneca.modelo.equipos.Equipo;

public class PartidoInterno extends PartidoConjuntos implements java.io.Serializable{
	
	private Equipo equipoVisitante;

	public PartidoInterno(LocalDate fecha, DeporteDeConjunto deporte, Equipo equipoLocal, Equipo equipoVisitante) throws Exception {
		
		super(fecha, deporte, equipoLocal);
		
		if(equipoVisitante == null) {
			throw new Exception("Equipo visitante nulo.");
		}
		
		validarRequisitos();
		
		this.equipoVisitante = equipoVisitante;
	}

	public Equipo getEquipoVisitante() {
		return equipoVisitante;
	}

	public void setEquipoVisitante(Equipo equipoVisitante) {
		this.equipoVisitante = equipoVisitante;
	}
	
    @Override
	public void validarRequisitos() throws Exception {
		
    	Equipo local = getEquipoLocal();
    	
    	if(local.equals(equipoVisitante)) {
    		throw new Exception("Un equipo no puede jugar contra sí mismo.");
    	}
    	
    	if(!equipoVisitante.getDeporte().equals(getDeporte())) {
    		throw new Exception("Los equipos deben pertenecer al mismo deporte.");
    	}
    	
    	if(!equipoVisitante.getCategoriaEdad().equals(equipoVisitante.getCategoriaEdad())) {
    		throw new Exception("Los equipos deben jugar en la misma categoría de edad.");
    	}
    	
    	if(!local.isActivo() || !equipoVisitante.isActivo()) {
    		throw new Exception("Ambos equipos deben estar activos");
    	}
    	
    	if(!local.puedeJugar()) {
    		throw new Exception("El equipo " + local.getNombre() 
    		+ " no tiene el mínimo de jugadores habilitados para jugar.");
    	}
    	
    	if(!equipoVisitante.puedeJugar()) {
    		throw new Exception("El equipo " + equipoVisitante.getNombre() 
    		+ " no tiene el mínimo de jugadores habilitados para jugar.");
    	}
	}
	
    @Override
    protected List<Equipo> getEquiposDelClub(){
    	return List.of(getEquipoLocal(), equipoVisitante);
    }
    
	public Equipo getGanador() throws Exception {
		
		ResultadoPartido resultado = getResultadoLocal();
		
		if(resultado.equals(ResultadoPartido.VICTORIA_LOCAL)) {
			return getEquipoLocal();
		} else if(resultado.equals(ResultadoPartido.DERROTA_LOCAL)) {
			return equipoVisitante;
		}
		
		return null;
	}

}
