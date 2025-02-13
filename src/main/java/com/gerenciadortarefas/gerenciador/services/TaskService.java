package com.gerenciadortarefas.gerenciador.services;

import com.gerenciadortarefas.gerenciador.controller.dto.CreateTaskDto;
import com.gerenciadortarefas.gerenciador.entities.*;
import com.gerenciadortarefas.gerenciador.repositories.TaskRepository;
import com.gerenciadortarefas.gerenciador.repositories.UserRepository;

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
    private UserRepository userRepository;

    @Autowired
    private UserService userService; // Serviço para obter informações do usuário atual

    public List<Task> getAllTasks(Pageable pageable, String username) {
        System.out.println("username: " + username);
        User currentUser = userRepository.findByName(username).orElseThrow(); // Obter o usuário atual
        System.out.println("currentUser: " + currentUser);
        System.out.println("pageable: " + pageable);       
        System.out.println(taskRepository.findByAssignedTo(currentUser, pageable).toString());
        return taskRepository.findByAssignedTo(currentUser, pageable);
    }

    public Optional<Task> getTaskById(Long id) {
        User currentUser = userService.getCurrentUser();
        return taskRepository.findById(id)
                .filter(task -> task.getAssignedTo().equals(currentUser)); // Validação de acesso
    }

    public Task createTask(CreateTaskDto dto) {
        var task = new Task();
        System.out.println("dto: " + dto);
        User user = userRepository.findByName(dto.assignedTo()).orElseThrow();

        validateDeadline(dto.deadline());
        validateTitleDuplication(dto.title());

        task.setDescription(dto.description());
        task.setStatus(dto.status());
        task.setTitle(dto.title());
        task.setCreatedOn(LocalDate.now()); // Garante que createdOn seja gerado automaticamente
        task.setAssignedTo(user);
        task.setDeadline(dto.deadline()); // Modificar para o prazo definido pelo usuário
        return taskRepository.save(task);
    }

    public Optional<Task> updateTask(Long id, Task taskDetails) {
        validateDeadline(taskDetails.getDeadline());
        return taskRepository.findById(id).map(task -> {
            task.setTitle(taskDetails.getTitle());
            task.setDescription(taskDetails.getDescription());
            task.setStatus(taskDetails.getStatus());
            task.setDeadline(taskDetails.getDeadline());
            return taskRepository.save(task);
        });
    }

    public boolean deleteTask(Long id) {
        return taskRepository.findById(id)
                .filter(task -> task.getId().equals(id)) // Validação de acesso
                .map(task -> {
                    taskRepository.delete(task);
                    return true;
                }).orElse(false);
    }

    public List<Task> filterTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    // Métodos de validação

    private void validateTitleDuplication(String title) {
        if (taskRepository.existsByTitle(title)) {
            throw new IllegalArgumentException("Já existe uma tarefa com este título.");
        }
    }

    private void validateDeadline(LocalDate deadline) {
        if (deadline != null && deadline.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("O prazo (deadline) não pode ser uma data passada.");
        }
    }
}
