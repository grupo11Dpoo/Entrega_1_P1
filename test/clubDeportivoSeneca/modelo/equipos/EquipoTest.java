package clubDeportivoSeneca.modelo.equipos;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import clubDeportivoSeneca.modelo.deportes.CategoriaEdad;
import clubDeportivoSeneca.modelo.deportes.Futbol;
import clubDeportivoSeneca.modelo.usuario.Jugador;

public class EquipoTest {

    private Futbol futbol;
    private CategoriaEdad sub15;
    private Equipo equipo;

    @BeforeEach
    public void setUp() throws Exception {
        futbol = new Futbol("Futbol", 7, true);
        sub15 = new CategoriaEdad("Sub-15", 13, 15);
        equipo = new Equipo("Seneca Sub-15", futbol, sub15, 10);
    }

    private Jugador jugadorDeEdad(String login, int edad) {
        return new Jugador(login, "1234", "Jugador " + login,
                LocalDate.now().minusYears(edad).minusDays(1));
    }

    @Test
    @DisplayName("Inscribir un jugador valido ocupa un cupo y lo asocia al equipo")
    public void inscribirJugadorValido() throws Exception {
        Jugador ana = jugadorDeEdad("ana", 14);

        Miembro miembro = equipo.inscribirJugador(ana, "Delantera", 9);

        assertNotNull(miembro);
        assertTrue(equipo.contieneJugador(ana));
        assertEquals(9, equipo.cuposDisponibles(), "Debe quedar un cupo menos");
        assertEquals(equipo, ana.getEquipoPorDeporte(futbol));
    }

    @Test
    @DisplayName("Rechaza a un jugador cuya edad no corresponde a la categoria")
    public void rechazaEdadFueraDeCategoria() {
        Jugador mayor = jugadorDeEdad("pedro", 20);

        assertThrows(Exception.class, () -> equipo.inscribirJugador(mayor, "Defensa", 4));
        assertFalse(equipo.contieneJugador(mayor));
        assertEquals(10, equipo.cuposDisponibles(), "Un rechazo no debe cambiar el equipo");
    }

    @Test
    @DisplayName("Rechaza un numero de camiseta ya usado en el equipo")
    public void rechazaCamisetaRepetida() throws Exception {
        equipo.inscribirJugador(jugadorDeEdad("ana", 14), "Delantera", 9);
        Jugador juan = jugadorDeEdad("juan", 14);

        assertThrows(Exception.class, () -> equipo.inscribirJugador(juan, "Defensa", 9));
        assertFalse(equipo.contieneJugador(juan));
    }

    @Test
    @DisplayName("Rechaza la inscripcion cuando el equipo esta lleno")
    public void rechazaEquipoLleno() throws Exception {
        Equipo pequeno = new Equipo("Seneca B", futbol, sub15, 1);
        pequeno.inscribirJugador(jugadorDeEdad("ana", 14), "Delantera", 9);

        assertThrows(Exception.class,
                () -> pequeno.inscribirJugador(jugadorDeEdad("juan", 14), "Defensa", 4));
        assertEquals(0, pequeno.cuposDisponibles());
    }

    @Test
    @DisplayName("Rechaza a un jugador que ya esta en otro equipo del mismo deporte")
    public void rechazaSegundoEquipoMismoDeporte() throws Exception {
        Jugador ana = jugadorDeEdad("ana", 14);
        equipo.inscribirJugador(ana, "Delantera", 9);
        Equipo otro = new Equipo("Otro Sub-15", futbol, sub15, 10);

        assertThrows(Exception.class, () -> otro.inscribirJugador(ana, "Delantera", 10));
        assertFalse(otro.contieneJugador(ana));
    }

    @Test
    @DisplayName("Rechaza inscribir en un equipo inactivo")
    public void rechazaEquipoInactivo() {
        equipo.setActivo(false);

        assertThrows(Exception.class,
                () -> equipo.inscribirJugador(jugadorDeEdad("ana", 14), "Delantera", 9));
    }

    @Test
    @DisplayName("Retirar a un jugador libera su cupo y su camiseta")
    public void retirarJugadorLiberaCupoYCamiseta() throws Exception {
        Jugador ana = jugadorDeEdad("ana", 14);
        equipo.inscribirJugador(ana, "Delantera", 9);

        equipo.retirarJugador(ana);

        assertFalse(equipo.contieneJugador(ana));
        assertEquals(10, equipo.cuposDisponibles());
        assertTrue(equipo.numeroCamisetaDisponible(9));
        assertNull(ana.getEquipoPorDeporte(futbol), "El jugador ya no debe tener equipo de futbol");
    }

    @Test
    @DisplayName("Retirar a un jugador que no pertenece al equipo se rechaza")
    public void rechazaRetirarJugadorAjeno() {
        assertThrows(Exception.class, () -> equipo.retirarJugador(jugadorDeEdad("ana", 14)));
    }

    @Test
    @DisplayName("El equipo solo puede jugar con el minimo de habilitados (7 en futbol)")
    public void puedeJugarConMinimoDeHabilitados() throws Exception {
        for (int i = 1; i <= 6; i++) {
            equipo.inscribirJugador(jugadorDeEdad("j" + i, 14), "Jugador", i);
        }
        assertFalse(equipo.puedeJugar(), "Con 6 jugadores no debe poder jugar");

        equipo.inscribirJugador(jugadorDeEdad("j7", 14), "Jugador", 7);
        assertTrue(equipo.puedeJugar(), "Con 7 jugadores si debe poder jugar");
    }
}