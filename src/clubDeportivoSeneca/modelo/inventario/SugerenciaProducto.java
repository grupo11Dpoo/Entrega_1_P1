package clubDeportivoSeneca.modelo.inventario;

import clubDeportivoSeneca.modelo.usuario.Empleado;
public class SugerenciaProducto implements java.io.Serializable {
	private final Empleado empleado;
	private final String nombre;
	private final String descripcion;
	private final TipoSugerencia tipo;
	private EstadoSugerencia estado;
	
	public SugerenciaProducto (Empleado empleado, String nombre, String descripcion, TipoSugerencia tipo) {
		if (empleado == null) {
			throw new IllegalArgumentException ("La sugerencia debe tener un empleado.");
		}
		if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre sugerido no puede estar vacío.");
        }
		if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripción no puede estar vacía.");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de sugerencia es obligatorio.");
        }
        this.empleado = empleado;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.estado = EstadoSugerencia.PENDIENTE;
	}
	
	public Empleado getEmpleado () {
		return empleado;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public String getDescripcion() {
		return descripcion;
	}
	
	public TipoSugerencia getTipo() {
		return tipo;
	}
	
	public EstadoSugerencia getEstado() {
		return estado;
	}
	
	public void aprobar () {
		validarPendiente();
		estado = EstadoSugerencia.RECHAZADA;
	}
	
	public void rechazar() {
		validarPendiente();
		estado = EstadoSugerencia.RECHAZADA;
	}
	
	public void validarPendiente() {
		if (estado != EstadoSugerencia.PENDIENTE) {
			throw new IllegalStateException ("La sugerencia fue resuelta: " + estado);
		}
	}

}
