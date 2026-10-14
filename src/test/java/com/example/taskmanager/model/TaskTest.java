package com.example.taskmanager.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    // ---------- equals / hashCode ----------

    @Test
    void equals_mismoObjeto_devuelveTrue() {
        Task a = new Task("A");
        assertEquals(a, a);                       // this == o
    }

    @Test
    void equals_null_devuelveFalse() {
        Task a = new Task("A");
        assertNotEquals(null, a);                 // o == null
        assertFalse(a.equals(null));
    }

    @Test
    void equals_otraClase_devuelveFalse() {
        Task a = new Task("A");
        assertNotEquals(a, "no soy una Task");    // getClass() != o.getClass()
    }

    @Test
    void equals_mismoId_devuelveTrue_yMismoHashCode() {
        Task a = new Task("A"); a.setId(1L);
        Task b = new Task("Otro título"); b.setId(1L);

        assertEquals(a, b);                       // solo importa el id
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equals_distintoId_devuelveFalse() {
        Task a = new Task("A"); a.setId(1L);
        Task b = new Task("A"); b.setId(2L);

        assertNotEquals(a, b);
    }

    @Test
    void equals_ambosSinId_sonIguales() {
        // Documenta el comportamiento actual: dos tareas sin persistir son "iguales"
        assertEquals(new Task("A"), new Task("B"));
    }

    // ---------- toString ----------

    @Test
    void toString_incluyeLosCampos() {
        Task t = new Task("Comprar pan");
        t.setId(7L);

        String s = t.toString();

        assertTrue(s.contains("id=7"));
        assertTrue(s.contains("title='Comprar pan'"));
        assertTrue(s.contains("completed=false"));
    }

    // ---------- @PrePersist ----------

    @Test
    void onCreate_asignaCreatedAt_yCompletedFalse() {
        Task t = new Task("A");
        t.setCompleted(true);

        LocalDateTime antes = LocalDateTime.now();
        t.onCreate();                             // accesible: mismo paquete

        assertNotNull(t.getCreatedAt());
        assertFalse(t.getCreatedAt().isBefore(antes));
        assertFalse(t.isCompleted());
    }

    // ---------- constructores y setters ----------

    @Test
    void constructorVacio_dejaValoresPorDefecto() {
        Task t = new Task();

        assertNull(t.getId());
        assertNull(t.getTitle());
        assertFalse(t.isCompleted());
        assertNull(t.getCreatedAt());
    }

    @Test
    void setters_actualizanLosCampos() {
        Task t = new Task();
        LocalDateTime fecha = LocalDateTime.of(2026, 1, 1, 10, 0);

        t.setTitle("Nuevo");
        t.setCreatedAt(fecha);

        assertEquals("Nuevo", t.getTitle());
        assertEquals(fecha, t.getCreatedAt());
    }
}
