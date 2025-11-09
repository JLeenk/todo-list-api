package dev.jleenksystem.todolist.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.jleenksystem.todolist.dto.TodoDto;
import dev.jleenksystem.todolist.exception.TodoNotFoundException;
import dev.jleenksystem.todolist.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TodoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TodoService service;

    @InjectMocks
    private TodoController controller;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new dev.jleenksystem.todolist.exception.GlobalExceptionHandler())
                .build();
    }

    // ============================
    // Happy path tests
    // ============================

    @Test
    void getAll_returnsListOfTodos() throws Exception {
        List<TodoDto> todos = List.of(
                new TodoDto(1L, "Test 1", false),
                new TodoDto(2L, "Test 2", true)
        );

        when(service.getAll()).thenReturn(todos);

        mockMvc.perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Test 1"))
                .andExpect(jsonPath("$[1].completed").value(true));

        verify(service).getAll();
    }

    @Test
    void getById_existingTodo_returnsTodo() throws Exception {
        TodoDto todo = new TodoDto(5L, "Hello", false);

        when(service.getById(5L)).thenReturn(todo);

        mockMvc.perform(get("/todos/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.description").value("Hello"))
                .andExpect(jsonPath("$.completed").value(false));

        verify(service).getById(5L);
    }

    @Test
    void create_returnsCreatedTodo() throws Exception {
        TodoDto request = new TodoDto(null, "New", false);
        TodoDto saved = new TodoDto(10L, "New", false);

        when(service.create(any(TodoDto.class))).thenReturn(saved);

        mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.description").value("New"))
                .andExpect(jsonPath("$.completed").value(false));

        verify(service).create(any(TodoDto.class));
    }

    @Test
    void update_returnsUpdatedTodo() throws Exception {
        TodoDto request = new TodoDto(null, "Updated", true);
        TodoDto updated = new TodoDto(3L, "Updated", true);

        when(service.patch(eq(3L), any(TodoDto.class))).thenReturn(updated);

        mockMvc.perform(patch("/todos/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.description").value("Updated"))
                .andExpect(jsonPath("$.completed").value(true));

        verify(service).patch(eq(3L), any(TodoDto.class));
    }

    @Test
    void delete_returnsNoContent() throws Exception {
        doNothing().when(service).delete(7L);

        mockMvc.perform(delete("/todos/7"))
                .andExpect(status().isNoContent());

        verify(service).delete(7L);
    }

    // ============================
    // Negative tests (404 Not Found)
    // ============================

    @Test
    void getById_notFound_returns404() throws Exception {
        when(service.getById(999L)).thenThrow(new TodoNotFoundException("Todo not found"));

        mockMvc.perform(get("/todos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void update_notFound_returns404() throws Exception {
        TodoDto request = new TodoDto(null, "Updated", true);
        when(service.patch(eq(999L), any(TodoDto.class)))
                .thenThrow(new TodoNotFoundException("Todo not found"));

        mockMvc.perform(patch("/todos/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void delete_notFound_returns404() throws Exception {
        doThrow(new TodoNotFoundException("Todo not found")).when(service).delete(999L);

        mockMvc.perform(delete("/todos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    // ============================
    // Negative tests (500 Internal Server Error)
    // ============================

    @Test
    void create_unexpectedException_returns500() throws Exception {
        TodoDto request = new TodoDto(null, "New", false);

        when(service.create(any(TodoDto.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal Server Error"));
    }

    @Test
    void getById_unexpectedException_returns500() throws Exception {
        when(service.getById(1L))
                .thenThrow(new RuntimeException("Unexpected"));

        mockMvc.perform(get("/todos/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal Server Error"));
    }
}
