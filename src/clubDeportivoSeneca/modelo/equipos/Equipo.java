package clubDeportivoSeneca.modelo.equipos;

import java.util.ArrayList;
import java.util.List;

import clubDeportivoSeneca.modelo.competencias.Circuito;
import clubDeportivoSeneca.modelo.deportes.CategoriaEdad;
import clubDeportivoSeneca.modelo.deportes.DeporteDeConjunto;
import clubDeportivoSeneca.modelo.usuario.Jugador;

public class Equipo implements java.io.Serializable {
	
	private String nombre;
	private DeporteDeConjunto deporte;
	private CategoriaEdad categoriaEdad;
	private int cupoMaximo;
	private boolean activo;
	private List<Miembro> miembros;
	
	public Equipo(String nombre, DeporteDeConjunto deporte, CategoriaEdad categoriaEdad, int cupoMaximo) {
		super();
		this.nombre = nombre;
		this.deporte = deporte;
		this.categoriaEdad = categoriaEdad;
		this.cupoMaximo = cupoMaximo;
        this.activo = true;
		this.miembros = new ArrayList<Miembro>();
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getCupoMaximo() {
		return cupoMaximo;
	}

	public void setCupoMaximo(int cupoMaximo) {
		this.cupoMaximo = cupoMaximo;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	public DeporteDeConjunto getDeporte() {
		return deporte;
	}

	public CategoriaEdad getCategoriaEdad() {
		return categoriaEdad;
	}

	public List<Miembro> getMiembros() {
		return miembros;
	}
	
	public Miembro inscribirJugador(Jugador jugador, String posicion, int numeroCamiseta) throws Exception {
		
		if(jugador == null) {
			throw new Exception("Jugador nulo. No hay jugador para inscribir.");
		}
		
		if(posicion == null) {
			throw new Exception("La posicion es obligatoria.");
		}
		
		if(!activo) {
			throw new Exception("El equipo no esta activo actualmente.");
		}
		
		if(!jugador.isActivo()) {
			throw new Exception("El jugador está suspendido.");
		}
		
		if(contieneJugador(jugador)) {
			throw new Exception("Este jugador ya está en el equipo.");
		}
		
		if(miembros.size() == cupoMaximo) {
			throw new Exception("El equipo ya está lleno.");
		}
		
		int edad = jugador.calcularEdad();
		
		if(!categoriaEdad.aceptaEdad(edad)) {
			throw new Exception("La edad '" + edad + "' no corresponde a la categoria '" + categoriaEdad + "'.");
		}
		
		if(jugador.getEquipoPorDeporte(deporte) != null) {
			throw new Exception("El jugador ya pertenece a este equipo.");
		}
		
		if(!numeroCamisetaDisponible(numeroCamiseta)) {
			throw new Exception("El número de camiseta '" + numeroCamiseta + "' ya está ocupado.");
		}
		
		Miembro nuevoMiembro = new Miembro(jugador, this, posicion, numeroCamiseta);
		miembros.add(nuevoMiembro);
		jugador.agregarMembresia(nuevoMiembro);
		return nuevoMiembro;
	}
	
	public void retirarJugador(Jugador jugador) throws Exception {
		
		Miembro miembro = buscar(jugador);
		
		if(miembro == null) {
			throw new Exception("Este jugador no pertence al equipo.");
		}
		
		miembros.remove(miembro);
		jugador.retirarMembresia(miembro);
	}
	
	public void cambiarNumeroCamiseta(Jugador jugador, int nuevoNumero) throws Exception {
		
		Miembro miembro = buscar(jugador);
		
		if(miembro == null) {
			throw new Exception("El jugador no pertenece a este equipo.");
		}
		
		if(nuevoNumero != miembro.getNumeroCamiseta() && !numeroCamisetaDisponible(nuevoNumero)) {
			throw new Exception("La camiseta " + nuevoNumero + " ya está ocupada.");
		}
		
		miembro.setNumeroCamiseta(nuevoNumero);
	}
	
	public boolean numeroCamisetaDisponible(int numero) {
		
		for(Miembro miembro : miembros) {
			if(miembro.getNumeroCamiseta() == numero) {
				return false;
			}
		}
		
		return true;
	}
	
	public List<Jugador> getJugadores(){
		
		List<Jugador> jugadores = new ArrayList<Jugador>();
		
		for(Miembro miembro : miembros) {
			jugadores.add(miembro.getJugador());
		}
		
		return jugadores;
	}
	
	public List<Jugador> getJugadoresHabilitados(Circuito circuito){
		
		List<Jugador> jugadoresHabilitados = new ArrayList<Jugador>();
		
		for(Miembro miembro : miembros) {
			
			Jugador jugador = miembro.getJugador();
			
			if(jugador.isActivo() && !jugador.estaSuspendido(circuito)) {
				
				jugadoresHabilitados.add(jugador);
			}
		}
		
		return jugadoresHabilitados;
	}
	
	public boolean puedeJugarCircuito(Circuito circuito) {
		
		return getJugadoresHabilitados(circuito).size() >= deporte.getMinJugadoresHabilitados();
	}
	
	public boolean puedeJugar() {
		return puedeJugarCircuito(null);
	}
	
	
	public boolean contieneJugador(Jugador jugador) {
		return buscar(jugador) != null;
	}
	
	public int cuposDisponibles() {
		return cupoMaximo - miembros.size();
	}
	
	public Miembro buscar(Jugador jugador) {
		
		for(Miembro miembro : miembros) {
			if(miembro.getJugador().equals(jugador)) {
				return miembro;
			}
		}
		
		return null;
	}
	
	@Override
    public boolean equals(Object o) {
        return o instanceof Equipo e && nombre.equalsIgnoreCase(e.nombre)
                && deporte.equals(e.deporte) && categoriaEdad.equals(e.categoriaEdad);
    }
 
    @Override
    public String toString() {
        return nombre + " (" + deporte + ", " + categoriaEdad + ")";
    }

}
