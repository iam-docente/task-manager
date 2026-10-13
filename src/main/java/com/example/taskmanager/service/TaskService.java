package com.example.taskmanager.service;

import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de lógica de negocio para la gestión de tareas.
 *
 * Responsabilidades:
 *   - Aplicar reglas de negocio (validaciones, cálculos, decisiones).
 *   - Orquestar las llamadas al repositorio.
 *   - Lanzar excepciones de dominio (ej. TaskNotFoundException).
 *
 * Lo que NO debe hacer:
 *   - Hablar HTTP (eso es del Controller).
 *   - Hablar SQL directamente (eso es del Repository).
 *
 * @Service es una especialización de @Component. Spring lo detecta
 * durante el escaneo y lo registra como bean en el contenedor IoC.
 */
@Service
public class TaskService {

    private final TaskRepository repository;

    /**
     * Inyección por constructor (buena práctica recomendada por Spring).
     *
     * Ventajas frente a @Autowired en el campo:
     *   1. Las dependencias son final (inmutables).
     *   2. La clase es testeable sin Spring (solo pasar un mock).
     *   3. Falla rápido si falta una dependencia.
     */
    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    // ============================================================
    // Consultas (READ)
    // ============================================================

    /**
     * Retorna todas las tareas registradas.
     * Complejidad: O(n) en cantidad de tareas.
     */
    public List<Task> getAllTasks() {
        return repository.findAll();
    }

    /**
     * Retorna solo las tareas marcadas como completadas.
     * Usa el query method findByCompleted del repositorio.
     */
    public List<Task> getCompletedTasks() {
        return repository.findByCompleted(true);
    }

    /**
     * Busca una tarea por su ID.
     * Si no existe, lanza TaskNotFoundException (que el Controller
     * convertirá en HTTP 404).
     *
     * Patrón: Optional.orElseThrow es más limpio que un if (task == null).
     */
    public Task getTaskById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    // ============================================================
    // Comandos (CREATE / UPDATE / DELETE)
    // ============================================================

    /**
     * Crea una nueva tarea con las validaciones de negocio mínimas.
     *
     * Regla de negocio: el título no puede estar vacío ni contener
     * solo espacios en blanco. Esta validación es INDEPENDIENTE
     * de la que hace el DTO, porque:
     *   1. El servicio puede ser invocado por otros servicios
     *      internos (no solo por el controller).
     *   2. Es defensa en profundidad.
     */
    public Task createTask(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }
        Task task = new Task(title.trim());
        return repository.save(task);
    }

    /**
     * Marca una tarea como completada.
     * Si la tarea no existe, lanza TaskNotFoundException.
     *
     * Nota: si la tarea ya estaba completada, la operación es idempotente
     * (no genera error, simplemente guarda el mismo estado).
     */
    public Task completeTask(Long id) {
        Task task = getTaskById(id);
        task.setCompleted(true);
        return repository.save(task);
    }

    /**
     * Elimina una tarea por su ID.
     * Si no existe, lanza TaskNotFoundException (en lugar de fallar silenciosamente).
     *
     * Esto es importante para el cliente: sabe que intentó eliminar algo
     * que no existía, lo cual puede indicar un bug en su lado.
     */
    public void deleteTask(Long id) {
        if (!repository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
