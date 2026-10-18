package clubDeportivoSeneca.modelo.usuario;

public abstract class Usuario implements java.io.Serializable {
	
	private final String login;
	private String password;
	
	protected Usuario(String login, String password) {
		this.login = login;
		this.password = password;
	}

	public String getLogin() {
		return login;
	}
	
	public boolean autenticar(String login, String password) {
		
		if(this.login.equals(login) && this.password.equals(password)) {
			return true;
		} else {
			return false;
		}
	}
	
	public void cambiarPassword(String passwordActual, String nuevaPassword) throws Exception{
		if(passwordActual.equals(nuevaPassword)) {
			this.password = nuevaPassword;
		} else {
			throw new Exception("La contraseña actual no coincide.");
		}
	}
	
}
