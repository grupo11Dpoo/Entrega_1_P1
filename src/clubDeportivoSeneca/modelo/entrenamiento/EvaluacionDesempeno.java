package clubDeportivoSeneca.modelo.entrenamiento;

public class EvaluacionDesempeno implements java.io.Serializable {

    private int nota;
    private String comentario;

    public EvaluacionDesempeno(int nota, String comentario) throws Exception {
        if (nota < 1 || nota > 5) {
            throw new Exception("La nota debe estar entre 1 y 5");
        }
        if (comentario == null || comentario.isBlank()) {
            throw new Exception("La evaluación debe tener un comentario");
        }
        this.nota = nota;
        this.comentario = comentario;
    }

    public int getNota() {
        return this.nota;
    }

    public String getComentario() {
        return this.comentario;
    }
}