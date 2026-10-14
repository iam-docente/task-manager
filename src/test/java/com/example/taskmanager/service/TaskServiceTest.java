package com.example.taskmanager.service;

import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del TaskService.
 *
 * Estrategia:
 *   - @ExtendWith(MockitoExtension.class): activa Mockito con JUnit 5.
 *   - @Mock: crea un TaskRepository falso (no toca BD).
 *   - @InjectMocks: inyecta el mock en TaskService automáticamente.
 *
 * Cada método @Test prueba UNA cosa concreta.
 * El nombre debe describir QUÉ se espera, no CÓMO se hace.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService - Pruebas unitarias de lógica de negocio")
class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    @InjectMocks
    private TaskService service;

    // ============================================================
    // getAllTasks()
    // ============================================================

    @Test
    @DisplayName("getAllTasks: debe retornar la lista completa del repositorio")
    void shouldReturnAllTasks() {
        // Arrange (preparar)
        Task t1 = new Task("Tarea 1");
        Task t2 = new Task("Tarea 2");
        when(repository.findAll()).thenReturn(Arrays.asList(t1, t2));

        // Act (actuar)
        List<Task> result = service.getAllTasks();

        // Assert (verificar)
        assertEquals(2, result.size());
        assertEquals("Tarea 1", result.get(0).getTitle());
        assertEquals("Tarea 2", result.get(1).getTitle());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllTasks: debe retornar lista vacía si no hay tareas")
    void shouldReturnEmptyListWhenNoTasks() {
        when(repository.findAll()).thenReturn(List.of());

        List<Task> result = service.getAllTasks();

        assertTrue(result.isEmpty());
        verify(repository).findAll();
    }

    // ============================================================
    // getCompletedTasks()
    // ============================================================

    @Test
    @DisplayName("getCompletedTasks: debe retornar solo las tareas completadas")
    void shouldReturnOnlyCompletedTasks() {
        Task completed = new Task("Completada");
        completed.setCompleted(true);
        when(repository.findByCompleted(true)).thenReturn(List.of(completed));

        List<Task> result = service.getCompletedTasks();

        assertEquals(1, result.size());
        assertTrue(result.get(0).isCompleted());
        verify(repository, times(1)).findByCompleted(true);
    }

    // ============================================================
    // getTaskById()
    // ============================================================

    @Test
    @DisplayName("getTaskById: debe retornar la tarea si existe")
    void shouldReturnTaskWhenExists() {
        Task task = new Task("Existente");
        task.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(task));

        Task result = service.getTaskById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Existente", result.getTitle());
        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("getTaskById: debe lanzar TaskNotFoundException si no existe")
    void shouldThrowWhenTaskNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        TaskNotFoundException ex = assertThrows(
                TaskNotFoundException.class,
                () -> service.getTaskById(99L)
        );
        assertTrue(ex.getMessage().contains("99"));
        verify(repository).findById(99L);
    }

    // ============================================================
    // createTask()
    // ============================================================

    @Test
    @DisplayName("createTask: debe crear tarea con título válido")
    void shouldCreateTaskWithValidTitle() {
        when(repository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task result = service.createTask("Estudiar DevOps");

        assertNotNull(result);
        assertEquals("Estudiar DevOps", result.getTitle());
        assertFalse(result.isCompleted());
        verify(repository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("createTask: debe normalizar el título con trim()")
    void shouldTrimTitleOnCreate() {
        when(repository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task result = service.createTask("   Título con espacios   ");

        assertEquals("Título con espacios", result.getTitle());
    }

    @Test
    @DisplayName("createTask: debe lanzar IllegalArgumentException si el título es null")
    void shouldThrowWhenTitleIsNull() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.createTask(null)
        );
        assertEquals("El título no puede estar vacío", ex.getMessage());
        verify(repository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("createTask: debe lanzar IllegalArgumentException si el título está vacío")
    void shouldThrowWhenTitleIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> service.createTask(""));
        verify(repository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("createTask: debe lanzar IllegalArgumentException si el título es solo espacios")
    void shouldThrowWhenTitleIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> service.createTask("     "));
        verify(repository, never()).save(any(Task.class));
    }

    // ============================================================
    // completeTask()
    // ============================================================

    @Test
    @DisplayName("completeTask: debe marcar la tarea como completada y guardarla")
    void shouldCompleteExistingTask() {
        Task task = new Task("Pendiente");
        task.setId(1L);
        task.setCompleted(false);
        when(repository.findById(1L)).thenReturn(Optional.of(task));
        when(repository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task result = service.completeTask(1L);

        assertTrue(result.isCompleted());

        // Verificamos con ArgumentCaptor que el objeto guardado es el mismo
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(repository).save(captor.capture());
        assertTrue(captor.getValue().isCompleted());
        assertEquals("Pendiente", captor.getValue().getTitle());
    }

    @Test
    @DisplayName("completeTask: debe lanzar TaskNotFoundException si no existe")
    void shouldThrowWhenCompletingNonExistent() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> service.completeTask(99L));
        verify(repository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("completeTask: debe ser idempotente si la tarea ya estaba completada")
    void shouldBeIdempotentWhenAlreadyCompleted() {
        Task task = new Task("Ya estaba completada");
        task.setId(1L);
        task.setCompleted(true);
        when(repository.findById(1L)).thenReturn(Optional.of(task));
        when(repository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task result = service.completeTask(1L);

        assertTrue(result.isCompleted());
        verify(repository).save(any(Task.class));
    }

    // ============================================================
    // deleteTask()
    // ============================================================

    @Test
    @DisplayName("deleteTask: debe eliminar la tarea si existe")
    void shouldDeleteExistingTask() {
        when(repository.existsById(1L)).thenReturn(true);

        service.deleteTask(1L);

        verify(repository, times(1)).existsById(1L);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteTask: debe lanzar TaskNotFoundException si no existe")
    void shouldThrowWhenDeletingNonExistent() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(TaskNotFoundException.class, () -> service.deleteTask(99L));
        verify(repository, never()).deleteById(anyLong());
    }
}
