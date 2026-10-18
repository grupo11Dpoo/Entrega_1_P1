package clubDeportivoSeneca.modelo.usuario;

public interface Comprador extends java.io.Serializable {
	
	String getLogin();
	int getPuntosFidelidad();
	void acumularPuntos(int valorCompra);
	void redimirPuntos(int valorCompra) throws Exception;

}
