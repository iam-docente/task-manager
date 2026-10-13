package com.example.taskmanager.exception;

/**
 * Excepción lanzada cuando se busca una tarea que no existe en la base de datos.
 *
 * Hereda de RuntimeException (excepción no checked) porque:
 *   1. Representa un error de negocio, no de infraestructura.
 *   2. No queremos obligar a cada llamador a envolverla en try/catch.
 *   3. Spring la manejará automáticamente vía @RestControllerAdvice (ver Fase 4).
 */
public class TaskNotFoundException extends RuntimeException {

    /**
     * Construye la excepción con un mensaje descriptivo que incluye el ID buscado.
     * El mensaje es lo que se devolverá al cliente HTTP en el body de la respuesta 404.
     *
     * @param id Identificador de la tarea que no fue encontrada.
     */
    public TaskNotFoundException(Long id) {
        super("Tarea no encontrada con id: " + id);
    }
}
