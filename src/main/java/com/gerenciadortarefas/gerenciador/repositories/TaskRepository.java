package com.gerenciadortarefas.gerenciador.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gerenciadortarefas.gerenciador.entities.Task;
import com.gerenciadortarefas.gerenciador.entities.TaskStatus;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStatus(TaskStatus status);

}