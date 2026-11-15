package clubDeportivoSeneca.consola;

import clubDeportivoSeneca.modelo.usuario.Administrador;
import clubDeportivoSeneca.modelo.usuario.Usuario;
import clubDeportivoSeneca.persistencia.ArchivoPlano;
import clubDeportivoSeneca.sistema.ClubDeportivoSeneca;

public class MenuConsultas extends ConsolaBase {
	public MenuConsultas(ClubDeportivoSeneca club, Usuario usuario, ArchivoPlano persistencia, LecturaConsola lectura) { super(club, usuario, persistencia, lectura); }
    public void ejecutar() {
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


}
