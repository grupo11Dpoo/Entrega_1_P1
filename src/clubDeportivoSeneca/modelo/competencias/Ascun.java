package clubDeportivoSeneca.modelo.competencias;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.HashMap;

import clubDeportivoSeneca.modelo.deportes.CategoriaEdad;
import clubDeportivoSeneca.modelo.deportes.DeporteDeConjunto;

public class Ascun extends Circuito implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
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
        return calcularClasificados(fase, 2);
	}
	
	public List<String> calcularClasificados(Fase fase, int cupos) {
        if (fase == null || cupos <= 0) return new ArrayList<>();
        return clasificarGrupos(fase).stream().limit(cupos).toList();
	}

	public String desdempatePorEnfrentamiento(String a, String b) {
        for (Fase fase : fases) for (PartidoCompetencia p : fase.getPartidos()) {
            if (p.isFinalizado() && ((p.getEquipoLocal().getNombre().equals(a) && p.getInstitucionRival().equals(b)) ||
                    (p.getEquipoLocal().getNombre().equals(b) && p.getInstitucionRival().equals(a)))) {
                if (p.getMarcadorLocal() > p.getMarcadorVisitante()) return p.getEquipoLocal().getNombre();
                if (p.getMarcadorLocal() < p.getMarcadorVisitante()) return p.getInstitucionRival();
            }
        }
        return null;
	}
	
	@Override
	public List<String> calcularClasificacion() {
		for (int i = fases.size() - 1; i >= 0; i--) {
			Fase f = fases.get(i);
	        for (PartidoCompetencia p : f.getPartidos()) {
	            if (p.isFinalizado()) return calcularClasificados(f);
	        }
	    }
	    return new ArrayList<>();
	}
	
	public List<String> clasificarGrupos(Fase fase) {
        if (fase == null) return new ArrayList<>();
        Map<String, Integer> puntos = new HashMap<>();
        for (PartidoCompetencia p : fase.getPartidos()) {
            if (!p.isFinalizado()) continue;
            String local = p.getEquipoLocal().getNombre(), rival = p.getInstitucionRival();
            int[] pts = calcularPuntos(p);
            puntos.merge(local, pts[0], Integer::sum); puntos.merge(rival, pts[1], Integer::sum);
        }
        List<String> equipos = new ArrayList<>(puntos.keySet());
        equipos.sort((a,b) -> Integer.compare(puntos.get(b), puntos.get(a)));
        return equipos;
	}
	
	public List<String> ganadoresEliminacion(Fase fase) throws Exception {
		
	    List<String> ganadores = new ArrayList<>();
	    
        for (PartidoCompetencia partido : fase.getPartidos()) {
            if (!partido.isFinalizado()) {
            	
            	if (partido.getMarcadorLocal() == partido.getMarcadorVisitante()) {
                    throw new Exception("No se pueden tener empate en partidos de eliminación directa.");
                }
            	
            	if(partido.getMarcadorLocal() > partido.getMarcadorVisitante()) {
            		ganadores.add(partido.getEquipoLocal().getNombre());
            		
            	} else {
            		ganadores.add(partido.getInstitucionRival());
            	}
            }
            
        }
        return ganadores;
	}
	
	private int[] calcularPuntos(PartidoCompetencia partido) {
        try {
            return getDeporte().calcularPuntosLiga(partido.getMarcadorLocal(), partido.getMarcadorVisitante());
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo calcular la puntuación del partido.", e);
        }
    }
	
	public int puntosEntre(String equipo, Set<String> grupo, List<PartidoCompetencia> partidos) throws Exception {
		int total = 0;
        for (PartidoCompetencia partido : partidos) {
            if (!partido.isFinalizado()) continue;
            int[] puntos = getDeporte().calcularPuntosLiga(partido.getMarcadorLocal(), partido.getMarcadorVisitante());
            String local = partido.getEquipoLocal().getNombre();
            String rival = partido.getInstitucionRival();
            if (local.equals(equipo) && grupo.contains(rival)) total += puntos[0];
            else if (rival.equals(equipo) && grupo.contains(local)) total += puntos[1];
        }
        return total;
	}
}
