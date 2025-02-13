package com.gerenciadortarefas.gerenciador.controller.dto;

import java.time.LocalDate;

import com.gerenciadortarefas.gerenciador.entities.TaskStatus;
import com.gerenciadortarefas.gerenciador.entities.User;

public record CreateTaskDto(String title, String description, LocalDate deadline, TaskStatus status, String assignedTo) {

}
