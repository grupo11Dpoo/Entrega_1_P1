package clubDeportivoSeneca.modelo.instalaciones;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import clubDeportivoSeneca.modelo.deportes.DeporteIndividual;

public abstract class Instalacion {

    private String id;
    private DeporteIndividual disciplina;
    private boolean techada;
    private List<Integer> capacidadesPermitidas;

    protected Instalacion(String id, DeporteIndividual disciplina, boolean techada, List<Integer> capacidadesPermitidas) throws Exception {
        if (id == null || id.isBlank()) {
            throw new Exception("La instalación debe tener un id");
        }
        if (disciplina == null) {
            throw new Exception("La instalación debe tener una disciplina");
        }
        if (capacidadesPermitidas == null || capacidadesPermitidas.isEmpty()) {
            throw new Exception("La instalación debe tener al menos una capacidad permitida");
        }
        this.id = id;
        this.disciplina = disciplina;
        this.techada = techada;
        this.capacidadesPermitidas = new ArrayList<>(capacidadesPermitidas);
    }

    public String getId() {
        return this.id;
    }

    public DeporteIndividual getDisciplina() {
        return this.disciplina;
    }

    public boolean isTechada() {
        return this.techada;
    }

    public List<Integer> getCapacidadesPermitidas() {
        return Collections.unmodifiableList(this.capacidadesPermitidas);
    }

    public boolean admiteJugadores(int n) {
        return capacidadesPermitidas.contains(n);
    }

    public abstract boolean restringeBebidaCaliente();
}