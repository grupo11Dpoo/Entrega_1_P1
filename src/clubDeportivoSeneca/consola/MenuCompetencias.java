package clubDeportivoSeneca.consola;

import java.util.List;

import clubDeportivoSeneca.modelo.competencias.Circuito;
import clubDeportivoSeneca.modelo.equipos.Equipo;
import clubDeportivoSeneca.modelo.usuario.Jugador;
import clubDeportivoSeneca.modelo.usuario.Usuario;
import clubDeportivoSeneca.persistencia.ArchivoPlano;
import clubDeportivoSeneca.sistema.ClubDeportivoSeneca;

public class MenuCompetencias extends ConsolaBase {
    public MenuCompetencias(ClubDeportivoSeneca club, Usuario usuario, ArchivoPlano persistencia, LecturaConsola lectura) {
        super(club, usuario, persistencia, lectura);
    }

    public void ejecutar() {
        System.out.println("\n--- COMPETENCIAS ---\n1. Listar competencias\n2. Crear ASCUN\n3. Crear Torneo de los Cerros\n4. Inscribir equipo y nómina\n5. Crear partido de competencia\n6. Registrar marcador/finalizar partido\n7. Ver clasificación de competencia\n0. Volver");
        try {
            switch (opcionAutorizada("MenuCompetencias", leer("Opción: "))) {
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

}
