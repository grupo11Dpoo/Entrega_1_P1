package clubDeportivoSeneca.modelo.deportes;

public abstract class DeporteIndividual extends Deporte {
	
	protected int maxInstalacionesSimultaneas;

	protected DeporteIndividual(String nombre, int maxInstalacionesSimultaneas) throws Exception {
		super(nombre);
		this.maxInstalacionesSimultaneas = maxInstalacionesSimultaneas;
	}

	public int getMaxInstalacionesSimultaneas() {
		return maxInstalacionesSimultaneas;
	}

	public void setMaxInstalacionesSimultaneas(int maxInstalacionesSimultaneas) {
		this.maxInstalacionesSimultaneas = maxInstalacionesSimultaneas;
	}

}
