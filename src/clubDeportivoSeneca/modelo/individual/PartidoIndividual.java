package clubDeportivoSeneca.modelo.individual;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import clubDeportivoSeneca.modelo.deportes.DeporteIndividual;
import clubDeportivoSeneca.modelo.deportes.TenisMesa;
import clubDeportivoSeneca.modelo.usuario.Jugador;

public class PartidoIndividual implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private LocalDate fecha;
    private DeporteIndividual disciplina;
    private Jugador jugador1;
    private Jugador jugador2;
    // arreglo
    private Jugador jugador3;
    private Jugador jugador4;
    private Jugador ganador; //correccion entrega 1
    private boolean finalizado;
    private boolean rankingsActualizados;
    private List<EstadisticaIndividual> estadisticas;

    public PartidoIndividual(LocalDate fecha, DeporteIndividual disciplina,
                             Jugador jugador1, Jugador jugador2) throws Exception {
        this(fecha, disciplina, jugador1, jugador2, null, null);
    }

    public PartidoIndividual(LocalDate fecha, DeporteIndividual disciplina,
                             Jugador jugador1, Jugador jugador2,
                             Jugador jugador3, Jugador jugador4) throws Exception {
        if (fecha == null || disciplina == null || jugador1 == null || jugador2 == null) {
            throw new Exception("El partido debe tener fecha, disciplina y participantes.");
        }
        if ((jugador3 == null) != (jugador4 == null)) {
            throw new Exception("En dobles se necesitan cuatro jugadores.");
        }
        List<Jugador> participantes = new ArrayList<>();
        participantes.add(jugador1);
        participantes.add(jugador2);
        if (jugador3 != null) {
            participantes.add(jugador3);
            participantes.add(jugador4);
        }
        if (participantes.stream().distinct().count() != participantes.size()) {
            throw new Exception("Un jugador no puede aparecer dos veces en el mismo partido.");
        }
        this.fecha = fecha;
        this.disciplina = disciplina;
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.jugador3 = jugador3;
        this.jugador4 = jugador4;
        this.finalizado = false;
        this.rankingsActualizados = false;
        this.estadisticas = new ArrayList<>();
        validarNiveles();
    }

    public LocalDate getFecha() { return fecha; }
    public DeporteIndividual getDisciplina() { return disciplina; }
    public Jugador getJugador1() { return jugador1; }
    public Jugador getJugador2() { return jugador2; }
    public Jugador getJugador3() { return jugador3; }
    public Jugador getJugador4() { return jugador4; }
    public boolean esDobles() { return jugador3 != null && jugador4 != null; }
    public Jugador getGanador() { return ganador; }
    public boolean isFinalizado() { return finalizado; }
    public boolean isRankingsActualizados() { return rankingsActualizados; }
    public List<EstadisticaIndividual> getEstadisticas() {
        return Collections.unmodifiableList(estadisticas);
    }
    public List<Jugador> getParticipantes() {
        List<Jugador> participantes = new ArrayList<>();
        participantes.add(jugador1);
        participantes.add(jugador2);
        if (esDobles()) { participantes.add(jugador3); participantes.add(jugador4); }
        return Collections.unmodifiableList(participantes);
    }

    public void setFecha(LocalDate fecha) throws Exception {
        if (finalizado) throw new Exception("No se puede cambiar la fecha de un partido finalizado.");
        if (fecha == null) throw new Exception("La fecha es obligatoria.");
        this.fecha = fecha;
    }

    public void validarNiveles() throws Exception {
        List<Jugador> jugadores = getParticipantes();

        if (jugadores.stream().anyMatch(Jugador::esEntrenador)) return;
        List<NivelJugador> niveles = new ArrayList<>();
        for (Jugador jugador : jugadores) {
            NivelJugador nivel = jugador.getNivelPorDeporteInd(disciplina);
            if (nivel == null) {
                throw new Exception("Todos los jugadores deben estar inscritos en " + disciplina.getNombre());
            }
            niveles.add(nivel);
        }
        for (int i = 0; i < niveles.size(); i++) {
            for (int j = i + 1; j < niveles.size(); j++) {
                if (!niveles.get(i).esAdyacenteA(niveles.get(j))) {
                    throw new Exception("Los niveles de los participantes no son compatibles.");
                }
            }
        }
    }

    public void registrarResultado(Jugador ganador, EstadisticaIndividual e1,
                                   EstadisticaIndividual e2) throws Exception {
        if (esDobles()) throw new Exception("En dobles se deben registrar cuatro estadísticas.");
        registrarResultadoInterno(ganador, List.of(e1, e2));
    }

    public void registrarResultado(Jugador ganador, EstadisticaIndividual e1,
                                   EstadisticaIndividual e2, EstadisticaIndividual e3,
                                   EstadisticaIndividual e4) throws Exception {
        if (!esDobles()) throw new Exception("Los partidos individuales tienen dos participantes.");
        registrarResultadoInterno(ganador, List.of(e1, e2, e3, e4));
    }

    private void registrarResultadoInterno(Jugador ganador, List<EstadisticaIndividual> datos) throws Exception {
        if (finalizado) throw new Exception("El partido ya tiene resultado.");
        if (!getParticipantes().contains(ganador)) {
            throw new Exception("El ganador debe participar en el partido.");
        }
        List<Jugador> participantes = getParticipantes();
        for (int i = 0; i < participantes.size(); i++) {
            EstadisticaIndividual e = datos.get(i);
            if (e == null || e.getJugador() != participantes.get(i)) {
                throw new Exception("Las estadísticas no corresponden a los jugadores del partido.");
            }
            boolean tenisMesa = disciplina instanceof TenisMesa;
            if (tenisMesa && !(e instanceof EstadisticaTMesa)) {
                throw new Exception("En tenis de mesa se registran sets y puntos.");
            }
            if (!tenisMesa && !(e instanceof EstadisticaTyP)) {
                throw new Exception("En tenis y pádel se registran sets, games, aces y errores.");
            }
        }
        this.ganador = ganador;
        this.estadisticas.addAll(datos);
        this.finalizado = true;
    }

    public void actualizarRankings(Ranking r1, Ranking r2) throws Exception {
        if (esDobles()) throw new Exception("En dobles se deben proporcionar cuatro rankings.");
        validarActualizacion(List.of(r1, r2));
        int puntaje1 = r1.getPuntaje();
        int puntaje2 = r2.getPuntaje();
        r1.actualizarElo(puntaje2, ganador == jugador1, fecha);
        r2.actualizarElo(puntaje1, ganador == jugador2, fecha);
        rankingsActualizados = true;
    }

    public void actualizarRankings(Ranking r1, Ranking r2, Ranking r3, Ranking r4) throws Exception {
        if (!esDobles()) throw new Exception("El partido no es de dobles.");
        validarActualizacion(List.of(r1, r2, r3, r4));
        //arreglo
        int promedioA = (r1.getPuntaje() + r3.getPuntaje()) / 2;
        int promedioB = (r2.getPuntaje() + r4.getPuntaje()) / 2;
        boolean ganaA = ganador == jugador1 || ganador == jugador3;
        r1.actualizarElo(promedioB, ganaA, fecha);
        r3.actualizarElo(promedioB, ganaA, fecha);
        r2.actualizarElo(promedioA, !ganaA, fecha);
        r4.actualizarElo(promedioA, !ganaA, fecha);
        rankingsActualizados = true;
    }

    private void validarActualizacion(List<Ranking> rankings) throws Exception {
        if (!finalizado) throw new Exception("El partido todavía no tiene resultado.");
        if (rankingsActualizados) throw new Exception("Los rankings ya se actualizaron con este partido.");
        List<Jugador> participantes = getParticipantes();
        for (int i = 0; i < rankings.size(); i++) {
            Ranking r = rankings.get(i);
            if (r == null || r.getJugador() != participantes.get(i)
                    || !r.getDisciplina().equals(disciplina)) {
                throw new Exception("Los rankings no corresponden a los jugadores y disciplina.");
            }
        }
    }
}