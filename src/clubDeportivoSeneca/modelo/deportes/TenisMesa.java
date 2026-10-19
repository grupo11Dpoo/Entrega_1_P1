package clubDeportivoSeneca.modelo.deportes;

public class TenisMesa extends DeporteIndividual implements java.io.Serializable{
	private static final long serialVersionUID = 1L;

	public TenisMesa(String nombre, int maxInstalacionesSimultaneas) throws Exception {
		super(nombre, maxInstalacionesSimultaneas);
	}
}
