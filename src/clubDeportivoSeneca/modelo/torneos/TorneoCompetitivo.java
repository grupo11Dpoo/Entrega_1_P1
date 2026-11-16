package clubDeportivoSeneca.modelo.torneos;
 
import java.time.LocalDate;
 
import clubDeportivoSeneca.modelo.deportes.CategoriaEdad;
import clubDeportivoSeneca.modelo.deportes.Deporte;
import clubDeportivoSeneca.modelo.individual.NivelJugador;
import clubDeportivoSeneca.modelo.usuario.Jugador;
 
public class TorneoCompetitivo extends Torneo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
 
    private double tarifaInscripcion;
 
    public TorneoCompetitivo(String nombre, Deporte deporte, CategoriaEdad categoriaEdad, NivelJugador nivel,
                             boolean dobles, int cupos, LocalDate fechaInicio, double tarifaInscripcion) throws Exception {
        
    	super(nombre, deporte, categoriaEdad, nivel, dobles, cupos, fechaInicio);
        
    	if (tarifaInscripcion <= 0) {
    		throw new Exception("La tarifa debe ser positiva.");
    	}
    	
        this.tarifaInscripcion = tarifaInscripcion;
    }
 
    public double getTarifaInscripcion() {
    	return tarifaInscripcion;
    }
 

    public double totalRecaudado() {
        double total = 0;
        for (Inscripcion i : inscripcionesInternas()) {
            total += tarifaInscripcion * pagantes(i);
        }
        return total;
    }
 
    public double premioPara(Inscripcion inscripcion) {
        if (getEstado() != EstadoTorneo.FINALIZADO || getPosiciones().isEmpty()
                || getPosiciones().get(0) != inscripcion) {
            return 0;
        }
        double premio = totalRecaudado();
        if (!inscripcion.esEquipo()) {
            premio = premio * pagantes(inscripcion) / inscripcion.getJugadores().size();
        }
        return premio;
    }
 
    private double pagantes(Inscripcion i) {
        if (i.esEquipo()) {
            return 1;
        }
        int n = 0;
        for (Jugador j : i.getJugadores()) {
            if (!esEmpleado(j)) {
                n++;
            }
        }
        return n;
    }
}
