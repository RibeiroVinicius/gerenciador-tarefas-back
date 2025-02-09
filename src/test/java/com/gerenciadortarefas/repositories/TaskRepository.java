package com.gerenciadortarefas.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.gerenciadortarefas.models.Task;
import com.gerenciadortarefas.models.TaskStatus;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(TaskStatus status);
}