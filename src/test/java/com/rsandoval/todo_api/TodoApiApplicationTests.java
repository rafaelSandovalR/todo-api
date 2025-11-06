package com.rsandoval.todo_api;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
