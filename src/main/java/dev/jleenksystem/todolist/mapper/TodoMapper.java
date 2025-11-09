package dev.jleenksystem.todolist.mapper;

import dev.jleenksystem.todolist.dto.TodoDto;
import dev.jleenksystem.todolist.model.Todo;

public class TodoMapper {
    public static TodoDto toDto(Todo todo) {
        TodoDto dto = new TodoDto();
        dto.setId(todo.getId());
        dto.setDescription(todo.getDescription());
        dto.setCompleted(todo.isCompleted());
        return dto;
    }

    public static Todo toEntity(TodoDto dto) {
        return new Todo(dto.getId(), dto.getDescription(), dto.getCompleted());
    }
}