package clubDeportivoSeneca.modelo.instalaciones;

import java.util.List;

import clubDeportivoSeneca.modelo.deportes.DeporteIndividual;

public class CanchaTenis extends Instalacion implements java.io.Serializable{
	private static final long serialVersionUID = 1L;

    public CanchaTenis(String id, DeporteIndividual disciplina) throws Exception {
        super(id, disciplina, false, List.of(2, 4));
    }

    @Override
    public boolean restringeBebidaCaliente() {
        return false;
    }
}