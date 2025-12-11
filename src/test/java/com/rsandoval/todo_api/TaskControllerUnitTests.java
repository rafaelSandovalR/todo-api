package com.rsandoval.todo_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rsandoval.todo_api.controller.TaskController;
import com.rsandoval.todo_api.model.Task;
import com.rsandoval.todo_api.model.User;
import com.rsandoval.todo_api.repository.TaskRepository;
import com.rsandoval.todo_api.repository.UserRepository;
import com.rsandoval.todo_api.service.JwtService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.security.test.context.support.WithMockUser;

// Testing only the TaskController
@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerUnitTests {

    // Spring will automatically inject ("autowire") our "fake postman" (MockMvc)
    @Autowired
    private MockMvc mockMvc;

    // Spring will create a mock version of our repository
    @MockitoBean
    private TaskRepository taskRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private User mockUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setRole("USER");
        // When the controller asks for "testuser", give it this object
        Mockito.when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));
        return user;
    }

    @Test
    @WithMockUser(username = "testuser")
    void testUpdateTask_ShouldUpdateAndReturnTask() throws Exception{
        User user = mockUser();
        Long taskId = 1L;
        // -- ARRANGE -- Simulate  1) 'existingTask': in the database
        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setDescription("Finish Rooney Drawing");
        existingTask.setCompleted(false);
        existingTask.setUser(user);
        // 2) an 'updatedTask': new data being sent
        Task updatedTask = new Task();
        updatedTask.setDescription("Finish Rooney Drawing First Draft");
        updatedTask.setCompleted(true);
        // 3) the final object we expect the 'save' method to return
        Task savedTask = new Task();
        savedTask.setId(taskId);
        savedTask.setDescription("Finish Rooney Drawing First Draft");
        savedTask.setCompleted(true);

        // Convert the updated data object into a JSON string
        objectMapper = new ObjectMapper();
        String updatedTaskAsJson = objectMapper.writeValueAsString(updatedTask);

        // Stub 1: WHEN findById(1L) is called, THEN return our existingTask
        Mockito.when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(existingTask)); // Must wrap in Optional.of() because that is what JpaRepository returns
        // Stub 2: When save() is called with *any* Task, THEN return our final savedTask
        Mockito.when(taskRepository.save(ArgumentMatchers.any(Task.class)))
                .thenReturn(savedTask);

        // -- ACT -- Perform a PUT request to "/api/tasks/1" with the new JSON
        mockMvc.perform(put("/api/tasks/" + taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedTaskAsJson))
                // -- ASSERT -- We expect the results to be:
                .andExpect(status().isOk()) // a) An HTTP 200 OK status
                // b) The JSON response should have the *updated* info
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Finish Rooney Drawing First Draft")))
                .andExpect(jsonPath("$.completed", is(true)));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testDeleteTask_ShouldReturnOkAndCallDeleteById() throws Exception {
        // -- ARRANGE -- Define the ID we want to delete
        User user = mockUser();
        Task task = new Task();
        task.setId(1L);
        task.setUser(user);

        Mockito.when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        // Stubbing a void method; Prevents the mock from throwing an error.
        Mockito.doNothing().when(taskRepository).deleteById(task.getId());

        // -- ACT -- Perform a DELETE request to "api/tasks/1"
        mockMvc.perform(delete("/api/tasks/" + task.getId()))
                .andExpect(status().isOk());

        // -- ASSERT -- Verify the interaction
        // that mock repository's deleteById method was called exactly 1 time with the correct ID
        Mockito.verify(taskRepository, Mockito.times(1)).deleteById(task.getId());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testCreateTask_ShouldCreateNewTask() throws Exception{
        // -- ARRANGE --
        mockUser();
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
        objectMapper = new ObjectMapper();
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
    @WithMockUser(username = "testuser")
    void testGetTask_WhenNotFound_ShouldReturn404() throws Exception {
        mockUser();
        Long nonExistentId = 999L;
        Mockito.when(taskRepository.findById(nonExistentId))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/tasks/" + nonExistentId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetTask_ShouldReturnTask() throws Exception {
        // -- ARRANGE --
        User user = mockUser();
        Long taskId = 1L;
        Task savedTask = new Task();
        savedTask.setId(taskId);
        savedTask.setDescription("Learn DevOps Pipeline");
        savedTask.setCompleted(true);
        savedTask.setUser(user);

        // Train Mockito; Stub findById()
        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(savedTask));

        // -- ACT --
        mockMvc.perform(get("/api/tasks/" + taskId))
                .andDo(print())
                // -- ASSERT --
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Learn DevOps Pipeline")))
                .andExpect(jsonPath("$.completed", is(true)));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetTasksByStatus_ShouldReturnCompletedTasks() throws Exception {
        // -- ARRANGE -- Create mock data
        User user = mockUser();
        Task task1 = new Task();
        task1.setId(1L);
        task1.setDescription("Completed task");
        task1.setCompleted(true);
        task1.setUser(user);

        // Teach mock repository
        Mockito.when(taskRepository.findByUserIdAndCompleted(user.getId(),true))
                .thenReturn(List.of(task1));

        // -- ACT -- Perform a GET request to the new endpoint
        mockMvc.perform(get("/api/tasks/search?completed=true"))
                // -- ASSERT --
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$[0].description", is("Completed task")))
                .andExpect(jsonPath("$[0].completed", is(true)));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetTasksByStatus_ShouldReturnIncompleteTasks() throws Exception {
        User user = mockUser();
        Task task2 = new Task();
        task2.setId(2L);
        task2.setDescription("Incomplete task");
        task2.setCompleted(false);
        task2.setUser(user);

        Mockito.when(taskRepository.findByUserIdAndCompleted(user.getId(), false))
                .thenReturn(List.of(task2));

        mockMvc.perform(get("/api/tasks/search?completed=false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$[0].description", is("Incomplete task")))
                .andExpect(jsonPath("$[0].completed", is(false)));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetAllTasks_ShouldReturnListOfTasks() throws Exception {
        // -- ARRANGE-- Create fake data
        User user = mockUser();
        Task task1 = new Task();
        task1.setId(1L);
        task1.setDescription("Read Dune Messiah");
        task1.setCompleted(false);
        task1.setUser(user);

        // Teach the fake repository what to do
        // WHEN taskRepository.findAll() is called, THEN return our fake list
        Mockito.when(taskRepository.findByUserId(
                ArgumentMatchers.eq(user.getId()),
                ArgumentMatchers.any(Sort.class)))
                .thenReturn(List.of(task1));

        // -- ACT -- Perform a fake GET request to API endpoint
        mockMvc.perform(get("/api/tasks"))
                // -- ASSERT -- (We expect the results to be)
                .andExpect(status().isOk()) // a) An HTTP 200 OK status
                // b) A JSON response; '$' is root of the JSON (the list), '[0]' is the first item in the list
                .andExpect(jsonPath("$[0].description", is("Read Dune Messiah")))
                .andExpect(jsonPath("$[0].completed", is(false)));
    }

}
