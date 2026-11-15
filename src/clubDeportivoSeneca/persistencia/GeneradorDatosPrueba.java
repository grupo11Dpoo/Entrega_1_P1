package clubDeportivoSeneca.persistencia;

import java.nio.file.Path;
import java.time.LocalDate;
import clubDeportivoSeneca.sistema.ClubDeportivoSeneca;
import clubDeportivoSeneca.modelo.usuario.Administrador;
import clubDeportivoSeneca.modelo.usuario.Entrenador;
import clubDeportivoSeneca.modelo.usuario.Fisioterapeuta;
import clubDeportivoSeneca.modelo.usuario.Jugador;
import clubDeportivoSeneca.modelo.deportes.Futbol;
import clubDeportivoSeneca.modelo.deportes.CategoriaEdad;
import clubDeportivoSeneca.modelo.equipos.Equipo;

//para proyecto 2 regenera los archivos de ejemplo sin modificar los datos del usuario
public final class GeneradorDatosPrueba {
    private GeneradorDatosPrueba() { }
    public static void main(String[] args) throws Exception {
        Path destino = Path.of(args.length > 0 ? args[0] : "datos_prueba/cds-inicial.dat");
        ClubDeportivoSeneca club = new ClubDeportivoSeneca();
        club.setAdministrador(new Administrador("admin", "admin123"));
        Jugador ana = club.crearJugador("ana", "1234", "Ana Rodríguez", LocalDate.of(2001, 4, 15));
        Jugador juan = club.crearJugador("juan", "1234", "Juan Pérez", LocalDate.of(2000, 8, 21));
        club.registrarUsuario(new Entrenador("entrenador", "1234", "Carlos López", "ENT-01"));
        club.registrarUsuario(new Fisioterapeuta("fisio", "1234", "Laura Gómez", "FIS-01"));
        Equipo equipo = club.crearEquipo("Séneca Fútbol", new Futbol("Fútbol", 7, true), new CategoriaEdad("Mayores", 18, 100), 20);
        club.inscribirJugadorEnEquipo(equipo, ana, "Delantera", 9);
        club.inscribirJugadorEnEquipo(equipo, juan, "Defensa", 4);
        new ArchivoPlano(destino).guardar(club);
        System.out.println("Ejemplo creado en " + destino.toAbsolutePath());
    }
}
