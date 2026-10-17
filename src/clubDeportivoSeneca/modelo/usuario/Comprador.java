package clubDeportivoSeneca.modelo.usuario;

public interface Comprador {
	
	String getLogin();
	int getPuntosFidelidad();
	void acumularPuntos(int valorCompra);
	void redimirPuntos(int valorCompra) throws Exception;

}
