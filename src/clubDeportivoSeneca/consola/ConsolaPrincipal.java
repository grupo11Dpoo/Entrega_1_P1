package clubDeportivoSeneca.consola;


import clubDeportivoSeneca.modelo.usuario.Administrador;
import clubDeportivoSeneca.modelo.usuario.Empleado;
import clubDeportivoSeneca.modelo.usuario.Jugador;
import clubDeportivoSeneca.modelo.usuario.Usuario;
import clubDeportivoSeneca.persistencia.ArchivoPlano;
import clubDeportivoSeneca.sistema.ClubDeportivoSeneca;

public class ConsolaPrincipal {
    private final LecturaConsola lectura;
    private final ArchivoPlano persistencia;
    private ClubDeportivoSeneca club;
    private Usuario usuarioActual;
    private String rolRequerido;

    public ConsolaPrincipal() { this(null); }
    public ConsolaPrincipal(String rutaDatos) {
        lectura = new LecturaConsola();
        String ruta = rutaDatos != null ? rutaDatos : System.getProperty("cds.archivoDatos");
        persistencia = ruta == null || ruta.isBlank() ? new ArchivoPlano() : new ArchivoPlano(ruta);
    }
    public static void main(String[] args) { new ConsolaPrincipal().iniciar(); }

    public void iniciarParaRol(String rol) {
        this.rolRequerido = rol;
        iniciar();
    }
    public void iniciar() {
        try {
            club = persistencia.cargar();
            if (club == null) {
                club = new ClubDeportivoSeneca();
                club.setAdministrador(new Administrador("admin", "admin123"));
                persistencia.guardar(club);
                System.out.println("Primer inicio: administrador admin / admin123 (cambia esta contraseña).");
            }
            if (club.getAdministrador() != null) club.getAdministrador().setClub(club);
            boolean seguir = true;
            try {
                while (seguir) seguir = usuarioActual == null ? menuPublico() : menuPrincipal();
            } finally {
                persistencia.guardar(club);
                System.out.println("Datos guardados en: " + persistencia.getRutaArchivo());
            }
        } catch (Exception e) { System.out.println("Error de inicio o guardado: " + e.getMessage()); }
    }
    private boolean esRolPermitido(Usuario usuario, String rol) {
        return switch (rol) {
            case "Administrador" -> usuario instanceof Administrador;
            case "Jugador" -> usuario instanceof Jugador;
            case "Empleado" -> usuario instanceof Empleado;
            default -> false;
        };
    }
    private boolean menuPublico() {
        System.out.println("\n========== CLUB DEPORTIVO SÉNECA ==========");
        System.out.println("1. Iniciar sesión\n2. Registrar jugador\n0. Salir");
        try {
            switch (lectura.leer("Opción: ")) {
                case "1" -> {
                    usuarioActual = club.iniciarSesion(lectura.obligatorio("Usuario: "), lectura.obligatorio("Contraseña: "));
                    if (usuarioActual != null && rolRequerido != null && !esRolPermitido(usuarioActual, rolRequerido)) {
                        usuarioActual = null;
                        System.out.println("La cuenta no pertenece al rol requerido: " + rolRequerido);
                    } else System.out.println(usuarioActual == null ? "Credenciales incorrectas." : "Bienvenido/a " + usuarioActual.getLogin());
                }
                case "2" -> {
                    club.crearJugador(lectura.obligatorio("Nuevo usuario: "), lectura.obligatorio("Contraseña: "), lectura.obligatorio("Nombre: "), lectura.fecha("Nacimiento (AAAA-MM-DD): "));
                    persistencia.guardar(club);
                    System.out.println("Registro exitoso. Inicia sesión.");
                }
                case "0" -> { return false; }
                default -> System.out.println("Opción no válida.");
            }
        } catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
        return true;
    }
    private boolean menuPrincipal() {
        System.out.println("\n========== MENÚ " + usuarioActual.getClass().getSimpleName().toUpperCase() + " ==========");
        System.out.println("1. Usuarios\n2. Equipos\n3. Partidos\n4. Competencias\n5. Deportes individuales\n6. Instalaciones\n7. Entrenamientos\n8. Turnos\n9. Inventario\n10. Ventas\n11. Consultas\n12. Guardar\n13. Cerrar sesión\n14. Cambiar contraseña\n0. Guardar y salir");
        try {
            switch (lectura.leer("Módulo: ")) {
                case "1" -> new MenuUsuarios(club, usuarioActual, persistencia, lectura).ejecutar();
                case "2" -> new MenuEquipos(club, usuarioActual, persistencia, lectura).ejecutar();
                case "3" -> new MenuPartidos(club, usuarioActual, persistencia, lectura).ejecutar();
                case "4" -> new MenuCompetencias(club, usuarioActual, persistencia, lectura).ejecutar();
                case "5" -> new MenuIndividuales(club, usuarioActual, persistencia, lectura).ejecutar();
                case "6" -> new MenuInstalaciones(club, usuarioActual, persistencia, lectura).ejecutar();
                case "7" -> new MenuEntrenamientos(club, usuarioActual, persistencia, lectura).ejecutar();
                case "8" -> new MenuTurnos(club, usuarioActual, persistencia, lectura).ejecutar();
                case "9" -> new MenuInventario(club, usuarioActual, persistencia, lectura).ejecutar();
                case "10" -> new MenuVentas(club, usuarioActual, persistencia, lectura).ejecutar();
                case "11" -> new MenuConsultas(club, usuarioActual, persistencia, lectura).ejecutar();
                case "12" -> { persistencia.guardar(club); System.out.println("Datos guardados."); }
                case "13" -> { persistencia.guardar(club); usuarioActual = null; }
                case "14" -> {
                    usuarioActual.cambiarPassword(lectura.obligatorio("Contraseña actual: "), lectura.obligatorio("Nueva contraseña: "));
                    persistencia.guardar(club);
                    System.out.println("Contraseña modificada.");
                }
                case "0" -> { return false; }
                default -> System.out.println("Opción no válida.");
            }
        } catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
        return true;
    }
}
