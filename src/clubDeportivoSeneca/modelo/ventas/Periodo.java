package clubDeportivoSeneca.modelo.ventas;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;

public enum Periodo {
	DIARIO,
	SEMANAL,
	MENSUAL;
	
	public boolean contiene(LocalDate referencia, LocalDate fecha) {
        if (referencia == null || fecha == null) {
            throw new IllegalArgumentException("Las fechas no pueden ser nulas.");
        }
        switch (this) {
            case DIARIO:
                return fecha.equals(referencia);
            case SEMANAL:
                LocalDate lunes = referencia.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                LocalDate domingo = lunes.plusDays(6);
                return !fecha.isBefore(lunes) && !fecha.isAfter(domingo);
            case MENSUAL:
                return YearMonth.from(fecha).equals(YearMonth.from(referencia));
            default:
                throw new IllegalStateException("Periodo desconocido: " + this);
        }
    }

}
