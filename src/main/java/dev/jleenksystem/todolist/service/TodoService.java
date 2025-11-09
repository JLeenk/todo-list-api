package dev.jleenksystem.todolist.service;

import org.springframework.stereotype.Service;

import dev.jleenksystem.todolist.dto.TodoDto;
import dev.jleenksystem.todolist.exception.TodoNotFoundException;
import dev.jleenksystem.todolist.mapper.TodoMapper;
import dev.jleenksystem.todolist.model.Todo;
import dev.jleenksystem.todolist.repository.TodoRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TodoService {
    private final TodoRepository repo;

    public TodoService(TodoRepository repo) {
        this.repo = repo;
    }

    public List<TodoDto> getAll() {
        return repo.findAll().stream().map(TodoMapper::toDto).collect(Collectors.toList());
    }

    public TodoDto getById(Long id) {
        Todo todo = repo.findById(id).orElseThrow(() -> new TodoNotFoundException("Todo not found"));
        return TodoMapper.toDto(todo);
    }

    public TodoDto create(TodoDto dto) {
        Todo todo = TodoMapper.toEntity(dto);
        Todo saved = repo.save(todo);
        return TodoMapper.toDto(saved);
    }

    public TodoDto patch(Long id, TodoDto dto) {
        Todo existing = repo.findById(id).orElseThrow(() -> new TodoNotFoundException("Todo not found for update"));
        existing.setDescription(dto.getDescription());
        existing.setCompleted(dto.getCompleted());
        repo.update(id, existing);
        return TodoMapper.toDto(existing);
    }

    public void delete(Long id) {
        int rows = repo.delete(id);
        if (rows == 0) {
            throw new TodoNotFoundException("Todo not found for delete");
        }
    }
}
