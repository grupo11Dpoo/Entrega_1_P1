package clubDeportivoSeneca.modelo.competencias;

import java.util.List;

import clubDeportivoSeneca.modelo.deportes.CategoriaEdad;
import clubDeportivoSeneca.modelo.equipos.Equipo;
import clubDeportivoSeneca.modelo.usuario.Jugador;

public class RequisitosInscripcionCircuito {
	
	private int nominaMinimaJugadores;
	private CategoriaEdad categoriaPermitida;
	
	public RequisitosInscripcionCircuito(int nominaMinimaJugadores, CategoriaEdad categoriaPermitida) {

		this.nominaMinimaJugadores = nominaMinimaJugadores;
		this.categoriaPermitida = categoriaPermitida;
	}

	public int getNominaMinimaJugadores() {
		return nominaMinimaJugadores;
	}

	public void setNominaMinimaJugadores(int nominaMinimaJugadores) {
		this.nominaMinimaJugadores = nominaMinimaJugadores;
	}

	public CategoriaEdad getCategoriaPermitida() {
		return categoriaPermitida;
	}

	public void setCategoriaPermitida(CategoriaEdad categoriaPermitida) {
		this.categoriaPermitida = categoriaPermitida;
	}
	
	public String motivoIncumplimiento(Equipo equipo, List<Jugador> nomina, Circuito circuito) {
		
		if(!equipo.isActivo()) {
			return "El equipo está dado de baja.";
		}
		
		if(!equipo.getCategoriaEdad().equals(categoriaPermitida)) {
			return "La categoría " + equipo.getCategoriaEdad() + " no está permitida (se exige " + categoriaPermitida + ").";
		}
		
		int habilitados = 0;
		
		for(Jugador jugador : nomina) {
			if(equipo.contieneJugador(jugador) && jugador.isActivo() && !jugador.estaSuspendido(circuito)) {
				habilitados ++;
			}
		}
		
		if(habilitados < nominaMinimaJugadores) {
			return "La nómina tiene " + habilitados + " jugadores habilitados y se exigen mínimo " + nominaMinimaJugadores + " jugadores.";
		}
		
		return null;
		
	}
	
	public boolean cumpleRequisitos(Equipo equipo, List<Jugador> nominaJugadores, Circuito circuito) {
		
		return motivoIncumplimiento(equipo, nominaJugadores, circuito) == null;
	}

}
























