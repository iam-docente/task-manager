package com.example.taskmanager.controller;

import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para la gestión de tareas.
 *
 * Responsabilidad: traducir HTTP ↔ Java.
 *   - Convierte el body JSON en objetos Java (deserialización).
 *   - Llama al servicio correspondiente.
 *   - Convierte el retorno del servicio en JSON (serialización).
 *   - Devuelve el código HTTP apropiado.
 *
 * Lo que NO debe hacer:
 *   - Lógica de negocio (eso es del Service).
 *   - Acceso a BD (eso es del Repository).
 *
 * @RestController = @Controller + @ResponseBody.
 *   - @Controller → registra la clase como bean web.
 *   - @ResponseBody → todo lo que retornen sus métodos se serializa a JSON.
 *
 * @RequestMapping("/api/tasks") → prefijo base para todos los endpoints.
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    // ============================================================
    // READ
    // ============================================================

    /**
     * GET /api/tasks
     * Retorna todas las tareas registradas.
     *
     * HTTP 200 OK con la lista en el body.
     */
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = service.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    /**
     * GET /api/tasks/completed
     * Retorna solo las tareas completadas.
     *
     * Nota: esta ruta debe declararse ANTES de /{id} para que Spring
     * no interprete "completed" como un ID.
     */
    @GetMapping("/completed")
    public ResponseEntity<List<Task>> getCompletedTasks() {
        return ResponseEntity.ok(service.getCompletedTasks());
    }

    /**
     * GET /api/tasks/{id}
     * Retorna una tarea específica por su ID.
     *
     * HTTP 200 OK si existe.
     * HTTP 404 NOT FOUND si no existe (lanzado por TaskService).
     */
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getTaskById(id));
    }

    // ============================================================
    // CREATE
    // ============================================================

    /**
     * POST /api/tasks
     * Crea una nueva tarea.
     *
     * @Valid → activa las validaciones del DTO (@NotBlank, @Size).
     *          Si fallan, Spring lanza MethodArgumentNotValidException
     *          y GlobalExceptionHandler la convierte en HTTP 400.
     *
     * @RequestBody → indica que el objeto viene del body del request (JSON).
     *
     * HTTP 201 CREATED con la tarea creada (incluye id, createdAt, etc.).
     */
    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody TaskRequest request) {
        Task created = service.createTask(request.getTitle());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ============================================================
    // UPDATE
    // ============================================================

    /**
     * PUT /api/tasks/{id}/complete
     * Marca una tarea como completada.
     *
     * HTTP 200 OK con la tarea actualizada.
     * HTTP 404 NOT FOUND si el ID no existe.
     *
     * Nota: usamos PUT (no PATCH) porque la operación es idempotente:
     * llamarla múltiples veces deja el mismo resultado.
     */
    @PutMapping("/{id}/complete")
    public ResponseEntity<Task> completeTask(@PathVariable Long id) {
        Task completed = service.completeTask(id);
        return ResponseEntity.ok(completed);
    }

    // ============================================================
    // DELETE
    // ============================================================

    /**
     * DELETE /api/tasks/{id}
     * Elimina una tarea por su ID.
     *
     * HTTP 204 NO CONTENT si la eliminación fue exitosa (sin body).
     * HTTP 404 NOT FOUND si el ID no existe.
     *
     * Nota: 204 en lugar de 200 porque no hay nada que devolver.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        service.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
