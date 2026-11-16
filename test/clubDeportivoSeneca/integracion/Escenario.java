package clubDeportivoSeneca.integracion;

import java.time.LocalDate;
import java.time.LocalTime;

import clubDeportivoSeneca.modelo.deportes.CategoriaEdad;
import clubDeportivoSeneca.modelo.deportes.Futbol;
import clubDeportivoSeneca.modelo.deportes.Tenis;
import clubDeportivoSeneca.modelo.equipos.Equipo;
import clubDeportivoSeneca.modelo.instalaciones.CanchaTenis;
import clubDeportivoSeneca.modelo.instalaciones.HistorialReservas;
import clubDeportivoSeneca.modelo.instalaciones.HorarioBloque;
import clubDeportivoSeneca.modelo.instalaciones.InventarioInstalaciones;
import clubDeportivoSeneca.modelo.inventario.Articulo;
import clubDeportivoSeneca.modelo.inventario.InventarioArticulos;
import clubDeportivoSeneca.modelo.turnos.GestorTurnos;
import clubDeportivoSeneca.modelo.usuario.Administrador;
import clubDeportivoSeneca.modelo.usuario.Entrenador;
import clubDeportivoSeneca.modelo.usuario.Fisioterapeuta;
import clubDeportivoSeneca.modelo.usuario.Jugador;
import clubDeportivoSeneca.sistema.ClubDeportivoSeneca;


public class Escenario {

    public final Administrador admin = new Administrador("admin", "admin123");
    public final ClubDeportivoSeneca club;
    public final Futbol futbol;
    public final Tenis tenis;
    public final CategoriaEdad sub15;
    public final CategoriaEdad mayores;
    public final CanchaTenis cancha;
    public final Articulo balon;
    public final LocalDate manana = LocalDate.now().plusDays(1);

    public Escenario() throws Exception {
        club = new ClubDeportivoSeneca(admin, new GestorTurnos(LocalTime.of(6, 0), LocalTime.of(22, 0)),
                new InventarioArticulos(), new InventarioInstalaciones(new HistorialReservas()));
        futbol = new Futbol("Futbol", 7, true);
        tenis = new Tenis("Tenis", 2);
        sub15 = new CategoriaEdad("Sub-15", 13, 15);
        mayores = new CategoriaEdad("Mayores", 18, 60);
        cancha = new CanchaTenis("T1", tenis);
        club.registrarInstalacion(cancha);
        balon = new Articulo("Balon", "Futbol", 50000);
        club.registrarArticulo(balon, 10);
    }

  
    public Jugador socio(String login, int edad) {
        return club.crearJugador(login, "1234", "Socio " + login, LocalDate.now().minusYears(edad).minusDays(1));
    }


    public Equipo equipo(String nombre, CategoriaEdad categoria, int edad, int n) throws Exception {
        Equipo equipo = club.crearEquipo(nombre, futbol, categoria, 20);
        for (int i = 1; i <= n; i++) {
            club.inscribirJugadorEnEquipo(equipo, socio(nombre.toLowerCase().replace(" ", "") + i, edad), "Jugador", i);
        }
        return equipo;
    }

    public Entrenador entrenador(String login) {
        Entrenador e = new Entrenador(login, "1234", "Entrenador " + login, login.toUpperCase());
        club.registrarUsuario(e);
        return e;
    }

    public Fisioterapeuta fisioterapeuta(String login) {
        Fisioterapeuta f = new Fisioterapeuta(login, "1234", "Fisio " + login, login.toUpperCase());
        club.registrarUsuario(f);
        return f;
    }

    public static HorarioBloque bloque(int inicio, int fin) throws Exception {
        return new HorarioBloque(LocalTime.of(inicio, 0), LocalTime.of(fin, 0));
    }
}