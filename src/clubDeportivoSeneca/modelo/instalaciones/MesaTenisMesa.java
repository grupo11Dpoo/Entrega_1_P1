package clubDeportivoSeneca.modelo.instalaciones;

import java.util.List;

import clubDeportivoSeneca.modelo.deportes.DeporteIndividual;

public class MesaTenisMesa extends Instalacion implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

    public MesaTenisMesa(String id, DeporteIndividual disciplina, boolean techada) throws Exception {
        super(id, disciplina, techada, List.of(2, 4));
    }

    @Override
    public boolean restringeBebidaCaliente() {
        return isTechada();
    }
}