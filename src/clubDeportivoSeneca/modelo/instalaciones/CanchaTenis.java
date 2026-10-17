package clubDeportivoSeneca.modelo.instalaciones;

import java.util.List;

import clubDeportivoSeneca.modelo.deportes.DeporteIndividual;

public class CanchaTenis extends Instalacion {

    public CanchaTenis(String id, DeporteIndividual disciplina) throws Exception {
        super(id, disciplina, false, List.of(2, 4));
    }

    @Override
    public boolean restringeBebidaCaliente() {
        return false;
    }
}