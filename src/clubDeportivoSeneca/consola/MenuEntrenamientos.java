package clubDeportivoSeneca.consola;

import java.util.List;
import clubDeportivoSeneca.modelo.entrenamiento.Entrenamiento;
import clubDeportivoSeneca.modelo.entrenamiento.EstadoAsistencia;
import clubDeportivoSeneca.modelo.instalaciones.Instalacion;
import clubDeportivoSeneca.modelo.usuario.Entrenador;
import clubDeportivoSeneca.modelo.usuario.Jugador;
import clubDeportivoSeneca.modelo.usuario.Usuario;
import clubDeportivoSeneca.persistencia.ArchivoPlano;
import clubDeportivoSeneca.sistema.ClubDeportivoSeneca;

public class MenuEntrenamientos extends ConsolaBase {
	public MenuEntrenamientos(ClubDeportivoSeneca club, Usuario usuario, ArchivoPlano persistencia, LecturaConsola lectura) {
        super(club, usuario, persistencia, lectura);
    }

    public void ejecutar() {
        System.out.println("\n--- ENTRENAMIENTOS ---\n1. Listar entrenamientos\n2. Planear entrenamiento\n3. Convocar equipo a entrenamiento\n4. Registrar asistencia\n5. Registrar evaluación\n0. Volver");
        try {
            switch (opcionAutorizada("MenuEntrenamientos", leer("Opción: "))) {
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

}
