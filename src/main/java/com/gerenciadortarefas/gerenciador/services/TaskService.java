package com.gerenciadortarefas.gerenciador.services;

import com.gerenciadortarefas.gerenciador.entities.*;
import com.gerenciadortarefas.gerenciador.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService; // Serviço para obter informações do usuário atual

    public Page<Task> getAllTasks(Pageable pageable) {
        User currentUser = userService.getCurrentUser(); // Obter o usuário atual
        return taskRepository.findByAssignedTo(currentUser, pageable);
    }

    public Optional<Task> getTaskById(Long id) {
        User currentUser = userService.getCurrentUser();
        return taskRepository.findById(id)
                .filter(task -> task.getAssignedTo().equals(currentUser)); // Validação de acesso
    }

    public Task createTask(Task task) {
        validateTitleDuplication(task.getTitle());
        validateDeadline(task.getDeadline());
        
        task.setCreatedOn(LocalDate.now()); // Geração automática de createdOn
        User currentUser = userService.getCurrentUser();
        return taskRepository.findById(task.getId())
            .filter(existingTask -> existingTask.getAssignedTo().equals(currentUser))
            .orElse(taskRepository.save(task));
    }

    public Optional<Task> updateTask(Long id, Task taskDetails) {
        validateDeadline(taskDetails.getDeadline());
        
        User currentUser = userService.getCurrentUser();
        return taskRepository.findById(id)
                .filter(task -> task.getAssignedTo().equals(currentUser)) // Validação de acesso
                .map(task -> {
                    validateTitleDuplication(taskDetails.getTitle(), id);
                    task.setTitle(taskDetails.getTitle());
                    task.setDescription(taskDetails.getDescription());
                    task.setStatus(taskDetails.getStatus());
                    task.setDeadline(taskDetails.getDeadline());
                    return taskRepository.save(task);
                });
    }

    public boolean deleteTask(Long id) {
        User currentUser = userService.getCurrentUser();
        return taskRepository.findById(id)
                .filter(task -> task.getAssignedTo().equals(currentUser)) // Validação de acesso
                .map(task -> {
                    taskRepository.delete(task);
                    return true;
                }).orElse(false);
    }

    public List<Task> filterTasksByStatus(TaskStatus status) {
        User currentUser = userService.getCurrentUser();
        return taskRepository.findByStatusAndAssignedTo(status, currentUser);
    }

    // Métodos de validação

    private void validateTitleDuplication(String title) {
        if (taskRepository.existsByTitle(title)) {
            throw new IllegalArgumentException("Já existe uma tarefa com este título.");
        }
    }

    private void validateTitleDuplication(String title, Long id) {
        if (taskRepository.existsByTitleAndIdNot(title, id)) {
            throw new IllegalArgumentException("Já existe uma tarefa com este título.");
        }
    }

    private void validateDeadline(LocalDate deadline) {
        if (deadline != null && deadline.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("O prazo não pode ser uma data passada.");
        }
    }
}
