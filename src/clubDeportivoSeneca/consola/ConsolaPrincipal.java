package clubDeportivoSeneca.consola;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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
import clubDeportivoSeneca.modelo.inventario.InventarioArticulos;
import clubDeportivoSeneca.modelo.inventario.SugerenciaProducto;
import clubDeportivoSeneca.modelo.inventario.TipoSugerencia;
import clubDeportivoSeneca.modelo.partidos.PartidoInterno;
import clubDeportivoSeneca.modelo.turnos.SolicitudCambioTurno;
import clubDeportivoSeneca.modelo.turnos.Turno;
import clubDeportivoSeneca.modelo.usuario.Administrador;
import clubDeportivoSeneca.modelo.usuario.Comprador;
import clubDeportivoSeneca.modelo.usuario.Empleado;
import clubDeportivoSeneca.modelo.usuario.Entrenador;
import clubDeportivoSeneca.modelo.usuario.Fisioterapeuta;
import clubDeportivoSeneca.modelo.usuario.Jugador;
import clubDeportivoSeneca.modelo.usuario.Usuario;
import clubDeportivoSeneca.modelo.ventas.Alergeno;
import clubDeportivoSeneca.modelo.ventas.Bebida;
import clubDeportivoSeneca.modelo.ventas.Periodo;
import clubDeportivoSeneca.modelo.ventas.ProductoCafeteria;
import clubDeportivoSeneca.modelo.ventas.Snack;
import clubDeportivoSeneca.modelo.ventas.Temperatura;
import clubDeportivoSeneca.modelo.ventas.VentaArticulosDeportivos;
import clubDeportivoSeneca.modelo.ventas.VentaCafeteria;
import clubDeportivoSeneca.modelo.ventas.VentaServiciosAdicionales;
import clubDeportivoSeneca.persistencia.ArchivoPlano;
import clubDeportivoSeneca.sistema.ClubDeportivoSeneca;

public class ConsolaPrincipal {
    private final Scanner entrada = new Scanner(System.in);
    private final ArchivoPlano persistencia;
    private ClubDeportivoSeneca club;
    private Usuario usuarioActual;

    public ConsolaPrincipal() {
        String ruta = System.getProperty("cds.archivoDatos");
        persistencia = ruta == null || ruta.isBlank() ? new ArchivoPlano() : new ArchivoPlano(ruta);
    }

    public static void main(String[] args) { new ConsolaPrincipal().iniciar(); }

    public void iniciar() {
        try {
            club = persistencia.cargar();
            if (club == null) {
                club = new ClubDeportivoSeneca();
                club.setAdministrador(new Administrador("admin", "admin123"));
                persistencia.guardar(club);
                System.out.println("Se creó el administrador inicial: admin / admin123");
            } else if (club.getAdministrador() != null) {
                club.getAdministrador().setClub(club);
            }
            boolean seguir = true;
            while (seguir) seguir = usuarioActual == null ? menuPublico() : menuPrincipal();
            persistencia.guardar(club);
            System.out.println("Datos guardados en: " + persistencia.getRutaArchivo());
        } catch (Exception e) {
            error(e);
        }
    }

    private boolean menuPublico() {
        System.out.println("\n========== CLUB DEPORTIVO SÉNECA ==========");
        System.out.println("1. Iniciar sesión\n2. Crear cuenta de socio\n0. Salir");
        switch (leer("Opción: ")) {
            case "1":
                usuarioActual = club.iniciarSesion(leer("Login: "), leer("Contraseña: "));
                System.out.println(usuarioActual == null ? "Credenciales incorrectas." : "Bienvenido/a " + usuarioActual.getLogin());
                return true;
            case "2":
                try {
                    club.crearJugador(leer("Login nuevo: "), leer("Contraseña: "), leer("Nombre completo: "), fecha("Fecha de nacimiento (AAAA-MM-DD): "));
                    guardarSilencioso();
                    System.out.println("Cuenta creada. Ya puedes iniciar sesión.");
                } catch (Exception e) { error(e); }
                return true;
            case "0": return false;
            default: System.out.println("Opción no válida."); return true;
        }
    }

    private boolean menuPrincipal() {
        System.out.println("\n========== MENÚ PRINCIPAL ==========");
        System.out.println("Sesión: " + usuarioActual.getLogin() + " (" + usuarioActual.getClass().getSimpleName() + ")");
        System.out.println("1. Usuarios y roles\n2. Equipos y membresías\n3. Partidos internos\n4. Competencias\n5. Deportes individuales y ranking\n6. Instalaciones y reservas\n7. Entrenamientos y asistencia\n8. Turnos y cambios\n9. Inventario y sugerencias\n10. Ventas y reportes\n11. Consultas generales\n12. Guardar datos\n13. Cerrar sesión\n0. Guardar y salir");
        try {
            switch (leer("Selecciona un módulo: ")) {
                case "1": menuUsuarios(); break;
                case "2": menuEquipos(); break;
                case "3": menuPartidos(); break;
                case "4": menuCompetencias(); break;
                case "5": menuIndividuales(); break;
                case "6": menuInstalaciones(); break;
                case "7": menuEntrenamientos(); break;
                case "8": menuTurnos(); break;
                case "9": menuInventario(); break;
                case "10": menuVentas(); break;
                case "11": consultas(); break;
                case "12": persistencia.guardar(club); System.out.println("Datos guardados correctamente."); break;
                case "13": usuarioActual = null; break;
                case "0": return false;
                default: System.out.println("Opción no válida.");
            }
        } catch (Exception e) { error(e); }
        return true;
    }

    private void menuUsuarios() {
        System.out.println("\n--- USUARIOS ---\n1. Listar usuarios\n2. Crear jugador\n3. Crear entrenador\n4. Crear fisioterapeuta\n5. Dar de baja jugador\n0. Volver");
        try {
            switch (leer("Opción: ")) {
                case "1": club.getUsuarios().values().forEach(u -> System.out.println(u.getClass().getSimpleName() + " | " + u.getLogin() + (u instanceof Jugador j ? " | " + j.getNombre() + " | activo=" + j.isActivo() : ""))); break;
                case "2": exigirAdmin(); club.crearJugador(leer("Login: "), leer("Contraseña: "), leer("Nombre: "), fecha("Nacimiento (AAAA-MM-DD): ")); guardarSilencioso(); System.out.println("Jugador creado."); break;
                case "3": exigirAdmin(); club.registrarUsuario(new Entrenador(leer("Login: "), leer("Contraseña: "), leer("Nombre: "), leer("Código de descuento: "))); guardarSilencioso(); System.out.println("Entrenador creado."); break;
                case "4": exigirAdmin(); club.registrarUsuario(new Fisioterapeuta(leer("Login: "), leer("Contraseña: "), leer("Nombre: "), leer("Código de descuento: "))); guardarSilencioso(); System.out.println("Fisioterapeuta creado."); break;
                case "5": exigirAdmin(); Jugador jugador = elegirJugador(); club.darBajaJugador(jugador); guardarSilencioso(); System.out.println("Jugador dado de baja."); break;
                case "0": return;
                default: System.out.println("Opción no válida.");
            }
        } catch (Exception e) { error(e); }
    }

    private void menuEquipos() {
        System.out.println("\n--- EQUIPOS Y MEMBRESÍAS ---\n1. Listar equipos\n2. Crear equipo\n3. Inscribir jugador en equipo\n4. Dar de baja equipo\n0. Volver");
        try {
            switch (leer("Opción: ")) {
                case "1": 
                	if (club.getEquipos().isEmpty()) System.out.println("No hay equipos."); 
                	else club.getEquipos().forEach(e -> System.out.println(e + " | deporte=" + e.getDeporte() + " | categoría=" + e.getCategoriaEdad() + " | miembros=" + e.getMiembros().size() + " | activo=" + e.isActivo())); break;
                case "2": exigirAdmin(); crearEquipo(); guardarSilencioso(); break;
                case "3": exigirAdmin(); Equipo equipo = elegirEquipo(); Jugador j = elegirJugador(); club.inscribirJugadorEnEquipo(equipo, j, leer("Posición: "), entero("Número de camiseta: ")); guardarSilencioso(); System.out.println("Inscripción realizada."); break;
                case "4": exigirAdmin(); club.darBajaEquipo(elegirEquipo()); guardarSilencioso(); System.out.println("Equipo dado de baja."); break;
                case "0": return;
                default: System.out.println("Opción no válida.");
            }
        } catch (Exception e) { error(e); }
    }

    private void menuPartidos() {
        System.out.println("\n--- PARTIDOS INTERNOS ---\n1. Listar partidos\n2. Crear partido interno\n3. Registrar marcador y finalizar partido\n0. Volver");
        try {
            switch (leer("Opción: ")) {
                case "1": 
                	if (club.getPartidosInternos().isEmpty()) System.out.println("No hay partidos internos."); 
                	else club.getPartidosInternos().forEach(System.out::println); break;
                case "2": exigirAdmin(); Equipo local = elegirEquipo(); Equipo visitante = elegirEquipo(); 
                if (local == visitante) 
                	throw new IllegalArgumentException("Debes elegir dos equipos distintos."); PartidoInterno p = new PartidoInterno(fecha("Fecha (AAAA-MM-DD): "), local.getDeporte(), local, visitante); club.registrarPartidoInterno(p); guardarSilencioso(); System.out.println("Partido creado."); break;
                case "3": exigirAdmin(); List<PartidoInterno> partidos = club.getPartidosInternos(); 
                if (partidos.isEmpty()) 
                	throw new IllegalStateException("No hay partidos internos."); 
                for (int i=0;i<partidos.size();i++) System.out.println((i+1)+". "+partidos.get(i).getFecha()+" | "+partidos.get(i).getEquipoLocal().getNombre()+" vs "+partidos.get(i).getEquipoVisitante().getNombre()+" | finalizado="+partidos.get(i).isFinalizado()); PartidoInterno partido = partidos.get(indice("Número de partido", partidos.size())); partido.registrarMarcador(entero("Marcador local: "), entero("Marcador visitante: ")); partido.finalizarPartido(); guardarSilencioso(); System.out.println("Marcador registrado y partido finalizado."); break;
                case "0": return;
                default: System.out.println("Opción no válida.");
            }
        } catch (Exception e) { error(e); }
    }

    private void menuCompetencias() {
        System.out.println("\n--- COMPETENCIAS ---\n1. Listar competencias\n2. Crear ASCUN\n3. Crear Torneo de los Cerros\n4. Inscribir equipo y nómina\n5. Crear partido de competencia\n6. Registrar marcador/finalizar partido\n7. Ver clasificación de competencia\n0. Volver");
        try {
            switch (leer("Opción: ")) {
                case "1": 
                	if (club.getCircuitos().isEmpty()) System.out.println("No hay competencias."); 
                	else club.getCircuitos().forEach(c -> System.out.println(c.getNombre() + " | " + c.getDeporte() + " | temporada=" + c.getTemporada() + " | equipos inscritos=" + c.getInscritos().size())); break;
                case "2": crearCompetencia(true); break;
                case "3": crearCompetencia(false); break;
                case "4": exigirAdmin(); Circuito circuito = elegirCircuito(); Equipo equipo = elegirEquipo(); List<Jugador> nomina = elegirNomina(); club.inscribirEquipoEnCompetencia(circuito, equipo, nomina); guardarSilencioso(); System.out.println("Equipo inscrito en la competencia."); break;
                case "5": exigirAdmin(); crearPartidoCompetencia(); guardarSilencioso(); break;
                case "6": exigirAdmin(); registrarResultadoCompetencia(); guardarSilencioso(); break;
                case "7": Circuito c = elegirCircuito(); List<String> clasificacion = c.calcularClasificacion(); 
                if (clasificacion.isEmpty()) System.out.println("Todavía no hay resultados para calcular una clasificación."); 
                else 
                	for(int i=0;i<clasificacion.size();i++) System.out.println((i+1)+". "+clasificacion.get(i)); break;
                case "0": return;
                default: System.out.println("Opción no válida.");
            }
        } catch (Exception e) { error(e); }
    }

    private void menuIndividuales() {
        System.out.println("\n--- DEPORTES INDIVIDUALES ---\n1. Ver partidos individuales\n2. Ver rankings\n3. Crear ranking ELO\n4. Asignar nivel a jugador\n5. Crear partido individual\n6. Registrar resultado y actualizar ELO\n0. Volver");
        try {
            switch (leer("Opción: ")) {
                case "1": 
                	if (club.getPartidosIndividuales().isEmpty()) System.out.println("No hay partidos individuales."); 
                	else club.getPartidosIndividuales().forEach(System.out::println); break;
                case "2": 
                	if (club.getRankings().isEmpty()) System.out.println("No hay rankings."); 
                	else club.getRankings().forEach(r -> System.out.println(r.getJugador().getLogin() + " | " + r.getDisciplina().getNombre() + " | ELO=" + r.getPuntaje())); break;
                case "3": exigirAdmin(); Jugador jugador = elegirJugador(); DeporteIndividual disciplina = elegirDisciplina(); Ranking ranking = new Ranking(jugador, disciplina); club.registrarRanking(ranking); guardarSilencioso(); System.out.println("Ranking creado con ELO inicial " + ranking.getPuntaje()); break;
                case "4": Jugador j = elegirJugador(); DeporteIndividual d = elegirDisciplina(); NivelJugador nivel = elegirNivel(); j.asignarNivel(d, nivel); guardarSilencioso(); System.out.println("Nivel asignado."); break;
                case "5": Jugador j1 = elegirJugador(); Jugador j2 = elegirJugador(); PartidoIndividual pi = new PartidoIndividual(fecha("Fecha (AAAA-MM-DD): "), elegirDisciplina(), j1, j2); club.registrarPartidoIndividual(pi); guardarSilencioso(); System.out.println("Partido individual creado."); break;
                case "6": registrarResultadoIndividual(); guardarSilencioso(); break;
                case "0": return;
                default: System.out.println("Opción no válida.");
            }
        } catch (Exception e) { error(e); }
    }

    private void menuInstalaciones() {
        System.out.println("\n--- INSTALACIONES Y RESERVAS ---\n1. Listar instalaciones\n2. Registrar instalación\n3. Ver historial de reservas\n4. Crear reserva\n5. Cancelar reserva\n0. Volver");
        try {
            switch (leer("Opción: ")) {
                case "1": List<Instalacion> instalaciones = club.getInventarioInstalaciones().getInstalaciones(); 
                if (instalaciones.isEmpty()) System.out.println("No hay instalaciones.");
                else instalaciones.forEach(i -> System.out.println(i.getId() + " | " + i.getDisciplina().getNombre() + " | capacidades=" + i.getCapacidadesPermitidas())); break;
                case "2": exigirAdmin(); crearInstalacion(); guardarSilencioso(); break;
                case "3": List<Reserva> reservas = club.getInventarioInstalaciones().getHistorial().getReservas(); 
                if (reservas.isEmpty()) System.out.println("No hay reservas."); else reservas.forEach(System.out::println); break;
                case "4": crearReserva(); guardarSilencioso(); break;
                case "5": exigirAdmin(); List<Reserva> activas = club.getInventarioInstalaciones().getHistorial().getReservas(); 
                if (activas.isEmpty()) throw new IllegalStateException("No hay reservas para cancelar."); mostrarReservas(activas); Reserva r = activas.get(indice("Número de reserva", activas.size())); club.cancelarReserva(r); guardarSilencioso(); System.out.println("Reserva cancelada."); break;
                case "0": return;
                default: System.out.println("Opción no válida.");
            }
        } catch (Exception e) { error(e); }
    }

    private void menuEntrenamientos() {
        System.out.println("\n--- ENTRENAMIENTOS ---\n1. Listar entrenamientos\n2. Planear entrenamiento\n3. Convocar equipo a entrenamiento\n4. Registrar asistencia\n5. Registrar evaluación\n0. Volver");
        try {
            switch (leer("Opción: ")) {
                case "1": 
                	if (club.getEntrenamientos().isEmpty()) System.out.println("No hay entrenamientos."); 
                	else club.getEntrenamientos().forEach(e -> System.out.println(e.getFecha() + " " + e.getHora() + " | entrenador=" + e.getEntrenador().getLogin() + " | convocados=" + e.getConvocados().size())); break;
                case "2": exigirAdmin(); List<Entrenador> entrenadores = entrenadores(); 
                if (entrenadores.isEmpty()) 
                	throw new IllegalStateException("Primero registra un entrenador."); Entrenador entrenador = entrenadores.get(indiceLista("Entrenador", entrenadores.stream().map(Entrenador::getLogin).toList())); Instalacion instalacion = elegirInstalacion(); Entrenamiento ent = club.planearEntrenamiento(entrenador, fecha("Fecha (AAAA-MM-DD): "), hora("Hora (HH:MM): "), entero("Duración en minutos: "), instalacion); guardarSilencioso(); System.out.println("Entrenamiento planeado."); break;
                case "3": Entrenamiento entrenamiento = elegirEntrenamiento(); entrenamiento.convocarEquipo(elegirEquipo()); guardarSilencioso(); System.out.println("Equipo convocado."); break;
                case "4": Entrenamiento e = elegirEntrenamiento(); Jugador j = elegirJugador(); EstadoAsistencia estado = elegirEstadoAsistencia(); e.registrarAsistencia(j, estado); guardarSilencioso(); System.out.println("Asistencia registrada."); break;
                case "5": Entrenamiento ev = elegirEntrenamiento(); Jugador je = elegirJugador(); ev.registrarEvaluacion(je, entero("Nota (1-5): "), leer("Comentario: ")); guardarSilencioso(); System.out.println("Evaluación registrada."); break;
                case "0": return;
                default: System.out.println("Opción no válida.");
            }
        } catch (Exception e) { error(e); }
    }

    private void menuTurnos() {
        System.out.println("\n--- TURNOS ---\n1. Listar turnos\n2. Asignar turno\n3. Verificar mínimos de personal por fecha\n4. Listar solicitudes de cambio\n5. Solicitar cambio de turno\n6. Aprobar/rechazar solicitud (administrador)\n0. Volver");
        try {
            switch (leer("Opción: ")) {
                case "1": 
                	if (club.getGestorTurnos().getTurnos().isEmpty()) System.out.println("No hay turnos."); 
                	else club.getGestorTurnos().getTurnos().forEach(t -> System.out.println(t.getEmpleado().getLogin() + " | " + t.getFecha() + " | " + t.getHoraInicio() + "-" + t.getHoraFin())); break;
                case "2": exigirAdmin(); List<Empleado> empleados = club.getEmpleados(); 
                if (empleados.isEmpty()) 
                	throw new IllegalStateException("No hay empleados registrados."); Empleado emp = empleados.get(indiceLista("Empleado", empleados.stream().map(Empleado::getNombre).toList())); Turno turno = club.getGestorTurnos().asignarTurno(emp, fecha("Fecha (AAAA-MM-DD): "), hora("Hora inicio (HH:MM): "), hora("Hora fin (HH:MM): ")); guardarSilencioso(); System.out.println("Turno asignado: " + turno.getHoraInicio() + "-" + turno.getHoraFin()); break;
                case "3": boolean cumple = club.getGestorTurnos().cumpleMinimos(fecha("Fecha (AAAA-MM-DD): ")); System.out.println(cumple ? "Se cumplen los mínimos de personal." : "No se cumplen los mínimos: se requieren 2 entrenadores y 1 fisioterapeuta por franja."); break;
                case "4": if (club.getGestorTurnos().getSolicitudes().isEmpty()) System.out.println("No hay solicitudes."); 
                else club.getGestorTurnos().getSolicitudes().forEach(s -> System.out.println(s.getEmpleado().getLogin() + " | " + s.getEstado() + " | nueva fecha=" + s.getNuevaFecha())); break;
                case "5": solicitarCambioTurno(); guardarSilencioso(); break;
                case "6": exigirAdmin(); List<SolicitudCambioTurno> solicitudes = club.getGestorTurnos().getSolicitudes(); 
                if(solicitudes.isEmpty()) 
                	throw new IllegalStateException("No hay solicitudes."); 
                for(int i=0;i<solicitudes.size();i++) System.out.println((i+1)+". "+solicitudes.get(i).getEmpleado().getLogin()+" | "+solicitudes.get(i).getEstado()+" | "+solicitudes.get(i).getNuevaFecha()); SolicitudCambioTurno sol = solicitudes.get(indice("Número de solicitud",solicitudes.size())); String resp=leer("Aprobar (A) o rechazar (R): "); 
                if(resp.equalsIgnoreCase("A")) System.out.println(club.getGestorTurnos().aprobarCambio(sol) ? "Cambio aprobado." : "Cambio rechazado porque incumple las reglas."); 
                else if(resp.equalsIgnoreCase("R")) { club.getGestorTurnos().rechazarCambio(sol); System.out.println("Cambio rechazado."); } 
                else throw new IllegalArgumentException("Respuesta no válida."); guardarSilencioso(); break;
                case "0": return;
                default: System.out.println("Opción no válida.");
            }
        } catch (Exception e) { error(e); }
    }

    private void menuInventario() {
        System.out.println("\n--- INVENTARIO Y SUGERENCIAS ---\n1. Ver stock\n2. Registrar artículo\n3. Reabastecer artículo\n4. Crear sugerencia de empleado\n5. Ver sugerencias\n6. Aprobar/rechazar sugerencia\n0. Volver");
        try {
            switch (leer("Opción: ")) {
                case "1": Map<String, Map<Articulo, Integer>> stock = club.getInventarioArticulos().getStock(); 
                if (stock.isEmpty()) System.out.println("Inventario vacío."); 
                else stock.forEach((ubicacion, articulos) -> articulos.forEach((a, cantidad) -> System.out.println(ubicacion + " | " + a.getNombre() + " | precio=$" + a.getPrecio() + " | cantidad=" + cantidad))); break;
                case "2": exigirAdmin(); Articulo articulo = new Articulo(leer("Nombre: "), leer("Categoría: "), entero("Precio en pesos: ")); club.registrarArticulo(articulo, entero("Cantidad inicial: ")); guardarSilencioso(); System.out.println("Artículo registrado."); break;
                case "3": exigirAdmin(); Articulo a = elegirArticulo(); club.getInventarioArticulos().reabastecer(a, entero("Cantidad a agregar: "), InventarioArticulos.UBICACION_TIENDA); guardarSilencioso(); System.out.println("Stock actualizado."); break;
                case "4": List<Empleado> emps = club.getEmpleados(); 
                if (emps.isEmpty()) 
                	throw new IllegalStateException("No hay empleados."); Empleado autor = emps.get(indiceLista("Empleado", emps.stream().map(Empleado::getLogin).toList())); TipoSugerencia tipo = elegirTipoSugerencia(); SugerenciaProducto sug = new SugerenciaProducto(autor, leer("Nombre del producto sugerido: "), leer("Descripción: "), tipo); club.registrarSugerencia(sug); guardarSilencioso(); System.out.println("Sugerencia registrada."); break;
                case "5": 
                	if (club.getSugerencias().isEmpty()) System.out.println("No hay sugerencias."); 
                	else club.getSugerencias().forEach(s -> System.out.println(s.getNombre() + " | " + s.getTipo() + " | " + s.getEstado() + " | empleado=" + s.getEmpleado().getLogin())); break;
                case "6": exigirAdmin(); 
                if (club.getSugerencias().isEmpty()) 
                	throw new IllegalStateException("No hay sugerencias."); List<SugerenciaProducto> sugerencias = club.getSugerencias(); for (int i=0;i<sugerencias.size();i++) System.out.println((i+1)+". "+sugerencias.get(i).getNombre()+" ("+sugerencias.get(i).getEstado()+")"); SugerenciaProducto s = sugerencias.get(indice("Número de sugerencia", sugerencias.size())); String decision = leer("Aprobar (A) o rechazar (R): "); if (decision.equalsIgnoreCase("A")) s.aprobar(); else if (decision.equalsIgnoreCase("R")) s.rechazar(); else throw new IllegalArgumentException("Decisión no válida."); guardarSilencioso(); System.out.println("Sugerencia actualizada."); break;
                case "0": return;
                default: System.out.println("Opción no válida.");
            }
        } catch (Exception e) { error(e); }
    }

    private void menuVentas() {
        System.out.println("\n--- VENTAS Y REPORTES ---\n1. Listar ventas\n2. Venta de artículos deportivos\n3. Venta de cafetería\n4. Reporte de ventas por rango de fechas\n5. Reporte resumido por rubro\n0. Volver");
        try {
            switch (leer("Opción: ")) {
                case "1": 
                	if (club.getVentas().isEmpty()) System.out.println("No hay ventas."); 
                	else club.getVentas().forEach(v -> System.out.println(v.getFecha() + " | " + v.getRubro() + " | comprador=" + v.getComprador().getLogin() + " | total=$" + v.getPrecioTotal() + " | finalizada=" + v.isFinalizada())); break;
                case "2": ventaTienda(); break;
                case "3": ventaCafeteria(); break;
                case "4": LocalDate desde = fecha("Desde (AAAA-MM-DD): "); LocalDate hasta = fecha("Hasta (AAAA-MM-DD): "); club.consultarVentas(desde, hasta).forEach(v -> System.out.println(v.getFecha()+" | "+v.getRubro()+" | $"+v.getPrecioTotal())); break;
                case "5": 
                	if (usuarioActual instanceof Administrador admin) admin.reporteVentas(); 
                else System.out.println("Solo el administrador puede consultar el reporte consolidado."); break;
                case "0": return;
                default: System.out.println("Opción no válida.");
            }
        } catch (Exception e) { error(e); }
    }

    private void crearPartidoCompetencia() throws Exception {
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

    private void registrarResultadoCompetencia() throws Exception {
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

    private void registrarResultadoIndividual() throws Exception {
        List<PartidoIndividual> partidos = club.getPartidosIndividuales();
        if (partidos.isEmpty()) 
        	throw new IllegalStateException("No hay partidos individuales.");
        for(int i=0;i<partidos.size();i++) { PartidoIndividual p=partidos.get(i); System.out.println((i+1)+". "+p.getFecha()+" | "+p.getJugador1().getLogin()+" vs "+p.getJugador2().getLogin()+" | "+p.getDisciplina().getNombre()+" | finalizado="+p.isFinalizado()); }
        PartidoIndividual p = partidos.get(indice("Número de partido",partidos.size()));
        System.out.println("Ganador: 1. "+p.getJugador1().getLogin()+"  2. "+p.getJugador2().getLogin());
        Jugador ganador = leer("Opción: ").equals("1") ? p.getJugador1() : p.getJugador2();
        if (p.getDisciplina() instanceof TenisMesa) {
            EstadisticaTMesa e1 = new EstadisticaTMesa(p.getJugador1(), entero("Sets jugador 1: "), entero("Puntos jugador 1: "));
            EstadisticaTMesa e2 = new EstadisticaTMesa(p.getJugador2(), entero("Sets jugador 2: "), entero("Puntos jugador 2: "));
            p.registrarResultado(ganador,e1,e2);
        } else {
            EstadisticaTyP e1 = new EstadisticaTyP(p.getJugador1(), entero("Sets jugador 1: "), entero("Games jugador 1: "), entero("Aces jugador 1: "), entero("Errores no forzados jugador 1: "));
            EstadisticaTyP e2 = new EstadisticaTyP(p.getJugador2(), entero("Sets jugador 2: "), entero("Games jugador 2: "), entero("Aces jugador 2: "), entero("Errores no forzados jugador 2: "));
            p.registrarResultado(ganador,e1,e2);
        }
        Ranking r1 = buscarOCrearRanking(p.getJugador1(), p.getDisciplina());
        Ranking r2 = buscarOCrearRanking(p.getJugador2(), p.getDisciplina());
        p.actualizarRankings(r1,r2);
        System.out.println("Resultado guardado. ELO actualizado: "+r1.getJugador().getLogin()+"="+r1.getPuntaje()+", "+r2.getJugador().getLogin()+"="+r2.getPuntaje());
    }

    private Ranking buscarOCrearRanking(Jugador jugador, DeporteIndividual disciplina) throws Exception {
        for (Ranking r : club.getRankings()) 
        	if (r.getJugador().equals(jugador) && r.getDisciplina().equals(disciplina)) 
        		return r;
        Ranking nuevo = new Ranking(jugador, disciplina); club.registrarRanking(nuevo); 
        return nuevo;
    }

    private void solicitarCambioTurno() throws Exception {
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

    private void consultas() {
        System.out.println("\n--- CONSULTAS Y REPORTES ---\n1. Resumen general\n2. Reporte de resultados\n3. Reporte de asistencia\n4. Reporte de rankings\n5. Historial de reservas\n6. Historial de entrenamientos\n7. Reporte de ventas\n0. Volver");
        switch (leer("Opción: ")) {
            case "1":
                System.out.println("Usuarios: " + club.getUsuarios().size() + " | jugadores activos: " + club.consultarJugadoresActivos().size());
                System.out.println("Equipos: " + club.getEquipos().size() + " | competencias: " + club.getCircuitos().size());
                System.out.println("Partidos internos: " + club.getPartidosInternos().size() + " | partidos de competencia: " + club.getPartidosCompetencia().size() + " | partidos individuales: " + club.getPartidosIndividuales().size());
                System.out.println("Instalaciones: " + club.getInventarioInstalaciones().getInstalaciones().size() + " | reservas: " + club.getInventarioInstalaciones().getHistorial().getReservas().size());
                System.out.println("Entrenamientos: " + club.getEntrenamientos().size() + " | turnos: " + club.getGestorTurnos().getTurnos().size() + " | ventas: " + club.getVentas().size()); break;
            case "2": 
            	if (usuarioActual instanceof Administrador admin) admin.reporteResultados(); 
            	else System.out.println("Reporte disponible solo para el administrador."); break;
            case "3": 
            	if (usuarioActual instanceof Administrador admin) admin.reporteAsistencia(); 
            	else System.out.println("Reporte disponible solo para el administrador."); break;
            case "4": 
            	if (usuarioActual instanceof Administrador admin) admin.reporteRankings(); 
            	else club.getRankings().forEach(r -> System.out.println(r.getJugador().getLogin()+" | "+r.getDisciplina().getNombre()+" | ELO="+r.getPuntaje())); break;
            case "5": club.getInventarioInstalaciones().getHistorial().getReservas().forEach(System.out::println); break;
            case "6": club.getEntrenamientos().forEach(System.out::println); break;
            case "7": 
            	if (usuarioActual instanceof Administrador admin) admin.reporteVentas(); 
            	else System.out.println("Reporte disponible solo para el administrador."); break;
            case "0": return;
            default: System.out.println("Opción no válida.");
        }
    }

    private void crearEquipo() throws Exception {
        String nombre = leer("Nombre del equipo: ");
        DeporteDeConjunto deporte = elegirDeporteConjunto();
        CategoriaEdad categoria = elegirCategoria();
        int cupo = entero("Cupo máximo: ");
        club.crearEquipo(nombre, deporte, categoria, cupo);
        System.out.println("Equipo creado.");
    }

    private void crearCompetencia(boolean ascun) throws Exception {
        exigirAdmin();
        String nombre = leer("Nombre: "); String organizador = leer("Entidad organizadora: "); String temporada = leer("Temporada: ");
        DeporteDeConjunto deporte = elegirDeporteConjunto(); CategoriaEdad categoria = elegirCategoria();
        int minimo = entero("Nómina mínima de jugadores: ");
        RequisitosInscripcionCircuito req = new RequisitosInscripcionCircuito(minimo, categoria);
        Circuito circuito = ascun ? new Ascun(nombre, organizador, temporada, deporte, categoria, req) : new TorneoCerros(nombre, organizador, temporada, deporte, categoria, req);
        club.registrarCircuito(circuito); guardarSilencioso(); System.out.println("Competencia registrada.");
        if (ascun && leer("¿Crear una fase inicial? (S/N): ").equalsIgnoreCase("S")) ((Ascun)circuito).crearFase(TipoFase.GRUPOS, leer("Nombre de la fase: "));
    }

    private void crearInstalacion() throws Exception {
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

    private void crearReserva() throws Exception {
        Jugador jugador = elegirJugador(); Instalacion instalacion = elegirInstalacion(); LocalDate fecha = fecha("Fecha (AAAA-MM-DD): ");
        HorarioBloque horario = new HorarioBloque(hora("Hora inicio (HH:MM): "), hora("Hora fin (HH:MM): "));
        Modalidad modalidad = leer("Modalidad: 1. Sencillos  2. Dobles: ").equals("1") ? Modalidad.SENCILLOS : Modalidad.DOBLES;
        int cantidad = modalidad == Modalidad.SENCILLOS ? 2 : 4;
        Reserva reserva = club.crearReserva(jugador, instalacion, fecha, horario, cantidad, modalidad);
        System.out.println("Reserva creada: " + reserva.getFecha() + " " + horario.getHoraInicio() + "-" + horario.getHoraFin());
    }

    private void ventaTienda() throws Exception {
        Comprador comprador = elegirComprador(); VentaArticulosDeportivos venta = new VentaArticulosDeportivos(LocalDate.now(), comprador);
        boolean agregar = true;
        while (agregar) { Articulo a = elegirArticulo(); 
        int cantidad = entero("Cantidad: "); venta.agregarArticulo(a, cantidad, club.getInventarioArticulos()); agregar = leer("¿Agregar otro artículo? (S/N): ").equalsIgnoreCase("S"); }
        venta.finalizar(club.getInventarioArticulos()); club.registrarVenta(venta); guardarSilencioso(); System.out.println("Venta finalizada. Total: $" + venta.getPrecioTotal());
    }

    private void ventaCafeteria() throws Exception {
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

    private Comprador elegirComprador() {
        String login = leer("Login del comprador: "); Usuario u = club.consultarUsuario(login);
        if (!(u instanceof Comprador)) 
        	throw new IllegalArgumentException("El usuario no está registrado como comprador.");
        return (Comprador)u;
    }

    private List<Jugador> elegirNomina() {
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

    private Circuito elegirCircuito() { 
    	if (club.getCircuitos().isEmpty()) 
    		throw new IllegalStateException("No hay competencias registradas."); List<Circuito> l=club.getCircuitos(); 
    		for(int i=0;i<l.size();i++) System.out.println((i+1)+". "+l.get(i).getNombre()); 
    		return l.get(indice("Número de competencia",l.size())); }
    
    private Equipo elegirEquipo() { 
    	if (club.getEquipos().isEmpty()) 
    		throw new IllegalStateException("No hay equipos registrados."); List<Equipo> l=club.getEquipos(); 
    	for(int i=0;i<l.size();i++) System.out.println((i+1)+". "+l.get(i).getNombre()+" | "+l.get(i).getDeporte()+" | "+l.get(i).getCategoriaEdad()); 
    	return l.get(indice("Número de equipo",l.size())); }
    
    private Jugador elegirJugador() { 
    	List<Jugador> l=club.consultarJugadoresActivos(); 
    	if(l.isEmpty()) 
    		throw new IllegalStateException("No hay jugadores activos."); 
    	for(int i=0;i<l.size();i++) System.out.println((i+1)+". "+l.get(i).getLogin()+" | "+l.get(i).getNombre()); 
    	return l.get(indice("Número de jugador",l.size())); }
    
    private Instalacion elegirInstalacion() { 
    	List<Instalacion> l=club.getInventarioInstalaciones().getInstalaciones(); 
    	if(l.isEmpty()) 
    		throw new IllegalStateException("No hay instalaciones. Registra una primero."); 
    	for(int i=0;i<l.size();i++) System.out.println((i+1)+". "+l.get(i).getId()+" | "+l.get(i).getDisciplina().getNombre()); 
    	return l.get(indice("Número de instalación",l.size())); }
    
    private Entrenamiento elegirEntrenamiento() { 
    	List<Entrenamiento> l=club.getEntrenamientos(); 
    	if(l.isEmpty()) 
    		throw new IllegalStateException("No hay entrenamientos."); 
    	for(int i=0;i<l.size();i++) System.out.println((i+1)+". "+l.get(i).getFecha()+" "+l.get(i).getHora()+" | "+l.get(i).getEntrenador().getLogin()); 
    	return l.get(indice("Número de entrenamiento",l.size())); }
    
    private Articulo elegirArticulo() { 
    	List<Articulo> articulos = new ArrayList<>(); club.getInventarioArticulos().getStock().values().forEach(m -> m.keySet().forEach(a -> { 
    		if(!articulos.contains(a)) articulos.add(a); })); 
    	if(articulos.isEmpty()) 
    		throw new IllegalStateException("No hay artículos. Registra uno primero."); 
    	for(int i=0;i<articulos.size();i++) System.out.println((i+1)+". "+articulos.get(i).getNombre()+" | $"+articulos.get(i).getPrecio()); 
    	return articulos.get(indice("Número de artículo",articulos.size())); }
    
    private List<Entrenador> entrenadores() { 
    	List<Entrenador> l=new ArrayList<>(); 
    	for(Usuario u:club.getUsuarios().values()) 
    		if(u instanceof Entrenador e) l.add(e); return l; }
    
    private DeporteIndividual elegirDisciplina() throws Exception { 
    	System.out.println("Disciplina: 1. Tenis  2. Pádel  3. Tenis de mesa"); switch(leer("Opción: ")) { case "1": return new Tenis("Tenis", 4); case "2": return new Padel("Pádel", 3); case "3": return new TenisMesa("Tenis de mesa", 6); default: throw new IllegalArgumentException("Disciplina no válida."); } }
    
    private DeporteDeConjunto elegirDeporteConjunto() throws Exception { 	
    	System.out.println("Deporte: 1. Fútbol  2. Baloncesto  3. Voleibol"); 
    	switch(leer("Opción: ")) { 
    	case "1": return new Futbol("Fútbol", 7, true); 
    	case "2": return new Baloncesto("Baloncesto", 5, false); 
    	case "3": return new Voleibol("Voleibol", 6, false); 
    	default: throw new IllegalArgumentException("Deporte no válido."); } }
    
    private CategoriaEdad elegirCategoria() throws Exception { 
    	System.out.println("Categoría: 1. Sub-13  2. Sub-15  3. Sub-17  4. Mayores"); 
    	switch(leer("Opción: ")) { 
    	case "1": return new CategoriaEdad("Sub-13",0,13); 
    	case "2": return new CategoriaEdad("Sub-15",14,15); 
    	case "3": return new CategoriaEdad("Sub-17",16,17); 
    	case "4": return new CategoriaEdad("Mayores",18,100); 
    	default: throw new IllegalArgumentException("Categoría no válida."); } }
    
    private NivelJugador elegirNivel() { 
    	System.out.println("Nivel: 1. PRINCIPIANTE  2. INTERMEDIO  3. AVANZADO"); 
    	switch(leer("Opción: ")) { 
    	case "1": return NivelJugador.PRINCIPIANTE; 
    	case "2": return NivelJugador.INTERMEDIO; 
    	case "3": return NivelJugador.AVANZADO; 
    	default: throw new IllegalArgumentException("Nivel no válido."); } }
    
    private EstadoAsistencia elegirEstadoAsistencia() { 
    	System.out.println("Estado: 1. ASISTIÓ  2. FALTÓ  3. LLEGÓ TARDE"); 
    	switch(leer("Opción: ")) { 
    	case "1": return EstadoAsistencia.ASISTIO; 
    	case "2": return EstadoAsistencia.FALTO; 
    	case "3": return EstadoAsistencia.LLEGO_TARDE; 
    	default: throw new IllegalArgumentException("Estado no válido."); } }
    
    private TipoSugerencia elegirTipoSugerencia() { 
    	System.out.println("Tipo: 1. Artículo  2. Snack"); 
    	return leer("Opción: ").equals("1") ? TipoSugerencia.ARTICULO : TipoSugerencia.SNACK; }
    
    private int indice(String pregunta, int cantidad) { 
    	int n=entero(pregunta+" (1-"+cantidad+"): "); 
    	if(n<1||n>cantidad) 
    		throw new IllegalArgumentException("Selección fuera de rango."); 
    	return n-1; }
    
    private int indiceLista(String pregunta, List<String> nombres) { 
    	for(int i=0;i<nombres.size();i++) System.out.println((i+1)+". "+nombres.get(i)); 
    	return indice(pregunta,nombres.size()); }
    
    private void mostrarReservas(List<Reserva> reservas) { 
    	for(int i=0;i<reservas.size();i++) System.out.println((i+1)+". "+reservas.get(i)); }
    
    private void exigirAdmin() { 
    	if (!(usuarioActual instanceof Administrador)) 
    		throw new SecurityException("Esta operación requiere iniciar sesión como administrador."); }
    
    private String leer(String mensaje) { 
    	System.out.print(mensaje); 
    	return entrada.nextLine().trim(); }
    
    private int entero(String mensaje) { 
    	try { 
    		return Integer.parseInt(leer(mensaje)); } 
    	catch(NumberFormatException e) { 
    		throw new IllegalArgumentException("Debes escribir un número entero."); } }
    
    private LocalDate fecha(String mensaje) { 
    	try { 
    		return LocalDate.parse(leer(mensaje)); } 
    	catch(Exception e) { 
    		throw new IllegalArgumentException("Fecha inválida. Usa AAAA-MM-DD."); } }
    
    private LocalTime hora(String mensaje) { 
    	try { 
    		return LocalTime.parse(leer(mensaje)); } 
    	catch(Exception e) { 
    		throw new IllegalArgumentException("Hora inválida. Usa HH:MM."); } }
    
    private void guardarSilencioso() { 
    	try { persistencia.guardar(club); } 
    	catch(Exception e) { error(e); } }
    
    private void error(Exception e) { 
    	System.out.println("No se pudo completar la operación: " + e.getMessage()); }

}