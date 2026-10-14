package com.example.taskmanager.controller;

import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración web del TaskController.
 *
 * Estrategia:
 *   - @WebMvcTest(TaskController.class): levanta SOLO la capa web.
 *   - @MockBean: reemplaza el TaskService real por un mock.
 *   - MockMvc: simula peticiones HTTP sin arrancar Tomcat.
 *
 * Se verifica el CONTRATO HTTP: rutas, códigos de estado, formato JSON.
 * La lógica de negocio ya está cubierta en TaskServiceTest.
 */
@WebMvcTest(TaskController.class)
@DisplayName("TaskController - Pruebas de integración web")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService service;

    // ============================================================
    // GET /api/tasks
    // ============================================================

    @Test
    @DisplayName("GET /api/tasks: debe retornar 200 con la lista de tareas")
    void shouldReturnAllTasks() throws Exception {
        Task t1 = new Task("Tarea 1");
        t1.setId(1L);
        Task t2 = new Task("Tarea 2");
        t2.setId(2L);
        when(service.getAllTasks()).thenReturn(Arrays.asList(t1, t2));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Tarea 1"))
                .andExpect(jsonPath("$[1].title").value("Tarea 2"));

        verify(service, times(1)).getAllTasks();
    }

    @Test
    @DisplayName("GET /api/tasks: debe retornar 200 con lista vacía si no hay tareas")
    void shouldReturnEmptyList() throws Exception {
        when(service.getAllTasks()).thenReturn(List.of());

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ============================================================
    // GET /api/tasks/completed
    // ============================================================

    @Test
    @DisplayName("GET /api/tasks/completed: debe retornar 200 con las completadas")
    void shouldReturnCompletedTasks() throws Exception {
        Task completed = new Task("Completada");
        completed.setId(1L);
        completed.setCompleted(true);
        when(service.getCompletedTasks()).thenReturn(List.of(completed));

        mockMvc.perform(get("/api/tasks/completed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].completed").value(true));

        verify(service).getCompletedTasks();
    }

    // ============================================================
    // GET /api/tasks/{id}
    // ============================================================

    @Test
    @DisplayName("GET /api/tasks/{id}: debe retornar 200 si la tarea existe")
    void shouldReturnTaskById() throws Exception {
        Task task = new Task("Existente");
        task.setId(1L);
        when(service.getTaskById(1L)).thenReturn(task);

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Existente"));
    }

    @Test
    @DisplayName("GET /api/tasks/{id}: debe retornar 404 si no existe")
    void shouldReturn404WhenNotFound() throws Exception {
        when(service.getTaskById(99L)).thenThrow(new TaskNotFoundException(99L));

        mockMvc.perform(get("/api/tasks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Tarea no encontrada con id: 99"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    // ============================================================
    // POST /api/tasks
    // ============================================================

    @Test
    @DisplayName("POST /api/tasks: debe retornar 201 al crear tarea válida")
    void shouldCreateTask() throws Exception {
        Task created = new Task("Nueva tarea");
        created.setId(1L);
        when(service.createTask(anyString())).thenReturn(created);

        String jsonBody = "{\"title\":\"Nueva tarea\"}";

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Nueva tarea"))
                .andExpect(jsonPath("$.completed").value(false));

        verify(service).createTask("Nueva tarea");
    }

    @Test
    @DisplayName("POST /api/tasks: debe retornar 400 si el título está vacío")
    void shouldReturn400WhenTitleIsEmpty() throws Exception {
        String jsonBody = "{\"title\":\"\"}";

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fields.title").exists());

        // El servicio NUNCA debe haber sido invocado (la validación del DTO lo bloqueó)
        verify(service, never()).createTask(anyString());
    }

    @Test
    @DisplayName("POST /api/tasks: debe retornar 400 si el título supera 200 caracteres")
    void shouldReturn400WhenTitleTooLong() throws Exception {
        String longTitle = "x".repeat(201);
        String jsonBody = "{\"title\":\"" + longTitle + "\"}";

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.title").exists());

        verify(service, never()).createTask(anyString());
    }

    @Test
    @DisplayName("POST /api/tasks: debe retornar 400 si falta el campo title")
    void shouldReturn400WhenTitleMissing() throws Exception {
        String jsonBody = "{}";

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest());

        verify(service, never()).createTask(anyString());
    }

    // ============================================================
    // PUT /api/tasks/{id}/complete
    // ============================================================

    @Test
    @DisplayName("PUT /api/tasks/{id}/complete: debe retornar 200 si completa la tarea")
    void shouldCompleteTask() throws Exception {
        Task completed = new Task("Pendiente");
        completed.setId(1L);
        completed.setCompleted(true);
        when(service.completeTask(1L)).thenReturn(completed);

        mockMvc.perform(put("/api/tasks/1/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.completed").value(true));

        verify(service).completeTask(1L);
    }

    @Test
    @DisplayName("PUT /api/tasks/{id}/complete: debe retornar 404 si no existe")
    void shouldReturn404WhenCompletingNonExistent() throws Exception {
        when(service.completeTask(99L)).thenThrow(new TaskNotFoundException(99L));

        mockMvc.perform(put("/api/tasks/99/complete"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Tarea no encontrada con id: 99"));
    }

    // ============================================================
    // DELETE /api/tasks/{id}
    // ============================================================

    @Test
    @DisplayName("DELETE /api/tasks/{id}: debe retornar 204 si elimina correctamente")
    void shouldDeleteTask() throws Exception {
        doNothing().when(service).deleteTask(1L);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(service).deleteTask(1L);
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id}: debe retornar 404 si no existe")
    void shouldReturn404WhenDeletingNonExistent() throws Exception {
        doThrow(new TaskNotFoundException(99L)).when(service).deleteTask(anyLong());

        mockMvc.perform(delete("/api/tasks/99"))
                .andExpect(status().isNotFound());

        verify(service).deleteTask(99L);
    }
}
