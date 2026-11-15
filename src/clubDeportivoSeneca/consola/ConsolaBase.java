package clubDeportivoSeneca.consola;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import clubDeportivoSeneca.modelo.competencias.Ascun;
import clubDeportivoSeneca.modelo.competencias.Circuito;
import clubDeportivoSeneca.modelo.competencias.RequisitosInscripcionCircuito;
import clubDeportivoSeneca.modelo.competencias.TipoFase;
import clubDeportivoSeneca.modelo.competencias.TorneoCerros;
import clubDeportivoSeneca.modelo.deportes.Baloncesto;
import clubDeportivoSeneca.modelo.deportes.CategoriaEdad;
import clubDeportivoSeneca.modelo.deportes.DeporteDeConjunto;
import clubDeportivoSeneca.modelo.deportes.DeporteIndividual;
import clubDeportivoSeneca.modelo.deportes.Futbol;
import clubDeportivoSeneca.modelo.deportes.Padel;
import clubDeportivoSeneca.modelo.deportes.Tenis;
import clubDeportivoSeneca.modelo.deportes.TenisMesa;
import clubDeportivoSeneca.modelo.deportes.Voleibol;
import clubDeportivoSeneca.modelo.equipos.Equipo;
import clubDeportivoSeneca.modelo.entrenamiento.Entrenamiento;
import clubDeportivoSeneca.modelo.entrenamiento.EstadoAsistencia;
import clubDeportivoSeneca.modelo.individual.NivelJugador;
import clubDeportivoSeneca.modelo.individual.EstadisticaTMesa;
import clubDeportivoSeneca.modelo.individual.EstadisticaTyP;
import clubDeportivoSeneca.modelo.individual.PartidoIndividual;
import clubDeportivoSeneca.modelo.individual.Ranking;
import clubDeportivoSeneca.modelo.instalaciones.CanchaPadel;
import clubDeportivoSeneca.modelo.instalaciones.CanchaTenis;
import clubDeportivoSeneca.modelo.instalaciones.HorarioBloque;
import clubDeportivoSeneca.modelo.instalaciones.Instalacion;
import clubDeportivoSeneca.modelo.instalaciones.MesaTenisMesa;
import clubDeportivoSeneca.modelo.instalaciones.Modalidad;
import clubDeportivoSeneca.modelo.instalaciones.Reserva;
import clubDeportivoSeneca.modelo.inventario.Articulo;
import clubDeportivoSeneca.modelo.inventario.TipoSugerencia;
import clubDeportivoSeneca.modelo.turnos.SolicitudCambioTurno;
import clubDeportivoSeneca.modelo.turnos.Turno;
import clubDeportivoSeneca.modelo.usuario.Administrador;
import clubDeportivoSeneca.modelo.usuario.Comprador;
import clubDeportivoSeneca.modelo.usuario.Empleado;
import clubDeportivoSeneca.modelo.usuario.Entrenador;
import clubDeportivoSeneca.modelo.usuario.Jugador;
import clubDeportivoSeneca.modelo.usuario.Usuario;
import clubDeportivoSeneca.modelo.ventas.Alergeno;
import clubDeportivoSeneca.modelo.ventas.Bebida;
import clubDeportivoSeneca.modelo.ventas.Snack;
import clubDeportivoSeneca.modelo.ventas.Temperatura;
import clubDeportivoSeneca.modelo.ventas.VentaArticulosDeportivos;
import clubDeportivoSeneca.modelo.ventas.VentaCafeteria;
import clubDeportivoSeneca.persistencia.ArchivoPlano;
import clubDeportivoSeneca.sistema.ClubDeportivoSeneca;

//para proyecto 2
public abstract class ConsolaBase {
    protected final ClubDeportivoSeneca club;
    protected final Usuario usuarioActual;
    protected final ArchivoPlano persistencia;
    protected final LecturaConsola entrada;

    protected ConsolaBase(ClubDeportivoSeneca club, Usuario usuarioActual, ArchivoPlano persistencia, LecturaConsola entrada) {
        this.club = club;
        this.usuarioActual = usuarioActual;
        this.persistencia = persistencia;
        this.entrada = entrada;
    }

    protected String opcionAutorizada(String modulo, String opcion) {
        if ("0".equals(opcion) || usuarioActual instanceof Administrador) return opcion;
        if (usuarioActual == null) throw new SecurityException("Inicia sesión para acceder al menú.");
        String permitidas = switch (modulo) {
            case "MenuUsuarios" -> "1";
            case "MenuEquipos", "MenuPartidos", "MenuEntrenamientos" -> "1";
            case "MenuCompetencias" -> "1,7";
            case "MenuIndividuales" -> "1,2";
            case "MenuInstalaciones" -> usuarioActual instanceof Jugador ? "1,3,4" : "1,3";
            case "MenuTurnos" -> usuarioActual instanceof Empleado ? "1,3,4,5" : "";
            case "MenuInventario" -> usuarioActual instanceof Empleado ? "1,4,5" : "1";
            case "MenuVentas" -> "2,3";
            default -> "";
        };
        if (!java.util.Arrays.asList(permitidas.split(",")).contains(opcion))
            throw new SecurityException("La opción " + opcion + " requiere permisos adicionales.");
        return opcion;
    }

    protected void crearPartidoCompetencia() throws Exception {
        Circuito circuito = elegirCircuito();
        Equipo local = elegirEquipo();
        String rival = leer("Nombre de la institución/equipo rival: ");
        String escenario = leer("Escenario: ");
        clubDeportivoSeneca.modelo.competencias.PartidoCompetencia partido = new clubDeportivoSeneca.modelo.competencias.PartidoCompetencia(fecha("Fecha (AAAA-MM-DD): "), local, rival, escenario, circuito);
        if (circuito instanceof Ascun ascun) {
            if (ascun.getFase().isEmpty()) ascun.crearFase(TipoFase.GRUPOS, "Fase de grupos");
            List<clubDeportivoSeneca.modelo.competencias.Fase> fases = ascun.getFase();
            for (int i=0;i<fases.size();i++) System.out.println((i+1)+". "+fases.get(i).getNombre());
            clubDeportivoSeneca.modelo.competencias.Fase fase = fases.get(indice("Fase", fases.size()));
            fase.agregarPartido(partido);
        }
        club.registrarPartidoCompetencia(partido);
        System.out.println("Partido de competencia registrado.");
    }

    protected void registrarResultadoCompetencia() throws Exception {
        List<clubDeportivoSeneca.modelo.competencias.PartidoCompetencia> partidos = club.getPartidosCompetencia();
        if (partidos.isEmpty()) 
        	throw new IllegalStateException("No hay partidos de competencia.");
        for (int i=0;i<partidos.size();i++) System.out.println((i+1)+". "+partidos.get(i).getFecha()+" | "+partidos.get(i).getEquipoLocal().getNombre()+" vs "+partidos.get(i).getInstitucionRival()+" | finalizado="+partidos.get(i).isFinalizado());
        clubDeportivoSeneca.modelo.competencias.PartidoCompetencia p = partidos.get(indice("Número de partido", partidos.size()));
        p.registrarMarcador(entero("Marcador local: "), entero("Marcador visitante: "));
        p.finalizarPartido();
        if (p.getCircuito() instanceof TorneoCerros torneo) torneo.registrarResultado(p);
        System.out.println("Resultado registrado y partido finalizado.");
    }

    protected void registrarResultadoIndividual() throws Exception {
        List<PartidoIndividual> partidos = club.getPartidosIndividuales();
        if (partidos.isEmpty()) throw new IllegalStateException("No hay partidos individuales.");
        for (int i = 0; i < partidos.size(); i++) {
            PartidoIndividual p = partidos.get(i);
            System.out.println((i + 1) + ". " + p.getFecha() + " | " + p.getDisciplina().getNombre()
                    + " | " + (p.esDobles() ? "Dobles" : "Individual") + " | finalizado=" + p.isFinalizado());
        }
        PartidoIndividual p = partidos.get(indice("Número de partido", partidos.size()));
        if (p.isFinalizado()) throw new Exception("Este partido ya tiene resultado.");
        List<Jugador> jugadores = p.getParticipantes();
        System.out.println("Elige un integrante del equipo ganador (1 o 3 = equipo A; 2 o 4 = equipo B):");
        for (int i = 0; i < jugadores.size(); i++) {
            System.out.println((i + 1) + ". " + jugadores.get(i).getLogin());
        }
        Jugador ganador = jugadores.get(indice("Ganador", jugadores.size()));
        List<clubDeportivoSeneca.modelo.individual.EstadisticaIndividual> datos = new ArrayList<>();
        for (Jugador jugador : jugadores) {
            System.out.println("Estadísticas de " + jugador.getLogin());
            if (p.getDisciplina() instanceof TenisMesa) {
                datos.add(new EstadisticaTMesa(jugador, entero("Sets: "), entero("Puntos: ")));
            } else {
                datos.add(new EstadisticaTyP(jugador, entero("Sets: "), entero("Games: "),
                        entero("Aces: "), entero("Errores no forzados: ")));
            }
        }
        if (p.esDobles()) {
            p.registrarResultado(ganador, datos.get(0), datos.get(1), datos.get(2), datos.get(3));
            Ranking r1 = buscarOCrearRanking(jugadores.get(0), p.getDisciplina());
            Ranking r2 = buscarOCrearRanking(jugadores.get(1), p.getDisciplina());
            Ranking r3 = buscarOCrearRanking(jugadores.get(2), p.getDisciplina());
            Ranking r4 = buscarOCrearRanking(jugadores.get(3), p.getDisciplina());
            p.actualizarRankings(r1, r2, r3, r4);
        } else {
            p.registrarResultado(ganador, datos.get(0), datos.get(1));
            Ranking r1 = buscarOCrearRanking(jugadores.get(0), p.getDisciplina());
            Ranking r2 = buscarOCrearRanking(jugadores.get(1), p.getDisciplina());
            p.actualizarRankings(r1, r2);
        }
        System.out.println("Resultado registrado y ELO actualizado.");
    }

    protected Ranking buscarOCrearRanking(Jugador jugador, DeporteIndividual disciplina) throws Exception {
        for (Ranking r : club.getRankings()) 
        	if (r.getJugador().equals(jugador) && r.getDisciplina().equals(disciplina)) 
        		return r;
        Ranking nuevo = new Ranking(jugador, disciplina); club.registrarRanking(nuevo); 
        return nuevo;
    }

    protected void solicitarCambioTurno() throws Exception {
        Empleado empleado;
        if (usuarioActual instanceof Empleado e) empleado = e;
        else {
            List<Empleado> empleados = club.getEmpleados();
            if(empleados.isEmpty()) 
            	throw new IllegalStateException("No hay empleados registrados.");
            empleado = empleados.get(indiceLista("Empleado", empleados.stream().map(Empleado::getLogin).toList()));
        }
        List<Turno> turnos = empleado.getTurnosSemanales();
        if(turnos.isEmpty()) 
        	throw new IllegalStateException("Este empleado no tiene turnos asignados.");
        for(int i=0;i<turnos.size();i++) System.out.println((i+1)+". "+turnos.get(i).getFecha()+" "+turnos.get(i).getHoraInicio()+"-"+turnos.get(i).getHoraFin());
        Turno actual = turnos.get(indice("Turno a cambiar",turnos.size()));
        SolicitudCambioTurno solicitud = empleado.solicitarCambioTurno(actual, fecha("Nueva fecha (AAAA-MM-DD): "), hora("Nueva hora inicio (HH:MM): "), hora("Nueva hora fin (HH:MM): "));
        club.getGestorTurnos().registrarSolicitud(solicitud);
        System.out.println("Solicitud creada y pendiente de aprobación.");
    }

    protected void crearEquipo() throws Exception {
        String nombre = leer("Nombre del equipo: ");
        DeporteDeConjunto deporte = elegirDeporteConjunto();
        CategoriaEdad categoria = elegirCategoria();
        int cupo = entero("Cupo máximo: ");
        club.crearEquipo(nombre, deporte, categoria, cupo);
        System.out.println("Equipo creado.");
    }

    protected void crearCompetencia(boolean ascun) throws Exception {
        exigirAdmin();
        String nombre = leer("Nombre: "); String organizador = leer("Entidad organizadora: "); String temporada = leer("Temporada: ");
        DeporteDeConjunto deporte = elegirDeporteConjunto(); CategoriaEdad categoria = elegirCategoria();
        int minimo = entero("Nómina mínima de jugadores: ");
        RequisitosInscripcionCircuito req = new RequisitosInscripcionCircuito(minimo, categoria);
        Circuito circuito = ascun ? new Ascun(nombre, organizador, temporada, deporte, categoria, req) : new TorneoCerros(nombre, organizador, temporada, deporte, categoria, req);
        club.registrarCircuito(circuito); guardarSilencioso(); System.out.println("Competencia registrada.");
        if (ascun && leer("¿Crear una fase inicial? (S/N): ").equalsIgnoreCase("S")) ((Ascun)circuito).crearFase(TipoFase.GRUPOS, leer("Nombre de la fase: "));
    }

    protected void crearInstalacion() throws Exception {
        System.out.println("Tipo: 1. Cancha de tenis  2. Cancha de pádel  3. Mesa de tenis de mesa");
        String tipo = leer("Opción: "); String id = leer("Identificador: "); DeporteIndividual d = elegirDisciplina(); Instalacion i;
        switch (tipo) {
            case "1": i = new CanchaTenis(id, d); break;
            case "2": i = new CanchaPadel(id, d); break;
            case "3": i = new MesaTenisMesa(id, d, leer("¿Techada? (S/N): ").equalsIgnoreCase("S")); break;
            default: throw new IllegalArgumentException("Tipo de instalación no válido.");
        }
        club.registrarInstalacion(i); System.out.println("Instalación registrada.");
    }

    protected void crearReserva() throws Exception {
        Jugador jugador = usuarioActual instanceof Jugador j ? j : elegirJugador(); Instalacion instalacion = elegirInstalacion(); LocalDate fecha = fecha("Fecha (AAAA-MM-DD): ");
        HorarioBloque horario = new HorarioBloque(hora("Hora inicio (HH:MM): "), hora("Hora fin (HH:MM): "));
        Modalidad modalidad = leer("Modalidad: 1. Sencillos  2. Dobles: ").equals("1") ? Modalidad.SENCILLOS : Modalidad.DOBLES;
        int cantidad = modalidad == Modalidad.SENCILLOS ? 2 : 4;
        Reserva reserva = club.crearReserva(jugador, instalacion, fecha, horario, cantidad, modalidad);
        System.out.println("Reserva creada: " + reserva.getFecha() + " " + horario.getHoraInicio() + "-" + horario.getHoraFin());
    }

    protected void ventaTienda() throws Exception {
        Comprador comprador = elegirComprador(); VentaArticulosDeportivos venta = new VentaArticulosDeportivos(LocalDate.now(), comprador);
        boolean agregar = true;
        while (agregar) { Articulo a = elegirArticulo(); 
        int cantidad = entero("Cantidad: "); venta.agregarArticulo(a, cantidad, club.getInventarioArticulos()); agregar = leer("¿Agregar otro artículo? (S/N): ").equalsIgnoreCase("S"); }
        venta.finalizar(club.getInventarioArticulos()); club.registrarVenta(venta); guardarSilencioso(); System.out.println("Venta finalizada. Total: $" + venta.getPrecioTotal());
    }

    protected void ventaCafeteria() throws Exception {
        Comprador comprador = elegirComprador(); VentaCafeteria venta = new VentaCafeteria(LocalDate.now(), comprador);
        boolean seguir = true;
        while (seguir) {
            System.out.println("Producto: 1. Bebida  2. Snack"); String tipo = leer("Opción: ");
            if (tipo.equals("1")) { String nombre = leer("Nombre de bebida: "); int precio = entero("Precio: "); Temperatura t = leer("Temperatura: 1. Fría  2. Caliente: ").equals("2") ? Temperatura.CALIENTE : Temperatura.FRIA; venta.agregarBebida(new Bebida(nombre, precio, t), entero("Cantidad: ")); }
            else 
            	if (tipo.equals("2")) { Snack snack = new Snack(leer("Nombre del snack: "), entero("Precio: ")); if (leer("¿Tiene alérgenos? (S/N): ").equalsIgnoreCase("S")) snack.agregarAlergeno(new Alergeno(leer("Nombre del alérgeno: "))); venta.agregarSnack(snack, entero("Cantidad: ")); }
            else 
            	throw new IllegalArgumentException("Tipo de producto no válido.");
            seguir = leer("¿Agregar otro producto? (S/N): ").equalsIgnoreCase("S");
        }
        if (!venta.getAlergenosAInformar().isEmpty()) System.out.println("Alérgenos que se deben informar: " + venta.getAlergenosAInformar());
        venta.confirmarAlergenos(); venta.finalizar(); club.registrarVenta(venta); guardarSilencioso(); System.out.println("Venta finalizada. Total: $" + venta.getPrecioTotal());
    }

    protected Comprador elegirComprador() {
        String login = usuarioActual instanceof Comprador ? usuarioActual.getLogin() : leer("Login del comprador: "); Usuario u = club.consultarUsuario(login);
        if (!(u instanceof Comprador)) 
        	throw new IllegalArgumentException("El usuario no está registrado como comprador.");
        return (Comprador)u;
    }

    protected List<Jugador> elegirNomina() {
        Equipo equipo = null;

        List<Jugador> disponibles = club.consultarJugadoresActivos();
        if (disponibles.isEmpty()) 
        	throw new IllegalStateException("No hay jugadores activos.");
        System.out.println("Escribe los logins de la nómina separados por coma.");
        for (Jugador j : disponibles) System.out.println(j.getLogin() + " | " + j.getNombre());
        String[] logins = leer("Logins: ").split(","); List<Jugador> resultado = new ArrayList<>();
        for (String login : logins) { Usuario u = club.consultarUsuario(login.trim()); 
        if (!(u instanceof Jugador)) 
        	throw new IllegalArgumentException("No existe jugador: " + login.trim()); resultado.add((Jugador)u); }
        return resultado;
    }

    protected Circuito elegirCircuito() { 
    	if (club.getCircuitos().isEmpty()) 
    		throw new IllegalStateException("No hay competencias registradas."); List<Circuito> l=club.getCircuitos(); 
    		for(int i=0;i<l.size();i++) System.out.println((i+1)+". "+l.get(i).getNombre()); 
    		return l.get(indice("Número de competencia",l.size())); }
    
    protected Equipo elegirEquipo() { 
    	if (club.getEquipos().isEmpty()) 
    		throw new IllegalStateException("No hay equipos registrados."); List<Equipo> l=club.getEquipos(); 
    	for(int i=0;i<l.size();i++) System.out.println((i+1)+". "+l.get(i).getNombre()+" | "+l.get(i).getDeporte()+" | "+l.get(i).getCategoriaEdad()); 
    	return l.get(indice("Número de equipo",l.size())); }
    
    protected Jugador elegirJugador() { 
    	List<Jugador> l=club.consultarJugadoresActivos(); 
    	if(l.isEmpty()) 
    		throw new IllegalStateException("No hay jugadores activos."); 
    	for(int i=0;i<l.size();i++) System.out.println((i+1)+". "+l.get(i).getLogin()+" | "+l.get(i).getNombre()); 
    	return l.get(indice("Número de jugador",l.size())); }
    
    protected Instalacion elegirInstalacion() { 
    	List<Instalacion> l=club.getInventarioInstalaciones().getInstalaciones(); 
    	if(l.isEmpty()) 
    		throw new IllegalStateException("No hay instalaciones. Registra una primero."); 
    	for(int i=0;i<l.size();i++) System.out.println((i+1)+". "+l.get(i).getId()+" | "+l.get(i).getDisciplina().getNombre()); 
    	return l.get(indice("Número de instalación",l.size())); }
    
    protected Entrenamiento elegirEntrenamiento() { 
    	List<Entrenamiento> l=club.getEntrenamientos(); 
    	if(l.isEmpty()) 
    		throw new IllegalStateException("No hay entrenamientos."); 
    	for(int i=0;i<l.size();i++) System.out.println((i+1)+". "+l.get(i).getFecha()+" "+l.get(i).getHora()+" | "+l.get(i).getEntrenador().getLogin()); 
    	return l.get(indice("Número de entrenamiento",l.size())); }
    
    protected Articulo elegirArticulo() { 
    	List<Articulo> articulos = new ArrayList<>(); club.getInventarioArticulos().getStock().values().forEach(m -> m.keySet().forEach(a -> { 
    		if(!articulos.contains(a)) articulos.add(a); })); 
    	if(articulos.isEmpty()) 
    		throw new IllegalStateException("No hay artículos. Registra uno primero."); 
    	for(int i=0;i<articulos.size();i++) System.out.println((i+1)+". "+articulos.get(i).getNombre()+" | $"+articulos.get(i).getPrecio()); 
    	return articulos.get(indice("Número de artículo",articulos.size())); }
    
    protected List<Entrenador> entrenadores() { 
    	List<Entrenador> l=new ArrayList<>(); 
    	for(Usuario u:club.getUsuarios().values()) 
    		if(u instanceof Entrenador e) l.add(e); return l; }
    
    protected DeporteIndividual elegirDisciplina() throws Exception { 
    	System.out.println("Disciplina: 1. Tenis  2. Pádel  3. Tenis de mesa"); switch(leer("Opción: ")) { case "1": return new Tenis("Tenis", 4); case "2": return new Padel("Pádel", 3); case "3": return new TenisMesa("Tenis de mesa", 6); default: throw new IllegalArgumentException("Disciplina no válida."); } }
    
    protected DeporteDeConjunto elegirDeporteConjunto() throws Exception { 	
    	System.out.println("Deporte: 1. Fútbol  2. Baloncesto  3. Voleibol"); 
    	switch(leer("Opción: ")) { 
    	case "1": return new Futbol("Fútbol", 7, true); 
    	case "2": return new Baloncesto("Baloncesto", 5, false); 
    	case "3": return new Voleibol("Voleibol", 6, false); 
    	default: throw new IllegalArgumentException("Deporte no válido."); } }
    
    protected CategoriaEdad elegirCategoria() throws Exception { 
    	System.out.println("Categoría: 1. Sub-13  2. Sub-15  3. Sub-17  4. Mayores"); 
    	switch(leer("Opción: ")) { 
    	case "1": return new CategoriaEdad("Sub-13",0,13); 
    	case "2": return new CategoriaEdad("Sub-15",14,15); 
    	case "3": return new CategoriaEdad("Sub-17",16,17); 
    	case "4": return new CategoriaEdad("Mayores",18,100); 
    	default: throw new IllegalArgumentException("Categoría no válida."); } }
    
    protected NivelJugador elegirNivel() { 
    	System.out.println("Nivel: 1. PRINCIPIANTE  2. INTERMEDIO  3. AVANZADO"); 
    	switch(leer("Opción: ")) { 
    	case "1": return NivelJugador.PRINCIPIANTE; 
    	case "2": return NivelJugador.INTERMEDIO; 
    	case "3": return NivelJugador.AVANZADO; 
    	default: throw new IllegalArgumentException("Nivel no válido."); } }
    
    protected EstadoAsistencia elegirEstadoAsistencia() { 
    	System.out.println("Estado: 1. ASISTIÓ  2. FALTÓ  3. LLEGÓ TARDE"); 
    	switch(leer("Opción: ")) { 
    	case "1": return EstadoAsistencia.ASISTIO; 
    	case "2": return EstadoAsistencia.FALTO; 
    	case "3": return EstadoAsistencia.LLEGO_TARDE; 
    	default: throw new IllegalArgumentException("Estado no válido."); } }
    
    protected TipoSugerencia elegirTipoSugerencia() { 
    	System.out.println("Tipo: 1. Artículo  2. Snack"); 
    	return leer("Opción: ").equals("1") ? TipoSugerencia.ARTICULO : TipoSugerencia.SNACK; }
    
    protected int indice(String pregunta, int cantidad) { 
    	int n=entero(pregunta+" (1-"+cantidad+"): "); 
    	if(n<1||n>cantidad) 
    		throw new IllegalArgumentException("Selección fuera de rango."); 
    	return n-1; }
    
    protected int indiceLista(String pregunta, List<String> nombres) { 
    	for(int i=0;i<nombres.size();i++) System.out.println((i+1)+". "+nombres.get(i)); 
    	return indice(pregunta,nombres.size()); }
    
    protected void mostrarReservas(List<Reserva> reservas) { 
    	for(int i=0;i<reservas.size();i++) System.out.println((i+1)+". "+reservas.get(i)); }
    
    protected void exigirAdmin() { 
    	if (!(usuarioActual instanceof Administrador)) 
    		throw new SecurityException("Esta operación requiere iniciar sesión como administrador."); }
    
    protected String leer(String mensaje) { 
    	System.out.print(mensaje); 
    	return entrada.leer(mensaje); }
    
    protected int entero(String mensaje) { return entrada.entero(mensaje); }

    protected LocalDate fecha(String mensaje) { return entrada.fecha(mensaje); }

    protected LocalTime hora(String mensaje) { return entrada.hora(mensaje); }

    protected void guardarSilencioso() { 
    	try { persistencia.guardar(club); } 
    	catch(Exception e) { throw new IllegalStateException("No se pudieron guardar los datos: " + e.getMessage(), e); } }
    
    protected void error(Exception e) { 
    	System.out.println("No se pudo completar la operación: " + e.getMessage()); }

}
