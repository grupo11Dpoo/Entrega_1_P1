package clubDeportivoSeneca.consola;


import clubDeportivoSeneca.modelo.usuario.Entrenador;
import clubDeportivoSeneca.modelo.usuario.Fisioterapeuta;
import clubDeportivoSeneca.modelo.usuario.Jugador;
import clubDeportivoSeneca.modelo.usuario.Usuario;
import clubDeportivoSeneca.persistencia.ArchivoPlano;
import clubDeportivoSeneca.sistema.ClubDeportivoSeneca;

public class MenuUsuarios extends ConsolaBase {
    public MenuUsuarios(ClubDeportivoSeneca club, Usuario usuario, ArchivoPlano persistencia, LecturaConsola lectura) {
        super(club, usuario, persistencia, lectura);
    }

    public void ejecutar() {
        System.out.println("\n--- USUARIOS ---\n1. Listar usuarios\n2. Crear jugador\n3. Crear entrenador\n4. Crear fisioterapeuta\n5. Dar de baja jugador\n0. Volver");
        try {
            switch (opcionAutorizada("MenuUsuarios", leer("Opción: "))) {
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

}

