package clubDeportivoSeneca.modelo.deportes;

public class Tenis extends DeporteIndividual implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	public Tenis(String nombre, int maxInstalacionesSimultaneas) throws Exception {
		super(nombre, maxInstalacionesSimultaneas);
	}

}
