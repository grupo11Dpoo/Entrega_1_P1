package clubDeportivoSeneca.modelo.torneos;
 
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
 
import clubDeportivoSeneca.modelo.deportes.CategoriaEdad;
import clubDeportivoSeneca.modelo.deportes.Deporte;
import clubDeportivoSeneca.modelo.deportes.DeporteDeConjunto;
import clubDeportivoSeneca.modelo.deportes.DeporteIndividual;
import clubDeportivoSeneca.modelo.equipos.Equipo;
import clubDeportivoSeneca.modelo.individual.NivelJugador;
import clubDeportivoSeneca.modelo.individual.Ranking;
import clubDeportivoSeneca.modelo.turnos.Turno;
import clubDeportivoSeneca.modelo.usuario.Jugador;
 
@SuppressWarnings("serial")
public abstract class Torneo implements Serializable {
 
    private String nombre;
    private Deporte deporte;
    private CategoriaEdad categoriaEdad;
    private NivelJugador nivel;
    private boolean dobles;
    private int cupos;
    private LocalDate fechaInicio;
    private EstadoTorneo estado;
    private List<Inscripcion> inscripciones;
    private List<Encuentro> encuentros;
    private List<Inscripcion> posiciones;

    private List<Jugador> jugadoresDestacados;
    private List<Equipo> equiposDestacados;
 
    protected Torneo(String nombre, Deporte deporte, CategoriaEdad categoriaEdad, NivelJugador nivel,
                     boolean dobles, int cupos, LocalDate fechaInicio) throws Exception {
    	
        if (nombre == null || nombre.isBlank()) {
        	throw new Exception("El nombre es obligatorio.");
        }
        
        if (deporte == null) {
        	throw new Exception("El deporte es obligatorio.");
        }
        
        if (cupos <= 0) {
        	throw new Exception("El número de cupos debe ser positivo.");
        }
        
        if (fechaInicio == null) {
        	throw new Exception("La fecha de inicio es obligatoria.");
        }
        
        if (deporte instanceof DeporteDeConjunto && (categoriaEdad == null || nivel != null || dobles)) {
            throw new Exception("Un torneo de conjunto lleva categoría de edad, sin nivel ni dobles.");
        }
        
        if (deporte instanceof DeporteIndividual && (nivel == null || categoriaEdad != null)) {
            throw new Exception("Un torneo individual lleva nivel, sin categoría de edad.");
        }
 
        this.nombre = nombre;
        this.deporte = deporte;
        this.categoriaEdad = categoriaEdad;
        this.nivel = nivel;
        this.dobles = dobles;
        this.cupos = cupos;
        this.fechaInicio = fechaInicio;
        this.estado = EstadoTorneo.EN_CURSO;
        this.inscripciones = new ArrayList<Inscripcion>();
        this.encuentros = new ArrayList<Encuentro>();
        this.posiciones = new ArrayList<Inscripcion>();
        this.jugadoresDestacados = new ArrayList<Jugador>();
        this.equiposDestacados = new ArrayList<Equipo>();
    }
 
    public String getNombre() { 
    	return nombre; 
    }
    
    public Deporte getDeporte() { 
    	return deporte; 
    }
    
    public CategoriaEdad getCategoriaEdad() {
    	return categoriaEdad;
    }
    
    public NivelJugador getNivel() {
    	return nivel;
    }
    
    public boolean isDobles() {
    	return dobles;
    }
    
    public int getCupos() {
    	return cupos;
    }
    
    public LocalDate getFechaInicio() {
    	return fechaInicio;
    }
    
    public EstadoTorneo getEstado() {
    	return estado;
    }
    
    public boolean esDeConjunto() {
    	return deporte instanceof DeporteDeConjunto;
    }
    
    public List<Inscripcion> getInscripciones() {
    	return Collections.unmodifiableList(inscripciones);
    }
    
    public List<Encuentro> getEncuentros() {
    	return Collections.unmodifiableList(encuentros);
    }
    
    public List<Inscripcion> getPosiciones() {
    	return Collections.unmodifiableList(posiciones);
    }
 
    protected List<Inscripcion> inscripcionesInternas() {
    	return inscripciones;
    }
    
    protected List<Encuentro> encuentrosInternos() {
    	return encuentros;
    }
    
    protected List<Inscripcion> posicionesInternas() {
    	return posiciones;
    }
    
    protected void cambiarEstado(EstadoTorneo nuevo) {
    	this.estado = nuevo;
    }

    
    public int cuposPreferenciales() {
        return (int) Math.ceil(cupos * 0.2);
    }

    public int cuposDisponibles() {
        return cupos - inscripciones.size();
    }

    public int cuposPreferencialesOcupados() {
        int ocupados = 0;
        for (Inscripcion i : inscripciones) {
            if (i.isCupoPreferencial()) {
                ocupados++;
            }
        }
        return ocupados;
    }
 
    public boolean admiteInscripciones() {
        return estado == EstadoTorneo.EN_CURSO && LocalDate.now().isBefore(fechaInicio);
    }
 
    public Inscripcion inscribirEquipo(Equipo equipo) throws Exception {
       
    	validarAbierto();
        
    	if (!esDeConjunto()) {
            throw new Exception("Este torneo es individual; no admite equipos.");
        }
        
    	if (equipo == null) {
            throw new Exception("El equipo es obligatorio.");
        }
        
    	if (!equipo.isActivo()) {
            throw new Exception("El equipo no está activo.");
        }
        
    	if (!equipo.getDeporte().equals(deporte)) {
            throw new Exception("El equipo no juega el deporte del torneo.");
        }
        
    	if (!equipo.getCategoriaEdad().equals(categoriaEdad)) {
            throw new Exception("El equipo no pertenece a la categoría de edad del torneo.");
        }
        
    	if (!equipo.puedeJugar()) {
            throw new Exception("El equipo no tiene los jugadores habilitados mínimos.");
        }
        
    	for (Inscripcion i : inscripciones) {
            if (i.esEquipo() && i.getEquipo().equals(equipo)) {
                throw new Exception("El equipo ya está inscrito en el torneo.");
            }
        }
        
    	Inscripcion nueva = new Inscripcion(equipo, asignarCupo(equiposDestacados.contains(equipo)));
        inscripciones.add(nueva);
        
        return nueva;
    }
 
    public Inscripcion inscribirJugador(Jugador jugador) throws Exception {
      
    	validarAbierto();
        
    	if (esDeConjunto()) {
            throw new Exception("Este torneo es de conjunto; se inscriben equipos.");
        }
        
    	if (dobles) {
            throw new Exception("Este torneo es de dobles; se inscriben parejas.");
        }
        
    	validarJugador(jugador);
        List<Jugador> participantes = new ArrayList<Jugador>();
        participantes.add(jugador);
        Inscripcion nueva = new Inscripcion(participantes, asignarCupo(jugadoresDestacados.contains(jugador)));
        inscripciones.add(nueva);
      
        return nueva;
    }
 
    public Inscripcion inscribirPareja(Jugador j1, Jugador j2) throws Exception {
       
    	validarAbierto();
        
    	if (esDeConjunto()) {
            throw new Exception("Este torneo es de conjunto; se inscriben equipos.");
        }
        
    	if (!dobles) {
            throw new Exception("Este torneo no es de dobles.");
        }
        
    	if (j1 == null || j2 == null) {
            throw new Exception("La pareja requiere dos jugadores.");
        }
        
    	if (j1 == j2) {
            throw new Exception("Los dos jugadores de la pareja deben ser distintos.");
        }
        
    	validarJugador(j1);
        validarJugador(j2);

        List<Jugador> participantes = new ArrayList<Jugador>();
        participantes.add(j1);
        participantes.add(j2);
        
        Inscripcion nueva = new Inscripcion(participantes,
                asignarCupo(jugadoresDestacados.contains(j1) || jugadoresDestacados.contains(j2)));
        
        inscripciones.add(nueva);
       
        return nueva;
    }
 
    public void desinscribir(Inscripcion inscripcion) throws Exception {
       
    	validarAbierto();
        
    	if (inscripcion == null || !inscripciones.contains(inscripcion)) {
            throw new Exception("La inscripción no pertenece a este torneo.");
        }
        
        inscripciones.remove(inscripcion);
    }
 
    public Encuentro registrarEncuentro(Inscripcion local, Inscripcion visitante, java.time.LocalDateTime fecha)
            throws Exception {
        if (estado == EstadoTorneo.FINALIZADO) {
            throw new Exception("El torneo ya finalizó.");
        }
       
        if (local == null || visitante == null) {
            throw new Exception("Local y visitante son obligatorios.");
        }
        
        if (local == visitante) {
            throw new Exception("Un participante no puede enfrentarse a sí mismo.");
        }
        
        if (!inscripciones.contains(local) || !inscripciones.contains(visitante)) {
            throw new Exception("Ambos participantes deben estar inscritos en el torneo.");
        }
        
        if (fecha != null && fecha.toLocalDate().isBefore(fechaInicio)) {
            throw new Exception("El encuentro no puede ser anterior al inicio del torneo.");
        }
        
        Encuentro encuentro = new Encuentro(local, visitante, fecha);
        encuentros.add(encuentro);
        
        return encuentro;
    }
 
    public void finalizar(List<Inscripcion> posicionesFinales) throws Exception {
        if (estado == EstadoTorneo.FINALIZADO) {
            throw new Exception("El torneo ya finalizó.");
        }
       
        if (LocalDate.now().isBefore(fechaInicio)) {
            throw new Exception("El torneo aún no ha iniciado.");
        }
        
        if (posicionesFinales == null || posicionesFinales.isEmpty()) {
            throw new Exception("Las posiciones finales son obligatorias.");
        }
        
        if (posicionesFinales.size() != inscripciones.size()
                || new HashSet<Inscripcion>(posicionesFinales).size() != posicionesFinales.size()
                || !inscripciones.containsAll(posicionesFinales)) {
            throw new Exception("Las posiciones deben incluir exactamente una vez a cada inscrito.");
        }
        
        posiciones.clear();
        posiciones.addAll(posicionesFinales);
        estado = EstadoTorneo.FINALIZADO;
        entregarPremios();
    }
 
    protected void entregarPremios() throws Exception {
    }
 
    public void definirDestacadosPorRanking(List<Ranking> rankings) throws Exception {
       
    	validarAbierto();
        
    	if (esDeConjunto()) {
            throw new Exception("Este torneo es de conjunto; los destacados son equipos.");
        }
        
    	if (rankings == null) {
            throw new Exception("Los rankings son obligatorios.");
        }
        
    	List<Ranking> categoria = new ArrayList<Ranking>();
        
    	for (Ranking r : rankings) {
        
    		if (r.getDisciplina().equals(deporte)
                    && r.getJugador().getNivelPorDeporteInd((DeporteIndividual) deporte) == nivel) {
                categoria.add(r);
            }
        }
        
    	categoria.sort((r1, r2) -> Integer.compare(r2.getPuntaje(), r1.getPuntaje()));
        
    	jugadoresDestacados.clear();
        
    	for (int k = 0; k < Math.min(cuposPreferenciales(), categoria.size()); k++) {
            jugadoresDestacados.add(categoria.get(k).getJugador());
        }
    }
 

    public void definirEquiposDestacados(List<Equipo> equiposPorDesempeno) throws Exception {
        
    	validarAbierto();
        if (!esDeConjunto()) {
            throw new Exception("Este torneo es individual; los destacados son jugadores.");
        }
        
        if (equiposPorDesempeno == null) {
            throw new Exception("La lista de equipos es obligatoria.");
        }
        
        equiposDestacados.clear();
        
        for (Equipo e : equiposPorDesempeno) {
            if (equiposDestacados.size() < cuposPreferenciales()) {
                equiposDestacados.add(e);
            }
        }
    }
 
    public List<Jugador> getJugadoresDestacados() {
        return Collections.unmodifiableList(jugadoresDestacados);
    }
 
    public List<Equipo> getEquiposDestacados() {
        return Collections.unmodifiableList(equiposDestacados);
    }
 
    public String historialDetallado() {
        
    	String historial = "Torneo: " + nombre + " | " + deporte
                + " | Estado: " + estado
                + " | Inicio: " + fechaInicio
                + " | Cupos: " + inscripciones.size() + "/" + cupos + "\n";
        historial += "Participantes:\n";
        
        for (Inscripcion i : inscripciones) {
        	historial += "  - " + i.descripcion();
            if (i.isCupoPreferencial()) {
            	historial += " (preferencial)";
            }
            historial += "\n";
        }
        
        historial += "Encuentros:\n";
        for (Encuentro e : encuentros) {
        	historial += "  - " + e.getFecha() + ": " + e.getLocal().descripcion()
                    + " vs " + e.getVisitante().descripcion();
            if (e.estaJugado()) {
            	historial += " -> ganó " + e.getGanador().descripcion() + " (" + e.getResultado() + ")";
            }
            historial += "\n";
        }
        
        historial += "Posiciones:\n";
        for (int k = 0; k < posiciones.size(); k++) {
        	historial += "  " + (k + 1) + ". " + posiciones.get(k).descripcion() + "\n";
        }
        return historial;
    }
 

    protected static boolean esEmpleado(Jugador jugador) {
        return jugador.getPerfilEmpleado() != null;
    }
 
    private void validarAbierto() throws Exception {
       
    	if (estado == EstadoTorneo.FINALIZADO) {
            throw new Exception("El torneo ya finalizó.");
        }
        
        if (!admiteInscripciones()) {
            throw new Exception("El torneo ya inició; no se admiten cambios en las inscripciones.");
        }
    }
 
    private void validarJugador(Jugador jugador) throws Exception {
       
    	if (jugador == null) {
            throw new Exception("El jugador es obligatorio.");
        }
        
        if (!jugador.isActivo()) {
            throw new Exception("El jugador no está activo.");
        }
        
        NivelJugador nivelJugador = jugador.getNivelPorDeporteInd((DeporteIndividual) deporte);
        if (nivelJugador == null || nivelJugador != nivel) {
            throw new Exception("El jugador no tiene el nivel del torneo (" + nivel + ").");
        }
        
        for (Inscripcion i : inscripciones) {
            if (i.getJugadores().contains(jugador)) {
                throw new Exception("El jugador " + jugador.getNombre() + " ya está inscrito.");
            }
        }
        if (esEmpleado(jugador)) {
            
        	for (Turno t : jugador.getPerfilEmpleado().getTurnosSemanales()) {
            
        		if (t.getFecha().equals(fechaInicio)) {
                    throw new Exception("El empleado " + jugador.getNombre() + " está de turno el día del torneo.");
                }
            }
        }
    }

    
    private boolean asignarCupo(boolean destacado) throws Exception {
       
    	if (cuposDisponibles() <= 0) {
            throw new Exception("No hay cupos disponibles.");
        }
        int prefLibres = Math.max(0, cuposPreferenciales() - cuposPreferencialesOcupados());
        if (destacado && prefLibres > 0) {
            return true;
        }
        int reservados = Math.min(prefLibres, destacadosPendientes());
        if (inscripciones.size() + reservados >= cupos) {
            throw new Exception("No hay cupos regulares disponibles.");
        }
        return false;
    }
 
  
    private int destacadosPendientes() {
        
    	int pendientes = 0;
    	
        if (esDeConjunto()) {
            for (Equipo e : equiposDestacados) {
                if (!estaInscrito(e)) {
                    pendientes++;
                }
            }
        } else {
            for (Jugador j : jugadoresDestacados) {
                if (!estaInscrito(j)) {
                    pendientes++;
                }
            }
        }
        return pendientes;
    }
 
    private boolean estaInscrito(Equipo equipo) {
        for (Inscripcion i : inscripciones) {
            if (i.esEquipo() && i.getEquipo().equals(equipo)) {
                return true;
            }
        }
        return false;
    }
 
    private boolean estaInscrito(Jugador jugador) {
        for (Inscripcion i : inscripciones) {
            if (i.getJugadores().contains(jugador)) {
                return true;
            }
        }
        return false;
    }
}
