package com.rsandoval.todo_api;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // A unique ID for each task

    private String description; // The text of the to-do item
    private boolean completed = false; // a flag to see if it's done

    public Long getId(){ return id; }
    public void setId(Long id){ this.id = id; }

    public String getDescription(){ return description; }
    public void setDescription(String description){ this.description = description; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed){ this.completed = completed; }

}
