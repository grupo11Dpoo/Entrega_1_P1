package clubDeportivoSeneca.consola;

import java.util.List;

import clubDeportivoSeneca.modelo.turnos.SolicitudCambioTurno;
import clubDeportivoSeneca.modelo.turnos.Turno;
import clubDeportivoSeneca.modelo.usuario.Empleado;
import clubDeportivoSeneca.modelo.usuario.Usuario;
import clubDeportivoSeneca.persistencia.ArchivoPlano;
import clubDeportivoSeneca.sistema.ClubDeportivoSeneca;

public class MenuTurnos extends ConsolaBase {
    public MenuTurnos(ClubDeportivoSeneca club, Usuario usuario, ArchivoPlano persistencia, LecturaConsola lectura) {
        super(club, usuario, persistencia, lectura);
    }

    public void ejecutar() {
        System.out.println("\n--- TURNOS ---\n1. Listar turnos\n2. Asignar turno\n3. Verificar mínimos de personal por fecha\n4. Listar solicitudes de cambio\n5. Solicitar cambio de turno\n6. Aprobar/rechazar solicitud (administrador)\n0. Volver");
        try {
            switch (opcionAutorizada("MenuTurnos", leer("Opción: "))) {
                case "1": 
                	if (club.getGestorTurnos().getTurnos().isEmpty()) System.out.println("No hay turnos."); 
                	else club.getGestorTurnos().getTurnos().forEach(t -> System.out.println(t.getEmpleado().getLogin() + " | " + t.getFecha() + " | " + t.getHoraInicio() + "-" + t.getHoraFin())); break;
                case "2": exigirAdmin(); List<Empleado> empleados = club.getEmpleados(); 
                if (empleados.isEmpty()) 
                	throw new IllegalStateException("No hay empleados registrados."); Empleado emp = empleados.get(indiceLista("Empleado", empleados.stream().map(Empleado::getNombre).toList())); Turno turno = club.getGestorTurnos().asignarTurno(emp, fecha("Fecha (AAAA-MM-DD): "), hora("Hora inicio (HH:MM): "), hora("Hora fin (HH:MM): ")); guardarSilencioso(); System.out.println("Turno asignado: " + turno.getHoraInicio() + "-" + turno.getHoraFin()); break;
                case "3": boolean cumple = club.getGestorTurnos().cumpleMinimos(fecha("Fecha (AAAA-MM-DD): ")); System.out.println(cumple ? "Se cumplen los mínimos de personal." : "No se cumplen los mínimos: se requieren 2 entrenadores y 1 fisioterapeuta por franja."); break;
                case "4": if (club.getGestorTurnos().getSolicitudes().isEmpty()) System.out.println("No hay solicitudes."); 
                else club.getGestorTurnos().getSolicitudes().forEach(s -> System.out.println(s.getEmpleado().getLogin() + " | " + s.getEstado() + " | nueva fecha=" + s.getNuevaFecha())); break;
                case "5": solicitarCambioTurno(); guardarSilencioso(); break;
                case "6": exigirAdmin(); List<SolicitudCambioTurno> solicitudes = club.getGestorTurnos().getSolicitudes(); 
                if(solicitudes.isEmpty()) 
                	throw new IllegalStateException("No hay solicitudes."); 
                for(int i=0;i<solicitudes.size();i++) System.out.println((i+1)+". "+solicitudes.get(i).getEmpleado().getLogin()+" | "+solicitudes.get(i).getEstado()+" | "+solicitudes.get(i).getNuevaFecha()); SolicitudCambioTurno sol = solicitudes.get(indice("Número de solicitud",solicitudes.size())); String resp=leer("Aprobar (A) o rechazar (R): "); 
                if(resp.equalsIgnoreCase("A")) System.out.println(club.getGestorTurnos().aprobarCambio(sol) ? "Cambio aprobado." : "Cambio rechazado porque incumple las reglas."); 
                else if(resp.equalsIgnoreCase("R")) { club.getGestorTurnos().rechazarCambio(sol); System.out.println("Cambio rechazado."); } 
                else throw new IllegalArgumentException("Respuesta no válida."); guardarSilencioso(); break;
                case "0": return;
                default: System.out.println("Opción no válida.");
            }
        } catch (Exception e) { error(e); }
    }

}

