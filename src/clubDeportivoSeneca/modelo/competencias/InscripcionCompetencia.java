package clubDeportivoSeneca.modelo.competencias;

import java.util.ArrayList;
import java.util.List;

import clubDeportivoSeneca.modelo.equipos.Equipo;
import clubDeportivoSeneca.modelo.usuario.Jugador;

public class InscripcionCompetencia implements java.io.Serializable{
	
	private Equipo equipo;
	private Circuito circuito;
	private List<Jugador> nomina;
	
	public InscripcionCompetencia(Equipo equipo, Circuito circuito, List<Jugador> jugadores) {

		this.equipo = equipo;
		this.circuito = circuito;
		this.nomina = jugadores;
	}

	public Equipo getEquipo() {
		return equipo;
	}

	public Circuito getCircuito() {
		return circuito;
	}

	public List<Jugador> getNomina() {
		return nomina;
	}
	
	public void agregarANomina(Jugador jugador) throws Exception {
		
		if(jugador == null) {
			throw new Exception("Jugador nulo");
		}
		
		if(!equipo.contieneJugador(jugador)) {
			throw new Exception(jugador.getNombre() + " no pertence a " + equipo.getNombre());
		}
		
		if(nomina.contains(jugador)) {
			throw new Exception(jugador.getNombre() + " ya está en la nómina.");
		}
		
		nomina.add(jugador);
	}
	
	public void quitarDeNomina(Jugador jugador) throws Exception {
		
		if(!nomina.contains(jugador)) {
			throw new Exception(jugador.getNombre() + " no está en la nómina.");
		}
		
		nomina.remove(jugador);
	}
	
	public boolean estaEnNomina(Jugador jugador) {
		
		return nomina.contains(jugador);
	}
	
	public List<Jugador> getNominaHabilitada() {
		
		List<Jugador> lista = new ArrayList<Jugador>();
		
		for(Jugador jugador : nomina) {
			if(equipo.contieneJugador(jugador) && jugador.isActivo() && !jugador.estaSuspendido(circuito)) {
				lista.add(jugador);
			}
		}
		
		return lista;
	}

}




















