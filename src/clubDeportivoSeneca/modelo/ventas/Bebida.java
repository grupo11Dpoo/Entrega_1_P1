package clubDeportivoSeneca.modelo.ventas;

public class Bebida extends ProductoCafeteria {
	
	private final Temperatura temperatura;
		
	public Bebida(String nombre, int precio, Temperatura temperatura) {
        super(nombre, precio);
        if (temperatura == null) {
            throw new IllegalArgumentException("La temperatura de la bebida es obligatoria.");
        }
        this.temperatura = temperatura;
    }
 
    public Temperatura getTemperatura() {
        return temperatura;
    }
 
    public boolean esCaliente() {
        return temperatura == Temperatura.CALIENTE;
    }
}
