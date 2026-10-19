package clubDeportivoSeneca.modelo.competencias;

import java.util.ArrayList;
import java.util.List;

import clubDeportivoSeneca.modelo.deportes.CategoriaEdad;
import clubDeportivoSeneca.modelo.deportes.DeporteDeConjunto;

public class TorneoCerros extends Circuito implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	
	private EstadisticasLiga estadisticasLiga;
	private List<PartidoCompetencia> partidosContados;

	public TorneoCerros(String nombre, String entidadOrganizadora, String temporada, DeporteDeConjunto deporte,
			CategoriaEdad categoriaEdad, RequisitosInscripcionCircuito requisitosIncripcion) throws Exception {
		
		super(nombre, entidadOrganizadora, temporada, deporte, categoriaEdad, "Liga todos contra todos.", requisitosIncripcion);
		
		this.estadisticasLiga = new EstadisticasLiga();;
		this.partidosContados = new ArrayList<PartidoCompetencia>();
	}

	public EstadisticasLiga getEstadisticasliga() {
		return estadisticasLiga;
	}
	
	@Override
	public void registrarPartido(PartidoCompetencia partido) throws Exception {
		if(partido != null && partido.getFase() != null) {
			throw new Exception("Una liga no tiene fases.");
		}
		
		super.registrarPartido(partido);
	}
	
	public void registrarResultado(PartidoCompetencia partido) throws Exception{
		
		if(partido == null || !getPartidos().contains(partido)) {
			throw new Exception("El partido no  está registrado en este torneo.");
		}
		
		if(!partido.isFinalizado()) {
			throw new Exception("El partido no ha finalizado.");
		}
		
		if(partidosContados.contains(partido)) {
			throw new Exception("El partido ya fue contado.");
		}
		
		int[] puntos = getDeporte().calcularPuntosLiga(partido.getMarcadorLocal(), partido.getMarcadorVisitante());
		
		estadisticasLiga.registrarResultado(partido.getEquipoLocal().getNombre(), puntos[0]);
		estadisticasLiga.registrarResultado(partido.getInstitucionRival(), puntos[1]);
		partidosContados.add(partido);
	}
	
	public void registrarResultadoExterno(String equipoA, int marcadorA, String equipoB, int marcadorB) throws Exception {
		
		if(equipoA == null || equipoB == null || equipoA.equals(equipoB)) {
			throw new Exception("Los dos equipos son obligatorios y no pueden ser iguales.");
		}
		
		int[] puntos = getDeporte().calcularPuntosLiga(marcadorA, marcadorB);
		estadisticasLiga.registrarResultado(equipoA, puntos[0]);
		estadisticasLiga.registrarResultado(equipoB, puntos[1]);
	}

	public List<String> calcularClasificacion() {
		List<String> resultado = new ArrayList<>();
        if (estadisticasLiga != null) {
            estadisticasLiga.getPuntos().entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .forEach(e -> resultado.add(e.getKey() + ": " + e.getValue() + " puntos"));
        }
        return resultado;	
	}

}
