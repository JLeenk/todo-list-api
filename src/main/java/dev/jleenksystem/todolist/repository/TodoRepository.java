package dev.jleenksystem.todolist.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import dev.jleenksystem.todolist.model.Todo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class TodoRepository {
    private final JdbcTemplate jdbc;

    public TodoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Todo> mapper = (rs, rowNum) -> mapTodo(rs);

    private Todo mapTodo(ResultSet rs) throws SQLException {
        return new Todo(
                rs.getLong("id"),
                rs.getString("description"),
                rs.getInt("completed") != 0);
    }

    public List<Todo> findAll() {
        return jdbc.query("SELECT id, description, completed FROM todos", mapper);
    }

    public Optional<Todo> findById(Long id) {
        try {
            Todo t = jdbc.queryForObject("SELECT id, description, completed FROM todos WHERE id = ?", mapper, id);
            return Optional.ofNullable(t);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Todo save(Todo todo) {
        jdbc.update("INSERT INTO todos (description, completed) VALUES (?, ?)",
                todo.getDescription(), todo.isCompleted() ? 1 : 0);
        Long id = jdbc.queryForObject("SELECT last_insert_rowid()", Long.class);
        todo.setId(id);
        return todo;
    }

    public int update(Long id, Todo todo) {
        return jdbc.update("UPDATE todos SET description = ?, completed = ? WHERE id = ?",
                todo.getDescription(), todo.isCompleted() ? 1 : 0, id);
    }

    public int delete(Long id) {
        return jdbc.update("DELETE FROM todos WHERE id = ?", id);
    }
}