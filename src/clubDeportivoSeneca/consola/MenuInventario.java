package clubDeportivoSeneca.consola;

import java.util.List;
import java.util.Map;

import clubDeportivoSeneca.modelo.inventario.Articulo;
import clubDeportivoSeneca.modelo.inventario.InventarioArticulos;
import clubDeportivoSeneca.modelo.inventario.SugerenciaProducto;
import clubDeportivoSeneca.modelo.inventario.TipoSugerencia;
import clubDeportivoSeneca.modelo.usuario.Empleado;
import clubDeportivoSeneca.modelo.usuario.Usuario;
import clubDeportivoSeneca.persistencia.ArchivoPlano;
import clubDeportivoSeneca.sistema.ClubDeportivoSeneca;

public class MenuInventario extends ConsolaBase {
    public MenuInventario(ClubDeportivoSeneca club, Usuario usuario, ArchivoPlano persistencia, LecturaConsola lectura) {
        super(club, usuario, persistencia, lectura);
    }

    public void ejecutar() {
        System.out.println("\n--- INVENTARIO Y SUGERENCIAS ---\n1. Ver stock\n2. Registrar artículo\n3. Reabastecer artículo\n4. Crear sugerencia de empleado\n5. Ver sugerencias\n6. Aprobar/rechazar sugerencia\n0. Volver");
        try {
            switch (opcionAutorizada("MenuInventario", leer("Opción: "))) {
                case "1": Map<String, Map<Articulo, Integer>> stock = club.getInventarioArticulos().getStock(); 
                if (stock.isEmpty()) System.out.println("Inventario vacío."); 
                else stock.forEach((ubicacion, articulos) -> articulos.forEach((a, cantidad) -> System.out.println(ubicacion + " | " + a.getNombre() + " | precio=$" + a.getPrecio() + " | cantidad=" + cantidad))); break;
                case "2": exigirAdmin(); Articulo articulo = new Articulo(leer("Nombre: "), leer("Categoría: "), entero("Precio en pesos: ")); club.registrarArticulo(articulo, entero("Cantidad inicial: ")); guardarSilencioso(); System.out.println("Artículo registrado."); break;
                case "3": exigirAdmin(); Articulo a = elegirArticulo(); club.getInventarioArticulos().reabastecer(a, entero("Cantidad a agregar: "), InventarioArticulos.UBICACION_TIENDA); guardarSilencioso(); System.out.println("Stock actualizado."); break;
                case "4": List<Empleado> emps = club.getEmpleados(); 
                if (emps.isEmpty()) 
                	throw new IllegalStateException("No hay empleados."); Empleado autor = usuarioActual instanceof Empleado e ? e : emps.get(indiceLista("Empleado", emps.stream().map(Empleado::getLogin).toList())); TipoSugerencia tipo = elegirTipoSugerencia(); SugerenciaProducto sug = new SugerenciaProducto(autor, leer("Nombre del producto sugerido: "), leer("Descripción: "), tipo); club.registrarSugerencia(sug); guardarSilencioso(); System.out.println("Sugerencia registrada."); break;
                case "5": 
                	if (club.getSugerencias().isEmpty()) System.out.println("No hay sugerencias."); 
                	else club.getSugerencias().forEach(s -> System.out.println(s.getNombre() + " | " + s.getTipo() + " | " + s.getEstado() + " | empleado=" + s.getEmpleado().getLogin())); break;
                case "6": exigirAdmin(); 
                if (club.getSugerencias().isEmpty()) 
                	throw new IllegalStateException("No hay sugerencias."); List<SugerenciaProducto> sugerencias = club.getSugerencias(); for (int i=0;i<sugerencias.size();i++) System.out.println((i+1)+". "+sugerencias.get(i).getNombre()+" ("+sugerencias.get(i).getEstado()+")"); SugerenciaProducto s = sugerencias.get(indice("Número de sugerencia", sugerencias.size())); String decision = leer("Aprobar (A) o rechazar (R): "); if (decision.equalsIgnoreCase("A")) s.aprobar(); else if (decision.equalsIgnoreCase("R")) s.rechazar(); else throw new IllegalArgumentException("Decisión no válida."); guardarSilencioso(); System.out.println("Sugerencia actualizada."); break;
                case "0": return;
                default: System.out.println("Opción no válida.");
            }
        } catch (Exception e) { error(e); }
    }

}

