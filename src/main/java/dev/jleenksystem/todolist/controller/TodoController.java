package dev.jleenksystem.todolist.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dev.jleenksystem.todolist.dto.TodoDto;
import dev.jleenksystem.todolist.service.TodoService;

import java.util.List;

@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    // GET /todos
    @GetMapping
    public ResponseEntity<List<TodoDto>> getAllTodos() {
        return ResponseEntity.ok(todoService.getAll());
    }

    // GET /todos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<TodoDto> getTodoById(@PathVariable Long id) {
        TodoDto todo = todoService.getById(id);
        return todo != null ? ResponseEntity.ok(todo)
                : ResponseEntity.notFound().build();
    }

    // POST /todos
    @PostMapping
    public ResponseEntity<TodoDto> createTodo(@RequestBody TodoDto todo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(todoService.create(todo));
    }

    // PATCH /todos/{id}
    @PatchMapping("/{id}")
    public ResponseEntity<TodoDto> updateTodo(@PathVariable Long id, @RequestBody TodoDto partialUpdate) {
        TodoDto updated = todoService.patch(id, partialUpdate);
        return updated != null ? ResponseEntity.ok(updated)
                : ResponseEntity.notFound().build();
    }

    // DELETE /todos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        todoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}