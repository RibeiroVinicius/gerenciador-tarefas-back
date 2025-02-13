package com.gerenciadortarefas.gerenciador.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.gerenciadortarefas.gerenciador.entities.Task;
import com.gerenciadortarefas.gerenciador.entities.TaskStatus;
import com.gerenciadortarefas.gerenciador.entities.User;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // Verifica se existe uma tarefa com o mesmo título
    boolean existsByTitle(String title);

    // Verifica se existe uma tarefa com o mesmo título, mas com ID diferente (usado para update)
    boolean existsByTitleAndIdNot(String title, Long id);

    // Busca todas as tarefas atribuídas a um usuário específico
    List<Task> findByAssignedTo(User assignedTo, Pageable pageable);

    // Filtra as tarefas pelo status e pelo usuário responsável
    List<Task> findByStatusAndAssignedTo(TaskStatus status, User assignedTo);

    // Filtra as tarefas pelo status
    List<Task> findByStatus(TaskStatus status);

    Optional<Task> findById(Long id);
}