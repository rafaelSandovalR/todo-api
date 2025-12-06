package com.rsandoval.todo_api.repository;

import com.rsandoval.todo_api.model.Task;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByCompleted (boolean completed);

    List<Task> findByUserId(Long userId, Sort sort);
    // Version of the original method that is user-specific
    List<Task> findByUserIdAndCompleted(Long userId, boolean completed);
}
