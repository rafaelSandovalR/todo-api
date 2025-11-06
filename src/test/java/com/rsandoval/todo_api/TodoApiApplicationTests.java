package com.rsandoval.todo_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Testing only the TaskController
@WebMvcTest(TaskController.class)
class TodoApiApplicationTests {

    // Spring will automatically inject ("autowire") our "fake postman" (MockMvc)
    @Autowired
    private MockMvc mockMvc;

    // Spring will create a mock version of our repository
    @MockitoBean
    private TaskRepository taskRepository;

	@Test
	void contextLoads() {
	}

    @Test
    void testDeleteTask_ShouldReturnOkAndCallDeleteById() throws Exception {
        // -- ARRANGE -- Define the ID we want to delete
        Long taskId = 1L;
        // Stubbing a void method; Prevents the mock from throwing an error.
        Mockito.doNothing().when(taskRepository).deleteById(taskId);

        // -- ACT -- Perform a DELETE request to "api/tasks/1"
        mockMvc.perform(delete("/api/tasks/" + taskId))
                .andExpect(status().isOk());

        // -- ASSERT -- Verify the interaction
        // that mock repository's deleteById method was called exactly 1 time with the correct ID
        Mockito.verify(taskRepository, Mockito.times(1)).deleteById(taskId);
    }

    @Test
    void testCreateTask_ShouldCreateNewTask() throws Exception{
        // -- ARRANGE --
        // New task we are "sending" to the server
        Task newTask = new Task();
        newTask.setDescription("Watch Incendies");
        newTask.setCompleted(false);
        // Task we expect the server to "return" with assigned ID
        Task savedTask = new Task();
        savedTask.setId(1L);
        savedTask.setDescription("Watch Incendies");
        savedTask.setCompleted(false);

        // Convert 'newTask' object into a JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String newTaskAsJson = objectMapper.writeValueAsString(newTask);

        // When taskRepository.save() is called with *any* Task object THEN return our 'savedTask'
        Mockito.when(taskRepository.save(ArgumentMatchers.any(Task.class)))
                .thenReturn(savedTask);

        // -- ACT -- Perform a POST request with our JSON and tell server we are sending JSON
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newTaskAsJson))
                // -- ASSERT -- We expect the results to be:
                .andExpect(status().isCreated()) // a) An HTTP 201 Created status
                // b) The returned JSON should have the new ID
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Watch Incendies")))
                .andExpect(jsonPath("$.completed", is(false)));
    }

    @Test
    void testGetAllTasks_ShouldReturnListOfTasks() throws Exception {
        // -- ARRANGE-- Create fake data
        Task task1 = new Task();
        task1.setId(1L);
        task1.setDescription("Read Dune Messiah");
        task1.setCompleted(false);

        // Teach the fake repository what to do
        // WHEN taskRepository.findAll() is called, THEN return our fake list
        Mockito.when(taskRepository.findAll()).thenReturn(List.of(task1));

        // -- ACT -- Perform a fake GET request to API endpoint
        mockMvc.perform(get("/api/tasks"))
                // -- ASSERT -- (We expect the results to be)
                .andExpect(status().isOk()) // a) An HTTP 200 OK status
                // b) A JSON response; '$' is root of the JSON (the list), '[0]' is the first item in the list
                .andExpect(jsonPath("$[0].description", is("Read Dune Messiah")))
                .andExpect(jsonPath("$[0].completed", is(false)));
    }

}
