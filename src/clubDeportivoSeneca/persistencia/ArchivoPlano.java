package clubDeportivoSeneca.persistencia;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.AtomicMoveNotSupportedException;

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
        Path temporal = Files.createTempFile(carpeta, "cds-", ".tmp");
        try {
            try (ObjectOutputStream salida = new ObjectOutputStream(
                    new BufferedOutputStream(Files.newOutputStream(temporal)))) {
                salida.writeObject(club);
            }
            try {
                Files.move(temporal, archivo, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporal);
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
