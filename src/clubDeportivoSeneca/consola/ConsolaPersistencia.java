package clubDeportivoSeneca.consola;

import clubDeportivoSeneca.persistencia.ArchivoPlano;

public class ConsolaPersistencia {
	public static void main(String[] args) {
        ArchivoPlano archivo = new ArchivoPlano();
        System.out.println("Archivo de datos del CDS: " + archivo.getRutaArchivo());
        System.out.println("¿Existe información guardada?: " + archivo.existeArchivo());
        System.out.println("Ejecute ConsolaPrincipal para cargar, modificar y guardar los datos.");
    }
}
