package clubDeportivoSeneca.modelo.competencias;

import java.util.ArrayList; 
import java.util.List;

public class Fase implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	
	private TipoFase tipo;
	private String nombre;
	private List<PartidoCompetencia> partidos;
	public Fase(TipoFase tipo, String nombre) {
		super();
		this.tipo = tipo;
		this.nombre = nombre;
		this.partidos = new ArrayList<PartidoCompetencia>();
	}
	public TipoFase getTipo() {
		return tipo;
	}
	public String getNombre() {
		return nombre;
	}
	public List<PartidoCompetencia> getPartidos() {
		return partidos;
	}

	public void agregarPartido(PartidoCompetencia partido) throws Exception {
		
		if (partido == null) {
			throw new IllegalArgumentException("Partido nulo.");
		}
        if (partido.getFase() != null) {
        	throw new IllegalStateException("El partido ya pertenece a una fase.");
        }
        
        partidos.add(partido);
        partido.setFase(this);
	}
	
	public void quitarPartido(PartidoCompetencia partido) {
        if (partidos.remove(partido)) {
        	partido.setFase(null);
        }
    }
}
