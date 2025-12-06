package com.rsandoval.todo_api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // A unique ID for each task

    private String description; // The text of the to-do item
    private boolean completed = false; // a flag to see if it's done

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // Creates a foreign key column
    @JsonIgnore // CRITICAL: Do not include the User data in the Task JSON response
    private User user;

    public Long getId(){ return id; }
    public void setId(Long id){ this.id = id; }

    public String getDescription(){ return description; }
    public void setDescription(String description){ this.description = description; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed){ this.completed = completed; }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
}
