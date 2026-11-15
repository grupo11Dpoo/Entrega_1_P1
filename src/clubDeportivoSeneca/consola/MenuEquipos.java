package clubDeportivoSeneca.consola;

import clubDeportivoSeneca.modelo.equipos.Equipo;
import clubDeportivoSeneca.modelo.usuario.Jugador;
import clubDeportivoSeneca.modelo.usuario.Usuario;
import clubDeportivoSeneca.persistencia.ArchivoPlano;
import clubDeportivoSeneca.sistema.ClubDeportivoSeneca;

public class MenuEquipos extends ConsolaBase {
	public MenuEquipos(ClubDeportivoSeneca club, Usuario usuario, ArchivoPlano persistencia, LecturaConsola lectura) {
        super(club, usuario, persistencia, lectura);
    }

    public void ejecutar() {
        System.out.println("\n--- EQUIPOS Y MEMBRESÍAS ---\n1. Listar equipos\n2. Crear equipo\n3. Inscribir jugador en equipo\n4. Dar de baja equipo\n0. Volver");
        try {
            switch (opcionAutorizada("MenuEquipos", leer("Opción: "))) {
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

}
