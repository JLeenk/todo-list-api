package dev.jleenksystem.todolist.repository;

import dev.jleenksystem.todolist.model.Todo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "/reset.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class TodoRepositoryTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    TodoRepository repository;

    @Test
    void findAll_returnsAllRows() {
        List<Todo> todos = repository.findAll();
        assertThat(todos).hasSize(2);
    }

    @Test
    void findById_existingId_returnsTodo() {
        Optional<Todo> todo = repository.findById(1L);
        assertThat(todo).isPresent();
        assertThat(todo.get().getDescription()).isEqualTo("Test Todo 1");
    }

    @Test
    void findById_notExisting_returnsEmpty() {
        Optional<Todo> todo = repository.findById(999L);
        assertThat(todo).isEmpty();
    }

    @Test
    void save_insertsTodoAndReturnsWithId() {
        Todo todo = new Todo(null, "New Item", false);
        Todo saved = repository.save(todo);

        assertThat(saved.getId()).isNotNull();
        assertThat(repository.findById(saved.getId())).isPresent();
    }

    @Test
    void update_updatesTodo() {
        Todo update = new Todo(null, "Updated", true);
        int rows = repository.update(1L, update);

        assertThat(rows).isEqualTo(1);

        Todo updated = repository.findById(1L).get();
        assertThat(updated.getDescription()).isEqualTo("Updated");
        assertThat(updated.isCompleted()).isTrue();
    }

    @Test
    void delete_removesTodo() {
        int rows = repository.delete(1L);
        assertThat(rows).isEqualTo(1);
        assertThat(repository.findById(1L)).isEmpty();
    }
}  