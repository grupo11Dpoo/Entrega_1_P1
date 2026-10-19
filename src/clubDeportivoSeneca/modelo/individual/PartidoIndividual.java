package clubDeportivoSeneca.modelo.individual;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import clubDeportivoSeneca.modelo.deportes.DeporteIndividual;
import clubDeportivoSeneca.modelo.deportes.TenisMesa;
import clubDeportivoSeneca.modelo.usuario.Jugador;

public class PartidoIndividual  implements java.io.Serializable{
	
	private static final long serialVersionUID = 1L;

    private LocalDate fecha;
    private DeporteIndividual disciplina;
    private Jugador jugador1;
    private Jugador jugador2;
    private Jugador ganador;
    private boolean finalizado;
    private List<EstadisticaIndividual> estadisticas;

    public PartidoIndividual(LocalDate fecha, DeporteIndividual disciplina, Jugador jugador1, Jugador jugador2) throws Exception {
        if (fecha == null || disciplina == null || jugador1 == null || jugador2 == null) {
            throw new Exception("El partido debe tener fecha, disciplina y dos jugadores");
        }
        if (jugador1 == jugador2) {
            throw new Exception("Un jugador no puede jugar contra sí mismo");
        }
        this.fecha = fecha;
        this.disciplina = disciplina;
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.finalizado = false;
        this.estadisticas = new ArrayList<>();
        validarNiveles();
    }

    public LocalDate getFecha() {
        return this.fecha;
    }

    public DeporteIndividual getDisciplina() {
        return this.disciplina;
    }

    public Jugador getJugador1() {
        return this.jugador1;
    }

    public Jugador getJugador2() {
        return this.jugador2;
    }

    public Jugador getGanador() {
        return this.ganador;
    }

    public boolean isFinalizado() {
        return this.finalizado;
    }

    public List<EstadisticaIndividual> getEstadisticas() {
        return Collections.unmodifiableList(this.estadisticas);
    }

    public void setFecha(LocalDate fecha) throws Exception {
        if (finalizado) {
            throw new Exception("No se puede cambiar la fecha de un partido finalizado");
        }
        this.fecha = fecha;
    }

    public void validarNiveles() throws Exception {
        if (jugador1.esEntrenador() || jugador2.esEntrenador()) {
            return;
        }
        NivelJugador nivel1 = jugador1.getNivelPorDeporteInd(disciplina);
        NivelJugador nivel2 = jugador2.getNivelPorDeporteInd(disciplina);
        if (nivel1 == null || nivel2 == null) {
            throw new Exception("Ambos jugadores deben estar inscritos en " + disciplina.getNombre());
        }
        if (!nivel1.esAdyacenteA(nivel2)) {
            throw new Exception("Los niveles " + nivel1 + " y " + nivel2 + " no pueden jugar entre sí");
        }
    }

    public void registrarResultado(Jugador ganador, EstadisticaIndividual e1, EstadisticaIndividual e2) throws Exception {
        if (finalizado) {
            throw new Exception("El partido ya tiene resultado");
        }
        if (ganador != jugador1 && ganador != jugador2) {
            throw new Exception("El ganador debe ser uno de los dos jugadores");
        }
        if (e1.getJugador() != jugador1 || e2.getJugador() != jugador2) {
            throw new Exception("Las estadísticas no corresponden a los jugadores del partido");
        }
        boolean esTenisMesa = disciplina instanceof TenisMesa;
        if (esTenisMesa && !(e1 instanceof EstadisticaTMesa && e2 instanceof EstadisticaTMesa)) {
            throw new Exception("En tenis de mesa se registran sets y puntos totales");
        }
        if (!esTenisMesa && !(e1 instanceof EstadisticaTyP && e2 instanceof EstadisticaTyP)) {
            throw new Exception("En tenis y pádel se registran sets, games, aces y errores no forzados");
        }
        this.ganador = ganador;
        estadisticas.add(e1);
        estadisticas.add(e2);
        this.finalizado = true;
    }

    public void actualizarRankings(Ranking r1, Ranking r2) throws Exception {
        if (!finalizado) {
            throw new Exception("El partido todavía no tiene resultado");
        }
        if (r1.getJugador() != jugador1 || r2.getJugador() != jugador2) {
            throw new Exception("Los rankings no corresponden a los jugadores del partido");
        }
        int puntaje1 = r1.getPuntaje();
        int puntaje2 = r2.getPuntaje();
        r1.actualizarElo(puntaje2, ganador == jugador1, fecha);
        r2.actualizarElo(puntaje1, ganador == jugador2, fecha);
    }
}