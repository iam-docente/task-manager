package com.example.taskmanager.repository;

import com.example.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de acceso a datos para la entidad Task.
 *
 * Al extender JpaRepository, heredamos gratis:
 *   - save(Task)              → INSERT o UPDATE
 *   - findById(Long)          → SELECT por ID
 *   - findAll()               → SELECT *
 *   - existsById(Long)        → ¿existe?
 *   - deleteById(Long)        → DELETE
 *   - count()                 → COUNT(*)
 *   ... y muchos más.
 *
 * Spring Data JPA genera la implementación automáticamente en runtime.
 * NO necesitamos escribir una clase TaskRepositoryImpl.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Spring Data JPA interpreta el nombre del método como una query:
     *   findByCompleted(true) → SELECT * FROM tasks WHERE completed = true
     *
     * Regla: "findBy" + NombreDelCampo + Condición.
     */
    List<Task> findByCompleted(boolean completed);

    /**
     * Búsqueda por título, ignorando mayúsculas/minúsculas.
     *   findByTitleContainingIgnoreCase("dev") → títulos que contienen "dev"
     */
    List<Task> findByTitleContainingIgnoreCase(String title);
}
