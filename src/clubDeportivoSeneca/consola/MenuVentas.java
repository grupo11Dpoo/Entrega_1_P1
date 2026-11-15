package clubDeportivoSeneca.consola;

import java.time.LocalDate;

import clubDeportivoSeneca.modelo.usuario.Administrador;
import clubDeportivoSeneca.modelo.usuario.Usuario;
import clubDeportivoSeneca.persistencia.ArchivoPlano;
import clubDeportivoSeneca.sistema.ClubDeportivoSeneca;

public class MenuVentas extends ConsolaBase {
    public MenuVentas(ClubDeportivoSeneca club, Usuario usuario, ArchivoPlano persistencia, LecturaConsola lectura) {
        super(club, usuario, persistencia, lectura);
    }

    public void ejecutar() {
        System.out.println("\n--- VENTAS Y REPORTES ---\n1. Listar ventas\n2. Venta de artículos deportivos\n3. Venta de cafetería\n4. Reporte de ventas por rango de fechas\n5. Reporte resumido por rubro\n0. Volver");
        try {
            switch (opcionAutorizada("MenuVentas", leer("Opción: "))) {
                case "1": 
                	if (club.getVentas().isEmpty()) System.out.println("No hay ventas."); 
                	else club.getVentas().forEach(v -> System.out.println(v.getFecha() + " | " + v.getRubro() + " | comprador=" + v.getComprador().getLogin() + " | total=$" + v.getPrecioTotal() + " | finalizada=" + v.isFinalizada())); break;
                case "2": ventaTienda(); break;
                case "3": ventaCafeteria(); break;
                case "4": LocalDate desde = fecha("Desde (AAAA-MM-DD): "); LocalDate hasta = fecha("Hasta (AAAA-MM-DD): "); club.consultarVentas(desde, hasta).forEach(v -> System.out.println(v.getFecha()+" | "+v.getRubro()+" | $"+v.getPrecioTotal())); break;
                case "5": 
                	if (usuarioActual instanceof Administrador admin) admin.reporteVentas(); 
                else System.out.println("Solo el administrador puede consultar el reporte consolidado."); break;
                case "0": return;
                default: System.out.println("Opción no válida.");
            }
        } catch (Exception e) { error(e); }
    }

}
