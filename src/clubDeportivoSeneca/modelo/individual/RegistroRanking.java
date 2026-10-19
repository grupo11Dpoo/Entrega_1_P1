package clubDeportivoSeneca.modelo.individual;

import java.time.LocalDate;

public class RegistroRanking implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

    private LocalDate fecha;
    private int puntaje;

    public RegistroRanking(LocalDate fecha, int puntaje) throws Exception {
        if (fecha == null) {
            throw new Exception("El registro debe tener fecha");
        }
        this.fecha = fecha;
        this.puntaje = puntaje;
    }

    public LocalDate getFecha() {
        return this.fecha;
    }

    public int getPuntaje() {
        return this.puntaje;
    }
}
