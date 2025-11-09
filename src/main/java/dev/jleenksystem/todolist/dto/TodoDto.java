package dev.jleenksystem.todolist.dto;

import jakarta.validation.constraints.NotBlank;

public class TodoDto {
    private Long id;

    @NotBlank(message = "description must not be blank")
    private String description;

    private Boolean completed;

    public TodoDto() {
    }

    public TodoDto(Long id, String description, Boolean completed) {
        this.id = id;
        this.description = description;
        this.completed = completed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}