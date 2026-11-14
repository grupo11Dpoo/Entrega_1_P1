package clubDeportivoSeneca.sistema;

import java.io.Serializable; 
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import clubDeportivoSeneca.modelo.competencias.Circuito;
import clubDeportivoSeneca.modelo.competencias.InscripcionCompetencia;
import clubDeportivoSeneca.modelo.competencias.PartidoCompetencia;
import clubDeportivoSeneca.modelo.deportes.CategoriaEdad;
import clubDeportivoSeneca.modelo.deportes.Deporte;
import clubDeportivoSeneca.modelo.deportes.DeporteDeConjunto;
import clubDeportivoSeneca.modelo.equipos.Equipo;
import clubDeportivoSeneca.modelo.equipos.Miembro;
import clubDeportivoSeneca.modelo.entrenamiento.Entrenamiento;
import clubDeportivoSeneca.modelo.individual.NivelJugador;
import clubDeportivoSeneca.modelo.individual.PartidoIndividual;
import clubDeportivoSeneca.modelo.individual.Ranking;
import clubDeportivoSeneca.modelo.instalaciones.HistorialReservas;
import clubDeportivoSeneca.modelo.instalaciones.InventarioInstalaciones;
import clubDeportivoSeneca.modelo.instalaciones.Instalacion;
import clubDeportivoSeneca.modelo.instalaciones.Reserva;
import clubDeportivoSeneca.modelo.instalaciones.HorarioBloque;
import clubDeportivoSeneca.modelo.instalaciones.Modalidad;
import clubDeportivoSeneca.modelo.inventario.InventarioArticulos;
import clubDeportivoSeneca.modelo.inventario.SugerenciaProducto;
import clubDeportivoSeneca.modelo.partidos.PartidoInterno;
import clubDeportivoSeneca.modelo.torneos.BonoDescuento;
import clubDeportivoSeneca.modelo.torneos.Encuentro;
import clubDeportivoSeneca.modelo.torneos.EstadoTorneo;
import clubDeportivoSeneca.modelo.torneos.Inscripcion;
import clubDeportivoSeneca.modelo.torneos.Torneo;
import clubDeportivoSeneca.modelo.torneos.TorneoCompetitivo;
import clubDeportivoSeneca.modelo.torneos.TorneoRecreativo;
import clubDeportivoSeneca.modelo.turnos.GestorTurnos;
import clubDeportivoSeneca.modelo.usuario.Administrador;
import clubDeportivoSeneca.modelo.usuario.Empleado;
import clubDeportivoSeneca.modelo.usuario.Jugador;
import clubDeportivoSeneca.modelo.usuario.Entrenador;
import clubDeportivoSeneca.modelo.usuario.Usuario;
import clubDeportivoSeneca.modelo.ventas.VentaServiciosAdicionales;


@SuppressWarnings("serial")
public class ClubDeportivoSeneca implements Serializable {

    private Administrador administrador;
    private Map<String, Usuario> usuarios = new LinkedHashMap<>();
    private List<Equipo> equipos = new ArrayList<>();
    private List<PartidoInterno> partidosInternos = new ArrayList<>();
    private List<Circuito> circuitos = new ArrayList<>();
    private List<PartidoCompetencia> partidosCompetencia = new ArrayList<>();
    private List<PartidoIndividual> partidosIndividuales = new ArrayList<>();
    private List<Ranking> rankings = new ArrayList<>();
    private List<Entrenamiento> entrenamientos = new ArrayList<>();
    private GestorTurnos gestorTurnos;
    private InventarioArticulos inventarioArticulos;
    private InventarioInstalaciones inventarioInstalaciones;
    private List<VentaServiciosAdicionales> ventas = new ArrayList<>();
    private List<SugerenciaProducto> sugerencias = new ArrayList<>();

    public ClubDeportivoSeneca() {
        try {
            HistorialReservas historial = new HistorialReservas();
            this.gestorTurnos = new GestorTurnos(LocalTime.of(6, 0), LocalTime.of(22, 0));
            this.inventarioArticulos = new InventarioArticulos();
            this.inventarioInstalaciones = new InventarioInstalaciones(historial);
        } catch (Exception e) {
            throw new IllegalStateException("No fue posible inicializar el club", e);
        }
    }

    public ClubDeportivoSeneca(Administrador administrador, GestorTurnos gestorTurnos,
            InventarioArticulos inventarioArticulos, InventarioInstalaciones inventarioInstalaciones) {
        if (gestorTurnos == null || inventarioArticulos == null || inventarioInstalaciones == null)
            throw new IllegalArgumentException("Los gestores e inventarios son obligatorios.");
        this.administrador = administrador;
        this.gestorTurnos = gestorTurnos;
        this.inventarioArticulos = inventarioArticulos;
        this.inventarioInstalaciones = inventarioInstalaciones;
        if (administrador != null) {
            registrarUsuario(administrador);
            administrador.setClub(this);
        }
    }

    public Administrador getAdministrador() { return administrador; }
    public void setAdministrador(Administrador administrador) {
        this.administrador = administrador;
        if (administrador != null) { registrarUsuario(administrador); administrador.setClub(this); }
    }
    public Map<String, Usuario> getUsuarios() { return Collections.unmodifiableMap(usuarios); }
    public List<Equipo> getEquipos() { return Collections.unmodifiableList(equipos); }
    public List<PartidoInterno> getPartidosInternos() { return Collections.unmodifiableList(partidosInternos); }
    public List<Circuito> getCircuitos() { return Collections.unmodifiableList(circuitos); }
    public List<PartidoCompetencia> getPartidosCompetencia() { return Collections.unmodifiableList(partidosCompetencia); }
    public List<PartidoIndividual> getPartidosIndividuales() { return Collections.unmodifiableList(partidosIndividuales); }
    public List<Ranking> getRankings() { return Collections.unmodifiableList(rankings); }
    public List<Entrenamiento> getEntrenamientos() { return Collections.unmodifiableList(entrenamientos); }
    public GestorTurnos getGestorTurnos() { return gestorTurnos; }
    public InventarioArticulos getInventarioArticulos() { return inventarioArticulos; }
    public InventarioInstalaciones getInventarioInstalaciones() { return inventarioInstalaciones; }
    public List<VentaServiciosAdicionales> getVentas() { return Collections.unmodifiableList(ventas); }
    public List<SugerenciaProducto> getSugerencias() { return Collections.unmodifiableList(sugerencias); }

    public void registrarUsuario(Usuario usuario) {
        if (usuario == null) throw new IllegalArgumentException("El usuario es obligatorio.");
        if (usuarios.containsKey(usuario.getLogin())) throw new IllegalArgumentException("Ya existe ese login.");
        usuarios.put(usuario.getLogin(), usuario);
    }
    public Usuario iniciarSesion(String login, String password) {
        Usuario usuario = usuarios.get(login);
        return usuario != null && usuario.autenticar(login, password) ? usuario : null;
    }
    public Jugador crearJugador(String login, String password, String nombre, LocalDate fechaNacimiento) {
        Jugador jugador = new Jugador(login, password, nombre, fechaNacimiento);
        registrarUsuario(jugador);
        return jugador;
    }
    public Equipo crearEquipo(String nombre, DeporteDeConjunto deporte, CategoriaEdad categoriaEdad, int cupo) {
        if (cupo <= 0) throw new IllegalArgumentException("El cupo debe ser positivo.");
        Equipo equipo = new Equipo(nombre, deporte, categoriaEdad, cupo);
        equipos.add(equipo);
        return equipo;
    }
    public void darBajaEquipo(Equipo equipo) {
        if (equipo == null || !equipos.contains(equipo)) throw new IllegalArgumentException("Equipo no registrado.");
        equipo.setActivo(false);
    }
    public void darBajaJugador(Jugador jugador) {
        if (jugador == null || usuarios.get(jugador.getLogin()) != jugador) throw new IllegalArgumentException("Jugador no registrado.");
        jugador.setActivo(false);
    }
    public void registrarPartidoInterno(PartidoInterno partido) {
        if (partido == null) throw new IllegalArgumentException("El partido es obligatorio.");
        partidosInternos.add(partido);
    }
    public void registrarCircuito(Circuito circuito) {
        if (circuito == null) throw new IllegalArgumentException("La competencia es obligatoria.");
        circuitos.add(circuito);
    }
    public void registrarPartidoCompetencia(PartidoCompetencia partido) throws Exception {
        if (partido == null) throw new IllegalArgumentException("El partido es obligatorio.");
        partido.getCircuito().registrarPartido(partido);
        partidosCompetencia.add(partido);
    }
    public void registrarPartidoIndividual(PartidoIndividual partido) {
        if (partido == null) throw new IllegalArgumentException("El partido es obligatorio.");
        partidosIndividuales.add(partido);
    }
    public void registrarEntrenamiento(Entrenamiento entrenamiento) {
        if (entrenamiento == null) throw new IllegalArgumentException("El entrenamiento es obligatorio.");
        entrenamientos.add(entrenamiento);
    }
    public void registrarVenta(VentaServiciosAdicionales venta) {
        if (venta == null) throw new IllegalArgumentException("La venta es obligatoria.");
        ventas.add(venta);
    }
    public void registrarSugerencia(SugerenciaProducto sugerencia) {
        if (sugerencia == null) throw new IllegalArgumentException("La sugerencia es obligatoria.");
        sugerencias.add(sugerencia);
    }
    public List<VentaServiciosAdicionales> consultarVentas(LocalDate desde, LocalDate hasta) {
        List<VentaServiciosAdicionales> resultado = new ArrayList<>();
        for (VentaServiciosAdicionales v : ventas)
            if ((desde == null || !v.getFecha().isBefore(desde)) && (hasta == null || !v.getFecha().isAfter(hasta))) resultado.add(v);
        return resultado;
    }
    public List<Empleado> getEmpleados() {
        List<Empleado> resultado = new ArrayList<>();
        for (Usuario u : usuarios.values()) if (u instanceof Empleado e) resultado.add(e);
        return resultado;
    }

    public Usuario consultarUsuario(String login) { return usuarios.get(login); }

    public Miembro inscribirJugadorEnEquipo(Equipo equipo, Jugador jugador, String posicion, int numeroCamiseta) throws Exception {
        if (equipo == null || !equipos.contains(equipo)) throw new IllegalArgumentException("El equipo no está registrado en el club.");
        return equipo.inscribirJugador(jugador, posicion, numeroCamiseta);
    }

    public Reserva crearReserva(Jugador jugador, Instalacion instalacion, LocalDate fecha,
            HorarioBloque horario, int cantidadJugadores, Modalidad modalidad) throws Exception {
        return inventarioInstalaciones.crearReserva(jugador, instalacion, fecha, horario, cantidadJugadores, modalidad);
    }

    public void cancelarReserva(Reserva reserva) throws Exception {
        inventarioInstalaciones.cancelarReserva(reserva);
    }

    public void registrarInstalacion(Instalacion instalacion) throws Exception {
        inventarioInstalaciones.agregarInstalacion(instalacion);
    }

    public void registrarArticulo(clubDeportivoSeneca.modelo.inventario.Articulo articulo, int cantidadInicial) {
        inventarioArticulos.registrarArticulo(articulo, cantidadInicial);
    }

    public InscripcionCompetencia inscribirEquipoEnCompetencia(Circuito circuito, Equipo equipo, List<Jugador> nomina) throws Exception {
        if (circuito == null || !circuitos.contains(circuito)) throw new IllegalArgumentException("La competencia no está registrada en el club.");
        return circuito.inscribirEquipo(equipo, nomina);
    }

    public Entrenamiento planearEntrenamiento(Entrenador entrenador, LocalDate fecha, LocalTime hora,
            int duracionMinutos, Instalacion instalacion) throws Exception {
        if (entrenador == null || usuarios.get(entrenador.getLogin()) != entrenador)
            throw new IllegalArgumentException("El entrenador debe estar registrado en el club.");
        Entrenamiento entrenamiento = entrenador.planearEntrenamiento(fecha, hora, duracionMinutos, instalacion);
        registrarEntrenamiento(entrenamiento);
        return entrenamiento;
    }

    public void registrarRanking(Ranking ranking) {
        if (ranking == null) throw new IllegalArgumentException("El ranking es obligatorio.");
        rankings.add(ranking);
    }

    public List<Equipo> consultarEquiposActivos(DeporteDeConjunto deporte, CategoriaEdad categoria) {
        List<Equipo> resultado = new ArrayList<>();
        for (Equipo e : equipos) if (e.isActivo() && (deporte == null || e.getDeporte().equals(deporte))
                && (categoria == null || e.getCategoriaEdad().equals(categoria))) resultado.add(e);
        return resultado;
    }

    public List<Jugador> consultarJugadoresActivos() {
        List<Jugador> resultado = new ArrayList<>();
        for (Usuario u : usuarios.values()) if (u instanceof Jugador j && j.isActivo()) resultado.add(j);
        return resultado;
    }
    
    
    
    //para proyecto 2
    
    private List<Torneo> torneos = new ArrayList<>();

    public List<Torneo> getTorneos() {
    	return torneos;
    }

    public TorneoRecreativo crearTorneoRecreativo(String nombre, Deporte deporte, CategoriaEdad categoriaEdad,
            NivelJugador nivel, boolean dobles, int cupos, LocalDate fechaInicio, double porcentajeBono) throws Exception {
        
    	TorneoRecreativo torneo = new TorneoRecreativo(nombre, deporte, categoriaEdad, nivel, dobles, cupos, fechaInicio, porcentajeBono);
        
    	registrarTorneo(torneo);
        
    	return torneo;
    }

    public TorneoCompetitivo crearTorneoCompetitivo(String nombre, Deporte deporte, CategoriaEdad categoriaEdad,
            NivelJugador nivel, boolean dobles, int cupos, LocalDate fechaInicio, double tarifaInscripcion) throws Exception {
        
    	TorneoCompetitivo torneo = new TorneoCompetitivo(nombre, deporte, categoriaEdad, nivel, dobles, cupos, fechaInicio, tarifaInscripcion);
        
    	registrarTorneo(torneo);
        
    	return torneo;
    }

    public List<Torneo> consultarTorneos(EstadoTorneo estado) {
        
    	List<Torneo> resultado = new ArrayList<>();
        
    	for (Torneo torneo : torneos) {
            
    		if (estado == null || torneo.getEstado() == estado) {
                resultado.add(torneo);
            }
        }
        return resultado;
    }

    public String consultarHistorialTorneo(Torneo torneo) throws Exception {
        
    	validarTorneo(torneo);
        return torneo.historialDetallado();
    }

    public void definirEquiposDestacados(Torneo torneo, List<Equipo> equiposPorDesempeno) throws Exception {
        
    	validarTorneo(torneo);
        torneo.definirEquiposDestacados(equiposPorDesempeno);
    }

    public Inscripcion inscribirEquipoEnTorneo(Torneo torneo, Equipo equipo) throws Exception {
        
    	validarTorneo(torneo);
    	
        if (equipo == null || !equipos.contains(equipo)) {
            throw new Exception("El equipo no está registrado en el club.");
        }
        
        return torneo.inscribirEquipo(equipo);
    }

    public Inscripcion inscribirJugadorEnTorneo(Torneo torneo, Jugador jugador) throws Exception {
        
    	validarTorneo(torneo);
        
        actualizarDestacados(torneo);
        
        return torneo.inscribirJugador(jugador);
    }

    public Inscripcion inscribirParejaEnTorneo(Torneo torneo, Jugador j1, Jugador j2) throws Exception {
        
    	validarTorneo(torneo);
        
    	actualizarDestacados(torneo);
        
    	return torneo.inscribirPareja(j1, j2);
    }

    public Inscripcion inscribirEmpleadoEnTorneo(Torneo torneo, Empleado empleado) throws Exception {
        
    	validarTorneo(torneo);
        
    	if (empleado == null || !empleado.esTambienJugador()) {
            throw new Exception("El empleado necesita un perfil de jugador para inscribirse.");
        }
    	
        Jugador jugador = empleado.getPerfilJugador();
        
        if (jugador.getPerfilEmpleado() == null) {
            jugador.setPerfilEmpleado(empleado);
        }
        
        actualizarDestacados(torneo);
        return torneo.inscribirJugador(jugador);
    }

    public void desinscribirDeTorneo(Torneo torneo, Inscripcion inscripcion) throws Exception {
        
    	validarTorneo(torneo);
        
    	torneo.desinscribir(inscripcion);
    }

    public Encuentro registrarEncuentroTorneo(Torneo torneo, Inscripcion local, Inscripcion visitante, LocalDateTime fecha) throws Exception {
        
    	validarTorneo(torneo);
        
    	return torneo.registrarEncuentro(local, visitante, fecha);
    }

    public void registrarResultadoEncuentro(Torneo torneo, Encuentro encuentro, Inscripcion ganador, String resultado) throws Exception {
        
    	validarTorneo(torneo);
        
    	if (encuentro == null || !torneo.getEncuentros().contains(encuentro)) {
            throw new Exception("El encuentro no pertenece al torneo.");
        }
    	
        encuentro.registrarResultado(ganador, resultado);
    }

    public void finalizarTorneo(Torneo torneo, List<Inscripcion> posicionesFinales) throws Exception {
        
    	validarTorneo(torneo);
        
    	torneo.finalizar(posicionesFinales);
    }

    public List<BonoDescuento> consultarBonosDisponibles(Jugador jugador) throws Exception {
        
    	if (jugador == null) {
            throw new Exception("El jugador es obligatorio.");
        }
    	
        List<BonoDescuento> resultado = new ArrayList<>();
        
        for (BonoDescuento b : jugador.getBonos()) {
            
        	if (!b.isRedimido() && b.estaVigente()) {
                resultado.add(b);
            }
        }
        
        return resultado;
    }

    public void redimirBono(VentaServiciosAdicionales venta, BonoDescuento bono) throws Exception {
        
    	if (venta == null) {
            throw new Exception("La venta es obligatoria.");
        }
        
    	venta.aplicarBono(bono);
    }

    private void registrarTorneo(Torneo torneo) throws Exception {
        
    	for (Torneo t : torneos) {
    		
            if (t.getNombre().equalsIgnoreCase(torneo.getNombre())) {
                throw new Exception("Ya existe un torneo con ese nombre.");
            }
        }
    	
        actualizarDestacados(torneo);
        torneos.add(torneo);
    }

    private void validarTorneo(Torneo torneo) throws Exception {
        
    	if (torneo == null || !torneos.contains(torneo)) {
            throw new Exception("El torneo no está registrado en el club.");
        }
    }

    private void actualizarDestacados(Torneo torneo) throws Exception {
        
    	if (!torneo.esDeConjunto()) {
            torneo.definirDestacadosPorRanking(rankings);
        }
    }
    
    
}
