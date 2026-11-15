package clubDeportivoSeneca.consola;

// para proyecto 2, para leer las consolas
import java.io.InputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public final class LecturaConsola {
	private final Scanner scanner;
    private final PrintStream salida;
    public LecturaConsola() { this(System.in, System.out); }
    public LecturaConsola(InputStream entrada, PrintStream salida) {
        this.scanner = new Scanner(entrada);
        this.salida = salida;
    }
    public String leer(String mensaje) {
        salida.print(mensaje);
        try { return scanner.nextLine().trim(); }
        catch (NoSuchElementException e) { throw new IllegalStateException("Se terminó la entrada de consola.", e); }
    }
    public String obligatorio(String mensaje) {
        String valor = leer(mensaje);
        if (valor.isBlank()) throw new IllegalArgumentException("El dato es obligatorio.");
        return valor;
    }
    public int entero(String mensaje) {
        try { return Integer.parseInt(obligatorio(mensaje)); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Debes escribir un número entero."); }
    }
    public LocalDate fecha(String mensaje) {
        try { return LocalDate.parse(obligatorio(mensaje)); }
        catch (DateTimeParseException e) { throw new IllegalArgumentException("Fecha inválida. Usa AAAA-MM-DD."); }
    }
    public LocalTime hora(String mensaje) {
        try { return LocalTime.parse(obligatorio(mensaje)); }
        catch (DateTimeParseException e) { throw new IllegalArgumentException("Hora inválida. Usa HH:MM."); }
    }
}

