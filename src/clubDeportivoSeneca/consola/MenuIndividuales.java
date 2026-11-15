package clubDeportivoSeneca.consola;

import java.time.LocalDate;
import clubDeportivoSeneca.modelo.deportes.DeporteIndividual;
import clubDeportivoSeneca.modelo.individual.NivelJugador;
import clubDeportivoSeneca.modelo.individual.PartidoIndividual;
import clubDeportivoSeneca.modelo.individual.Ranking;
import clubDeportivoSeneca.modelo.usuario.Jugador;
import clubDeportivoSeneca.modelo.usuario.Usuario;
import clubDeportivoSeneca.persistencia.ArchivoPlano;
import clubDeportivoSeneca.sistema.ClubDeportivoSeneca;

public class MenuIndividuales extends ConsolaBase {
    public MenuIndividuales(ClubDeportivoSeneca club, Usuario usuario, ArchivoPlano persistencia, LecturaConsola lectura) {
        super(club, usuario, persistencia, lectura);
    }

    public void ejecutar() {
        System.out.println("\n--- DEPORTES INDIVIDUALES ---\n1. Ver partidos individuales\n2. Ver rankings\n3. Crear ranking ELO\n4. Asignar nivel a jugador\n5. Crear partido individual\n6. Registrar resultado y actualizar ELO\n0. Volver");
        try {
            switch (opcionAutorizada("MenuIndividuales", leer("Opción: "))) {
                case "1": 
                	if (club.getPartidosIndividuales().isEmpty()) System.out.println("No hay partidos individuales."); 
                	else club.getPartidosIndividuales().forEach(System.out::println); break;
                case "2": 
                	if (club.getRankings().isEmpty()) System.out.println("No hay rankings."); 
                	else club.getRankings().forEach(r -> System.out.println(r.getJugador().getLogin() + " | " + r.getDisciplina().getNombre() + " | ELO=" + r.getPuntaje())); break;
                case "3": exigirAdmin(); Jugador jugador = elegirJugador(); DeporteIndividual disciplina = elegirDisciplina(); Ranking ranking = new Ranking(jugador, disciplina); club.registrarRanking(ranking); guardarSilencioso(); System.out.println("Ranking creado con ELO inicial " + ranking.getPuntaje()); break;
                case "4": Jugador j = elegirJugador(); DeporteIndividual d = elegirDisciplina(); NivelJugador nivel = elegirNivel(); j.asignarNivel(d, nivel); guardarSilencioso(); System.out.println("Nivel asignado."); break;
                case "5": {
                    LocalDate fechaPartido = fecha("Fecha (AAAA-MM-DD): ");
                    DeporteIndividual deporte = elegirDisciplina();
                    boolean dobles = leer("Modalidad: 1. Individual  2. Dobles: ").equals("2");
                    System.out.println("Jugador 1 (equipo A):"); Jugador j1 = elegirJugador();
                    System.out.println("Jugador 2 (equipo B):"); Jugador j2 = elegirJugador();
                    PartidoIndividual pi;
                    if (dobles) {
                        System.out.println("Jugador 3 (compañero de jugador 1):"); Jugador j3 = elegirJugador();
                        System.out.println("Jugador 4 (compañero de jugador 2):"); Jugador j4 = elegirJugador();
                        pi = new PartidoIndividual(fechaPartido, deporte, j1, j2, j3, j4);
                    } else {
                        pi = new PartidoIndividual(fechaPartido, deporte, j1, j2);
                    }
                    club.registrarPartidoIndividual(pi);
                    guardarSilencioso();
                    System.out.println("Partido creado.");
                    break;
                }
                case "6": registrarResultadoIndividual(); guardarSilencioso(); break;
                case "0": return;
                default: System.out.println("Opción no válida.");
            }
        } catch (Exception e) { error(e); }
    }

}
