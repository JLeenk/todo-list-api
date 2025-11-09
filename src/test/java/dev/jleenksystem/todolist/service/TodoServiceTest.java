package dev.jleenksystem.todolist.service;

import dev.jleenksystem.todolist.dto.TodoDto;
import dev.jleenksystem.todolist.model.Todo;
import dev.jleenksystem.todolist.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class TodoServiceTest {

    private TodoRepository repo;
    private TodoService service;

    @BeforeEach
    void setup() {
        repo = mock(TodoRepository.class);
        service = new TodoService(repo);
    }

    @Test
    void getAllTodos_returnsList() {
        List<Todo> mockList = Arrays.asList(
                new Todo(1L, "Test 1", false),
                new Todo(2L, "Test 2", true));

        when(repo.findAll()).thenReturn(mockList);

        List<TodoDto> result = service.getAll();

        assertThat(result).hasSize(2);
        verify(repo).findAll();
    }

    @Test
    void getTodoById_existing_returnsTodo() {
        Todo t = new Todo(1L, "Hello", false);
        when(repo.findById(1L)).thenReturn(Optional.of(t));

        TodoDto result = service.getById(1L);

        assertThat(result.getDescription()).isEqualTo("Hello");
        verify(repo).findById(1L);
    }

    @Test
    void getTodoById_notExisting_throwsException() {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Todo not found");
    }

    @Test
    void createTodo_savesAndReturnsTodo() {
        TodoDto todoDto = new TodoDto(null, "New", false);
        Todo savedTodo = new Todo(10L, "New", false);

        when(repo.save(any(Todo.class))).thenReturn(savedTodo);

        TodoDto result = service.create(todoDto);

        assertThat(result.getId()).isEqualTo(10L);
        verify(repo).save(any(Todo.class));
    }

    @Test
    void updateTodo_updatesFields() {
        Todo existing = new Todo(1L, "Old", false);
        TodoDto updateData = new TodoDto(null, "Updated", true);

        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(repo.update(eq(1L), any())).thenReturn(1);

        TodoDto result = service.patch(1L, updateData);

        assertThat(result.getDescription()).isEqualTo("Updated");
        assertThat(result.getCompleted()).isTrue();

        ArgumentCaptor<Todo> captor = ArgumentCaptor.forClass(Todo.class);
        verify(repo).update(eq(1L), captor.capture());

        Todo updatedPassed = captor.getValue();
        assertThat(updatedPassed.getDescription()).isEqualTo("Updated");
        assertThat(updatedPassed.isCompleted()).isTrue();
    }

    @Test
    void deleteTodo_existing_deletesSuccessfully() {
        when(repo.delete(1L)).thenReturn(1);

        service.delete(1L);

        verify(repo).delete(1L);
    }

    @Test
void deleteTodo_notExisting_throwsException() {
    when(repo.delete(999L)).thenReturn(0);

    assertThatThrownBy(() -> service.delete(999L))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Todo not found for delete");
}
}