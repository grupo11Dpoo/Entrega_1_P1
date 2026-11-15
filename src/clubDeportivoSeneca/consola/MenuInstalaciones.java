package clubDeportivoSeneca.consola;

import java.util.List;
import clubDeportivoSeneca.modelo.instalaciones.Instalacion;
import clubDeportivoSeneca.modelo.instalaciones.Reserva;
import clubDeportivoSeneca.modelo.usuario.Usuario;
import clubDeportivoSeneca.persistencia.ArchivoPlano;
import clubDeportivoSeneca.sistema.ClubDeportivoSeneca;

public class MenuInstalaciones extends ConsolaBase {
	public MenuInstalaciones(ClubDeportivoSeneca club, Usuario usuario, ArchivoPlano persistencia, LecturaConsola lectura) {
        super(club, usuario, persistencia, lectura);
    }

    public void ejecutar() {
        System.out.println("\n--- INSTALACIONES Y RESERVAS ---\n1. Listar instalaciones\n2. Registrar instalación\n3. Ver historial de reservas\n4. Crear reserva\n5. Cancelar reserva\n0. Volver");
        try {
            switch (opcionAutorizada("MenuInstalaciones", leer("Opción: "))) {
                case "1": List<Instalacion> instalaciones = club.getInventarioInstalaciones().getInstalaciones(); 
                if (instalaciones.isEmpty()) System.out.println("No hay instalaciones.");
                else instalaciones.forEach(i -> System.out.println(i.getId() + " | " + i.getDisciplina().getNombre() + " | capacidades=" + i.getCapacidadesPermitidas())); break;
                case "2": exigirAdmin(); crearInstalacion(); guardarSilencioso(); break;
                case "3": List<Reserva> reservas = club.getInventarioInstalaciones().getHistorial().getReservas(); 
                if (reservas.isEmpty()) System.out.println("No hay reservas."); else reservas.forEach(System.out::println); break;
                case "4": crearReserva(); guardarSilencioso(); break;
                case "5": exigirAdmin(); List<Reserva> activas = club.getInventarioInstalaciones().getHistorial().getReservas(); 
                if (activas.isEmpty()) throw new IllegalStateException("No hay reservas para cancelar."); mostrarReservas(activas); Reserva r = activas.get(indice("Número de reserva", activas.size())); club.cancelarReserva(r); guardarSilencioso(); System.out.println("Reserva cancelada."); break;
                case "0": return;
                default: System.out.println("Opción no válida.");
            }
        } catch (Exception e) { error(e); }
    }

}
