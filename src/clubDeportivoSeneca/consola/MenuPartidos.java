package clubDeportivoSeneca.consola;

import java.util.List;

import clubDeportivoSeneca.modelo.equipos.Equipo;
import clubDeportivoSeneca.modelo.partidos.PartidoInterno;
import clubDeportivoSeneca.modelo.usuario.Usuario;
import clubDeportivoSeneca.persistencia.ArchivoPlano;
import clubDeportivoSeneca.sistema.ClubDeportivoSeneca;

public class MenuPartidos extends ConsolaBase {
    public MenuPartidos(ClubDeportivoSeneca club, Usuario usuario, ArchivoPlano persistencia, LecturaConsola lectura) {
        super(club, usuario, persistencia, lectura);
    }

    public void ejecutar() {
        System.out.println("\n--- PARTIDOS INTERNOS ---\n1. Listar partidos\n2. Crear partido interno\n3. Registrar marcador y finalizar partido\n0. Volver");
        try {
            switch (opcionAutorizada("MenuPartidos", leer("Opción: "))) {
                case "1": 
                	if (club.getPartidosInternos().isEmpty()) System.out.println("No hay partidos internos."); 
                	else club.getPartidosInternos().forEach(System.out::println); break;
                case "2": exigirAdmin(); Equipo local = elegirEquipo(); Equipo visitante = elegirEquipo(); 
                if (local == visitante) 
                	throw new IllegalArgumentException("Debes elegir dos equipos distintos."); PartidoInterno p = new PartidoInterno(fecha("Fecha (AAAA-MM-DD): "), local.getDeporte(), local, visitante); club.registrarPartidoInterno(p); guardarSilencioso(); System.out.println("Partido creado."); break;
                case "3": exigirAdmin(); List<PartidoInterno> partidos = club.getPartidosInternos(); 
                if (partidos.isEmpty()) 
                	throw new IllegalStateException("No hay partidos internos."); 
                for (int i=0;i<partidos.size();i++) System.out.println((i+1)+". "+partidos.get(i).getFecha()+" | "+partidos.get(i).getEquipoLocal().getNombre()+" vs "+partidos.get(i).getEquipoVisitante().getNombre()+" | finalizado="+partidos.get(i).isFinalizado()); PartidoInterno partido = partidos.get(indice("Número de partido", partidos.size())); partido.registrarMarcador(entero("Marcador local: "), entero("Marcador visitante: ")); partido.finalizarPartido(); guardarSilencioso(); System.out.println("Marcador registrado y partido finalizado."); break;
                case "0": return;
                default: System.out.println("Opción no válida.");
            }
        } catch (Exception e) { error(e); }
    }

}

