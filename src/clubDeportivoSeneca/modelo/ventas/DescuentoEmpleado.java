package clubDeportivoSeneca.modelo.ventas;

import clubDeportivoSeneca.modelo.usuario.Comprador;
import clubDeportivoSeneca.modelo.usuario.Empleado;
import clubDeportivoSeneca.modelo.usuario.Jugador;

public class DescuentoEmpleado implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final int PERSONAL = 15;
    public static final int COMPARTIR = 8;
 
    private final Empleado titular;
 
    public DescuentoEmpleado(Empleado titular) {
        if (titular == null) {
            throw new IllegalArgumentException("El titular del descuento no puede ser nulo.");
        }
        this.titular = titular;
    }
 
    public Empleado getTitular() {
        return titular;
    }
    
    public int getPorcentaje(Comprador comprador) {
        if (comprador == null) {
            throw new IllegalArgumentException("El comprador no puede ser nulo.");
        }
        return esElTitular(comprador) ? PERSONAL : COMPARTIR;
    }
 
    public int calcularDescuento(int subtotal, Comprador comprador) {
        if (subtotal < 0) {
            throw new IllegalArgumentException("El subtotal no puede ser negativo.");
        }
        return subtotal * getPorcentaje(comprador) / 100;
    }
 
    private boolean esElTitular(Comprador comprador) {
        String login = comprador.getLogin();
        if (login.equals(titular.getLogin())) {
            return true;
        }
        Jugador perfil = titular.getPerfilJugador();
        return perfil != null && login.equals(perfil.getLogin());
    }

}
