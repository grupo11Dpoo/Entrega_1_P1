package clubDeportivoSeneca.modelo.torneos;
 
import java.time.LocalDate;
import java.util.List;

import clubDeportivoSeneca.modelo.deportes.CategoriaEdad;
import clubDeportivoSeneca.modelo.deportes.Deporte;
import clubDeportivoSeneca.modelo.individual.NivelJugador;
import clubDeportivoSeneca.modelo.usuario.Jugador;
 
@SuppressWarnings("serial")
public class TorneoRecreativo extends Torneo {
 
    private double porcentajeBono;
 
    public TorneoRecreativo(String nombre, Deporte deporte, CategoriaEdad categoriaEdad, NivelJugador nivel,
                            boolean dobles, int cupos, LocalDate fechaInicio, double porcentajeBono) throws Exception {
        
    	super(nombre, deporte, categoriaEdad, nivel, dobles, cupos, fechaInicio);
       
    	if (porcentajeBono <= 0 || porcentajeBono > 1) {
    		throw new Exception("El bono debe estar entre 0 y 1.");
    	}
        this.porcentajeBono = porcentajeBono;
    }
 
    public double getPorcentajeBono() {
    	return porcentajeBono;
    }
 

    public void entregarBonos(String torneoOrigen, double porcentaje, LocalDate vigenteHasta) throws Exception {
        
    	if (getPosiciones().isEmpty()) {
            throw new Exception("El torneo no tiene posiciones; no hay ganador.");
        }

        Inscripcion ganador = getPosiciones().get(0);

        List<Jugador> beneficiarios;
        if (ganador.esEquipo()) {
            beneficiarios = ganador.getEquipo().getJugadores();
        } else {
            beneficiarios = ganador.getJugadores();
        }

        for (Jugador jugador : beneficiarios) {
            jugador.agregarBono(new BonoDescuento(torneoOrigen, porcentaje, vigenteHasta));
        }
    }
 

    @Override
    protected void entregarPremios() throws Exception {
        entregarBonos(getNombre(), porcentajeBono, LocalDate.now().plusMonths(3));
    }
}
