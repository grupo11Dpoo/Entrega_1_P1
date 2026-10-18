package clubDeportivoSeneca.persistencia;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import clubDeportivoSeneca.sistema.ClubDeportivoSeneca;


public class ArchivoPlano {
    private final Path archivo;

    public ArchivoPlano() {
        this(Paths.get(System.getProperty("user.home"), ".clubDeportivoSeneca", "cds.dat"));
    }

    public ArchivoPlano(String rutaArchivo) {
        this(Paths.get(rutaArchivo));
    }

    public ArchivoPlano(Path archivo) {
        if (archivo == null) throw new IllegalArgumentException("La ruta del archivo es obligatoria.");
        this.archivo = archivo.toAbsolutePath().normalize();
    }

    public Path getRutaArchivo() { return archivo; }

    public void guardar(ClubDeportivoSeneca club) throws IOException {
        if (club == null) throw new IllegalArgumentException("No hay datos del club para guardar.");
        Path carpeta = archivo.getParent();
        if (carpeta != null) Files.createDirectories(carpeta);
        try (ObjectOutputStream salida = new ObjectOutputStream(
                new BufferedOutputStream(Files.newOutputStream(archivo)))) {
            salida.writeObject(club);
            salida.flush();
        }
    }

    public ClubDeportivoSeneca cargar() throws IOException, ClassNotFoundException {
        if (!Files.exists(archivo)) return null;
        try (ObjectInputStream entrada = new ObjectInputStream(
                new BufferedInputStream(Files.newInputStream(archivo)))) {
            Object objeto = entrada.readObject();
            if (!(objeto instanceof ClubDeportivoSeneca))
                throw new IOException("El archivo no contiene datos válidos del Club Deportivo Séneca.");
            return (ClubDeportivoSeneca) objeto;
        }
    }

    public boolean existeArchivo() { return Files.isRegularFile(archivo); }
}
