package clubDeportivoSeneca.modelo.usuario;

import java.time.LocalDate; 
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import clubDeportivoSeneca.modelo.competencias.Circuito;
import clubDeportivoSeneca.modelo.deportes.DeporteDeConjunto;
import clubDeportivoSeneca.modelo.deportes.DeporteIndividual;
import clubDeportivoSeneca.modelo.equipos.Equipo;
import clubDeportivoSeneca.modelo.equipos.Miembro;
import clubDeportivoSeneca.modelo.partidos.Sancion;

public class Jugador extends Usuario implements Comprador{
	
	private String nombre;
	private LocalDate fechaNacimiento;
	private int puntosFidelidad;
	private boolean activo;
	private boolean bebidaCaliente;
	private Instalacion instalacionActual;
	private Empleado perfilEmpleado;
	private List<Miembro> membresias;
	private List<NivelDeporteIndividual> nivelesIndivuales;
	private List<Sancion> sanciones;
	
	public Jugador(String login, String password, String nombre, LocalDate fechaNacimiento) {
		super(login, password);
		
		if(fechaNacimiento == null || fechaNacimiento.isAfter(LocalDate.now())) {
			throw new Exception("La fecha de nacimiento es inválida.");
		}
		
		this.nombre = nombre;
		this.fechaNacimiento = fechaNacimiento;
		this.puntosFidelidad = 0;
		this.activo = true;
		this.bebidaCaliente = false;
		this.membresias = new ArrayList<Miembro>();
		this.nivelesIndivuales = new ArrayList<NivelDeporteIndividual>();
		this.sanciones = new ArrayList<Sancion>();
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public LocalDate getFechaNacimiento() {
		return fechaNacimiento;
	}

	public int getPuntosFidelidad() {
		return puntosFidelidad;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	public boolean isBebidaCaliente() {
		return bebidaCaliente;
	}

	public void setBebidaCaliente(boolean bebidaCaliente) {
		this.bebidaCaliente = bebidaCaliente;
	}

	public Instalacion getInstalacionActual() {
		return instalacionActual;
	}

	public void setInstalacionActual(Instalacion instalacionActual) {
		this.instalacionActual = instalacionActual;
	}

	public Empleado getPerfilEmpleado() {
		return perfilEmpleado;
	}

	public void setPerfilEmpleado(Empleado perfilEmpleado) {
		this.perfilEmpleado = perfilEmpleado;
	}

	public List<NivelDeporteIndividual> getNivelesIndivuales() {
		return nivelesIndivuales;
	}

	public void setNivelIndivual(NivelDeporteIndividual nivelIndivual) {
		this.nivelesIndivuales = nivelIndivual;
	}
	
	public List<Sancion> getSanciones() {
		return sanciones;
	}
	

	
	public int calcularEdad() {
		LocalDate fechaActual = LocalDate.now();
		
		Period periodo = Period.between(fechaNacimiento, fechaActual);
		
		return periodo.getYears();
	}
	
	@Override
	public void acumularPuntos(int valorCompra) {
		
		puntosFidelidad += (int) (valorCompra * 0.02);
		
	}
	
	@Override
	public void redimirPuntos(int valorCompra) throws Exception {
		
		if(puntosFidelidad == 0) {
			throw new Exception("No hay puntos para redimir.");
		} else if(valorCompra >= puntosFidelidad){
			valorCompra -= puntosFidelidad;
			puntosFidelidad = 0;
		} else {
			puntosFidelidad -= valorCompra;
			valorCompra = 0;
		}
		
	}
	
	
	
    public void agregarMembresia(Miembro miembro) throws Exception {
		
		if(miembro == null) {
			throw new Exception(("Membresía nula."));
		}
		
		if(membresias.contains(miembro)) {
			throw new Exception("Este jugador ya contiene esta membresía.");
		}
		
		membresias.add(miembro);
	}
    
    public void retirarMembresia(Miembro miembro) {
    	membresias.remove(miembro);
    }
    
    public List<Equipo> geEquipos(){
    	List<Equipo> equipos = new ArrayList<Equipo>();
    	
    	for(Miembro miembro : membresias) {
    		equipos.add(miembro.getEquipo());
    	}
    	
    	return equipos;
    }
    
    public Equipo getEquipoPorDeporte(DeporteDeConjunto deporte) {
		
		for(Miembro miembro : membresias) {
			if(miembro.getEquipo().getDeporte().equals(deporte)) {
				return miembro.getEquipo();
			}
		}
		
		return null;
	}
	
	
	
	
	public void asignarNivel(DeporteIndividual deporte, NivelJugador nivel) {
		
		for(NivelDeporteIndividual nivel : niveles) {
			if(nivel.getDeporte().equals(deporte)) {
				niveles.setNivel(nivel);
			} else {
				niveles.add(new NivelDeporteIndividual(this, deporte, nivel));
			}
		}
	}
	
	public NivelJugador getNivelPorDeporteInd(DeporteIndividual deporte) {
		
		for(NivelDeporteIndividual nivel : niveles) {
			if(nivel.getDeporte().equals(deporte)) {
				return nivel.getNivel();
			}
		}
		
		return null;
	}
	
	
	
	public void agregarSancion(Sancion sancion) throws Exception {
		
		if(sancion == null) {
			throw new Exception("No hay sanción para añadir.");
		}
		
		sanciones.add(sancion);
	}
	
    public boolean estaSuspendido(Circuito circuito) {
		
		for(Sancion sancion : sanciones) {
			if(sancion.estaVigente() && sancion.aplicaCircuito(circuito)) {
				return true;
			}
		}
		
		return false;
	}
	

    
	public boolean esEntrenador() {
		return perfilEmpleado instanceof Empleado;
	}

}























