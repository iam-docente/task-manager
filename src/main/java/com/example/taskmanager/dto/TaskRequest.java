package com.example.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para la creación de una tarea.
 *
 * Solo contiene el campo "title" porque es lo único que el cliente
 * debe enviar. El "id", "completed" y "createdAt" los genera el servidor.
 *
 * Las anotaciones de Jakarta Validation se ejecutan automáticamente
 * cuando el controller usa @Valid. Si alguna falla, Spring devuelve
 * HTTP 400 (Bad Request) antes de ejecutar el método del controller.
 */
public class TaskRequest {

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200, message = "El título no puede superar los 200 caracteres")
    private String title;

    // ============================================================
    // Constructores
    // ============================================================

    public TaskRequest() {
        // Requerido por Jackson para deserializar el JSON
    }

    public TaskRequest(String title) {
        this.title = title;
    }

    // ============================================================
    // Getter y Setter
    // ============================================================

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
