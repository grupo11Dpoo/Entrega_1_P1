package clubDeportivoSeneca.consola;

import java.time.LocalDate; 
import java.util.Scanner;
import clubDeportivoSeneca.modelo.deportes.Baloncesto;
import clubDeportivoSeneca.modelo.deportes.CategoriaEdad;
import clubDeportivoSeneca.modelo.deportes.DeporteDeConjunto;
import clubDeportivoSeneca.modelo.deportes.Futbol;
import clubDeportivoSeneca.modelo.deportes.Voleibol;
import clubDeportivoSeneca.modelo.usuario.Administrador;
import clubDeportivoSeneca.modelo.usuario.Jugador;
import clubDeportivoSeneca.modelo.usuario.Usuario;
import clubDeportivoSeneca.modelo.equipos.Equipo;
import clubDeportivoSeneca.persistencia.ArchivoPlano;
import clubDeportivoSeneca.sistema.ClubDeportivoSeneca;

public class ConsolaPrincipal {
	private final Scanner entrada = new Scanner(System.in);
    private final ArchivoPlano persistencia = new ArchivoPlano();
    private ClubDeportivoSeneca club;
    private Usuario usuarioActual;

    public static void main(String[] args) {
        new ConsolaPrincipal().iniciar();
    }

    public void iniciar() {
        try {
            club = persistencia.cargar();
            if (club == null) {
                club = new ClubDeportivoSeneca();
                Administrador admin = new Administrador("admin", "admin123");
                club.setAdministrador(admin);
                persistencia.guardar(club);
                System.out.println("Se creó el administrador inicial: login=admin, contraseña=admin123");
            }
            boolean continuar = true;
            while (continuar) {
                if (usuarioActual == null) continuar = menuPublico();
                else continuar = menuUsuario();
            }
            persistencia.guardar(club);
            System.out.println("Datos guardados en: " + persistencia.getRutaArchivo());
        } catch (Exception e) {
            System.err.println("No se pudo iniciar la aplicación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean menuPublico() throws Exception {
        System.out.println("\n=== CLUB DEPORTIVO SÉNECA ===\n1. Iniciar sesión\n2. Crear cuenta de socio\n0. Guardar y salir");
        String opcion = leer("Opción: ");
        switch (opcion) {
            case "1":
                String login = leer("Login: ");
                String password = leer("Contraseña: ");
                usuarioActual = club.iniciarSesion(login, password);
                if (usuarioActual == null) System.out.println("Credenciales incorrectas.");
                else System.out.println("Bienvenido/a, " + usuarioActual.getLogin());
                return true;
            case "2":
                String nuevoLogin = leer("Login nuevo: ");
                String nuevaClave = leer("Contraseña: ");
                String nombre = leer("Nombre completo: ");
                LocalDate nacimiento = LocalDate.parse(leer("Fecha de nacimiento (AAAA-MM-DD): "));
                club.crearJugador(nuevoLogin, nuevaClave, nombre, nacimiento);
                persistencia.guardar(club);
                System.out.println("Socio registrado correctamente.");
                return true;
            case "0": return false;
            default: System.out.println("Opción no válida."); return true;
        }
    }

    private boolean menuUsuario() throws Exception {
        System.out.println("\nSesión: " + usuarioActual.getLogin());
        System.out.println("1. Ver usuarios\n2. Ver equipos\n3. Crear jugador (administrador)\n4. Crear equipo (administrador)\n5. Guardar datos\n6. Cerrar sesión\n0. Guardar y salir");
        String opcion = leer("Opción: ");
        switch (opcion) {
            case "1":
                club.getUsuarios().values().forEach(u -> System.out.println(u.getClass().getSimpleName() + " - " + u.getLogin()));
                break;
            case "2":
                if (club.getEquipos().isEmpty()) System.out.println("Todavía no hay equipos registrados.");
                else for (Equipo e : club.getEquipos()) System.out.println(e.getNombre() + " | " + e.getDeporte() + " | " + e.getCategoriaEdad() + " | activos=" + e.isActivo());
                break;
            case "3":
                if (!(usuarioActual instanceof Administrador)) { System.out.println("Solo el administrador puede registrar jugadores."); break; }
                String login = leer("Login del jugador: ");
                String clave = leer("Contraseña: ");
                String nombre = leer("Nombre: ");
                LocalDate fecha = LocalDate.parse(leer("Fecha de nacimiento (AAAA-MM-DD): "));
                club.crearJugador(login, clave, nombre, fecha);
                System.out.println("Jugador creado.");
                break;
            case "4":
                if (!(usuarioActual instanceof Administrador)) { System.out.println("Solo el administrador puede crear equipos."); break; }
                crearEquipo();
                break;
            case "5": persistencia.guardar(club); System.out.println("Guardado correcto."); break;
            case "6": usuarioActual = null; break;
            case "0": return false;
            default: System.out.println("Opción no válida.");
        }
        return true;
    }

    private void crearEquipo() throws Exception {
        String nombre = leer("Nombre del equipo: ");
        System.out.println("Deporte: 1.Fútbol 2.Baloncesto 3.Voleibol");
        String deporteOpcion = leer("Opción: ");
        DeporteDeConjunto deporte;
        switch (deporteOpcion) {
            case "1": deporte = new Futbol("Fútbol", 7, true); break;
            case "2": deporte = new Baloncesto("Baloncesto", 5, false); break;
            case "3": deporte = new Voleibol("Voleibol", 6, false); break;
            default: System.out.println("Deporte no válido."); return;
        }
        System.out.println("Categoría: 1.Sub-13 2.Sub-15 3.Sub-17 4.Mayores");
        String categoriaOpcion = leer("Opción: ");
        String nombreCategoria; int edadMin; int edadMax;
        switch (categoriaOpcion) {
            case "1": nombreCategoria = "Sub-13"; edadMin = 0; edadMax = 13; break;
            case "2": nombreCategoria = "Sub-15"; edadMin = 14; edadMax = 15; break;
            case "3": nombreCategoria = "Sub-17"; edadMin = 16; edadMax = 17; break;
            case "4": nombreCategoria = "Mayores"; edadMin = 18; edadMax = 100; break;
            default: System.out.println("Categoría no válida."); return;
        }
        int cupo = Integer.parseInt(leer("Cupo máximo de jugadores: "));
        club.crearEquipo(nombre, deporte, new CategoriaEdad(nombreCategoria, edadMin, edadMax), cupo);
        System.out.println("Equipo creado correctamente.");
    }

    private String leer(String mensaje) {
        System.out.print(mensaje);
        return entrada.nextLine().trim();
    }

}
