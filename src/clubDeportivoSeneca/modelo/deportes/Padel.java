package clubDeportivoSeneca.modelo.deportes;

public class Padel extends DeporteIndividual implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	public Padel(String nombre, int maxInstalacionesSimultaneas) throws Exception {
		super(nombre, maxInstalacionesSimultaneas);
	}

}
