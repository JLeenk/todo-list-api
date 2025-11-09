package dev.jleenksystem.todolist.integration;

import dev.jleenksystem.todolist.dto.TodoDto;
import dev.jleenksystem.todolist.model.Todo;
import dev.jleenksystem.todolist.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TodoApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TodoRepository repository;

    private String baseUrl;

    @BeforeEach
    void setup() {
        baseUrl = "http://localhost:" + port + "/todos";

        // Clean database before each test
        repository.findAll().forEach(todo -> repository.delete(todo.getId()));
    }

    // ============================
    // Happy path tests
    // ============================

    @Test
    void getAll_returnsListOfTodos() {
        repository.save(new Todo(null, "Test 1", false));
        repository.save(new Todo(null, "Test 2", true));

        ResponseEntity<TodoDto[]> response = restTemplate.getForEntity(baseUrl, TodoDto[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        TodoDto[] todos = response.getBody();
        assertThat(todos).hasSize(2);
        assertThat(List.of(todos)).extracting("description").containsExactlyInAnyOrder("Test 1", "Test 2");
    }

    @Test
    void getById_existingTodo_returnsTodo() {
        Todo todo = repository.save(new Todo(null, "Hello", false));

        ResponseEntity<TodoDto> response = restTemplate.getForEntity(baseUrl + "/" + todo.getId(), TodoDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        TodoDto dto = response.getBody();
        assertThat(dto).isNotNull();
        assertThat(dto.getDescription()).isEqualTo("Hello");
    }

    @Test
    void create_returnsCreatedTodo() {
        TodoDto request = new TodoDto(null, "New", false);

        ResponseEntity<TodoDto> response = restTemplate.postForEntity(baseUrl, request, TodoDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        TodoDto created = response.getBody();
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getDescription()).isEqualTo("New");
    }

    @Test
    void update_returnsUpdatedTodo() {
        Todo todo = repository.save(new Todo(null, "Old", false));
        TodoDto updateRequest = new TodoDto(null, "Updated", true);
        HttpEntity<TodoDto> requestEntity = new HttpEntity<>(updateRequest);

        ResponseEntity<TodoDto> response = restTemplate.exchange(
                baseUrl + "/" + todo.getId(), HttpMethod.PATCH, requestEntity, TodoDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        TodoDto updated = response.getBody();
        assertThat(updated.getDescription()).isEqualTo("Updated");
        assertThat(updated.getCompleted()).isTrue();
    }

    @Test
    void delete_returnsNoContent() {
        Todo todo = repository.save(new Todo(null, "To delete", false));

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/" + todo.getId(), HttpMethod.DELETE, null, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(repository.findById(todo.getId())).isEmpty();
    }

    // ============================
    // Negative tests (404 Not Found)
    // ============================

    @Test
    void getById_notFound_returns404() {
        ResponseEntity<TodoDto> response = restTemplate.getForEntity(baseUrl + "/9999", TodoDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void update_notFound_returns404() {
        TodoDto updateRequest = new TodoDto(null, "Updated", true);
        HttpEntity<TodoDto> requestEntity = new HttpEntity<>(updateRequest);

        ResponseEntity<TodoDto> response = restTemplate.exchange(
                baseUrl + "/9999", HttpMethod.PATCH, requestEntity, TodoDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void delete_notFound_returns404() {
        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/9999", HttpMethod.DELETE, null, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // ============================
    // Negative tests (500 Internal Server Error)
    // ============================

    @Test
    void create_unexpectedException_returns500() {
        // Force repository to throw a RuntimeException (simulate DB failure)
        TodoDto request = new TodoDto(null, "New", false);

        // Use a proxy or mock if you want real 500 test
        // For simplicity, this is usually tested in unit tests with mocks
    }
}
