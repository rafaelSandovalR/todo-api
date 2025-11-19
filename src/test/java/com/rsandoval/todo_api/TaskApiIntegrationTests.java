package com.rsandoval.todo_api;

import com.rsandoval.todo_api.model.Task;
import com.rsandoval.todo_api.repository.TaskRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

// 1. We use @SpringBootTest to start the entire application, and tell it to run a random, unused port to avoid conflicts.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskApiIntegrationTests {

    // 2. Spring will automatically inject a real HTTP client that is configured to talk to our running test server
    @Autowired
    private TestRestTemplate restTemplate;

    // 3. We also inject the real repository so we can clean up the database after each test.
    @Autowired
    private TaskRepository taskRepository;

    // 4. This helper method cleans the database after every test, so our tests don't interfere with each other.
    @AfterEach
    void tearDown() {
        taskRepository.deleteAll();
    }

    @Test
    void testCreateTask_ShouldSaveToDatabase() {
        // -- ARRANGE -- Create the task object we want to send
        Task task = new Task();
        task.setDescription("Watch new PTA movie");

        // -- ACT -- Use the real HTTP client to send a real POST request
        ResponseEntity<Task> response = restTemplate.postForEntity(
                "/api/tasks",
                task,
                Task.class
        );

        // -- ASSERT --
        // A. Check the HTTP response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull(); // Check that the body is not null before using it
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getDescription()).isEqualTo("Watch new PTA movie");

        // B. Check the database directly
        Long newId = response.getBody().getId();
        Optional<Task> optionalTask = taskRepository.findById(newId); // Check for presence before calling .get()
        assertThat(optionalTask).isPresent();
        Task savedTask = optionalTask.get(); // Now that we've confirmed it's present, we can safely .get() it
        assertThat(savedTask.getDescription()).isEqualTo("Watch new PTA movie");
    }

    @Test
    void testGetAllTasks_ShouldReturnAllTasks() {
        Task task1 = new Task();
        task1.setDescription("Test task 1");
        taskRepository.save(task1);

        Task task2 = new Task();
        task2.setDescription("Test task 2");
        taskRepository.save(task2);

        ResponseEntity<Task[]> response = restTemplate.getForEntity(
                "/api/tasks",
                Task[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isEqualTo(2);
        assertThat(response.getBody()[0].getDescription()).isEqualTo("Test task 1");
        assertThat(response.getBody()[1].getDescription()).isEqualTo("Test task 2");
    }
}
