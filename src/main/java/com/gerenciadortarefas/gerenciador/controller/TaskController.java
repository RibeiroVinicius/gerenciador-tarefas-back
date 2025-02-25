package com.gerenciadortarefas.gerenciador.controller;

import com.gerenciadortarefas.gerenciador.controller.dto.CreateTaskDto;
import com.gerenciadortarefas.gerenciador.entities.*;
import com.gerenciadortarefas.gerenciador.services.TaskService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    // GET /tasks - Listar todas as tarefas com paginação e ordenação
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(Pageable pageable, @RequestParam(required = false) String user) {
        return ResponseEntity.ok(taskService.getAllTasks(pageable, user));
    }

    // GET /tasks/{id} - Buscar uma tarefa específica
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Optional<Task> task = taskService.getTaskById(id);
        return task.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /tasks - Criar uma nova tarefa
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody CreateTaskDto dto) {
        return ResponseEntity.ok(taskService.createTask(dto));
    }

    // PUT /tasks/{id} - Atualizar uma tarefa existente
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task) {
        Optional<Task> updatedTask = taskService.updateTask(id, task);
        return updatedTask.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // DELETE /tasks/{id} - Excluir uma tarefa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        return taskService.deleteTask(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // GET /tasks/filter?status={status} - Filtrar tarefas por status
    @GetMapping("/tasks/filter")
    public ResponseEntity<List<Task>> filterTasksByStatus(@RequestParam TaskStatus status) {
        List<Task> tasks = taskService.filterTasksByStatus(status);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Exception handler for {@link IllegalArgumentException}.
     * Returns a 400 Bad Request response with the exception message.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        // Return a 400 Bad Request response with the exception message
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
