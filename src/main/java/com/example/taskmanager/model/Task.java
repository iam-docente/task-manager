package com.example.taskmanager.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad que representa una tarea en el sistema.
 *
 * Anotaciones JPA:
 *   @Entity       → marca la clase como entidad persistente.
 *   @Table        → define el nombre de la tabla en la BD.
 *   @Id           → clave primaria.
 *   @GeneratedValue → estrategia de generación (IDENTITY = autoincrement).
 *   @Column       → personaliza la columna.
 *   @PrePersist   → callback antes de insertar.
 */
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false)
    private boolean completed;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Se ejecuta automáticamente ANTES de insertar una fila.
     * Garantiza que createdAt y completed siempre tengan valores válidos.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.completed = false;
    }

    // ============================================================
    // Constructores
    // ============================================================

    public Task() {
        // Requerido por JPA
    }

    public Task(String title) {
        this.title = title;
        this.completed = false;
    }

    // ============================================================
    // Getters y Setters
    // ============================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // ============================================================
    // equals y hashCode (basados en id)
    // ============================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // ============================================================
    // toString (útil para debugging y logs)
    // ============================================================

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", completed=" + completed +
                ", createdAt=" + createdAt +
                '}';
    }
}
