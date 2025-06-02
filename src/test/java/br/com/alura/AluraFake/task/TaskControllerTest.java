package br.com.alura.AluraFake.task;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import java.util.stream.Stream;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.util.exceptions.ConflictException;
import br.com.alura.AluraFake.util.exceptions.EntityNotFoundException;
import br.com.alura.AluraFake.util.exceptions.InvalidStateException;

import org.springframework.http.MediaType;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskUseCase taskUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @ParameterizedTest
    @MethodSource("provideInvalidOpenTextTasks")
    public void newOpenTextTask__should_return_bad_request_when_invalid(NewOpenTextTaskDTO invalidDto, String field) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/task/new/opentext")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value(field))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    private static Stream<Arguments> provideInvalidOpenTextTasks() {
        return Stream.of(
            // Invalid courseId
            arguments(new NewOpenTextTaskDTO(null, "valid statement", 1), "courseId"),
            
            // Invalid order
            arguments(new NewOpenTextTaskDTO(1L, "valid statement", null), "order"),
            arguments(new NewOpenTextTaskDTO(1L, "valid statement", -1), "order"),
            
            // Invalid statement
            arguments(new NewOpenTextTaskDTO(1L, null, 1), "statement"),
            arguments(new NewOpenTextTaskDTO(1L, "", 1), "statement"),
            arguments(new NewOpenTextTaskDTO(1L, "len", 1), "statement")
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidSingleChoiceTasks")
    public void newSingleChoiceTask__should_return_bad_request_when_new_single_choice_task_dto_is_invalid(NewSingleChoiceTaskDTO invalidDto, String field) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/task/new/singlechoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value(field))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    private static Stream<Arguments> provideInvalidSingleChoiceTasks() {
        List<OptionDTO> notEnoughOptions = Arrays.asList(new OptionDTO());
        List<OptionDTO> tooManyOptions = Arrays.asList(new OptionDTO(), new OptionDTO(), new OptionDTO(), new OptionDTO(), new OptionDTO(), new OptionDTO());
        List<OptionDTO> enoughOptions = Arrays.asList(new OptionDTO(), new OptionDTO());

        return Stream.of(
            // Invalid courseId
            arguments(new NewSingleChoiceTaskDTO(null, "valid statement", 1, enoughOptions), "courseId"),
            
            // Invalid order
            arguments(new NewSingleChoiceTaskDTO(1L, "valid statement", null, enoughOptions), "order"),
            arguments(new NewSingleChoiceTaskDTO(1L, "valid statement", -1, enoughOptions), "order"),
            
            // Invalid statement
            arguments(new NewSingleChoiceTaskDTO(1L, null, 1, enoughOptions), "statement"),
            arguments(new NewSingleChoiceTaskDTO(1L, "", 1, enoughOptions), "statement"),
            arguments(new NewSingleChoiceTaskDTO(1L, "len", 1, enoughOptions), "statement"),
            
            // Invalid options
            arguments(new NewSingleChoiceTaskDTO(1L, "valid statement", 1, notEnoughOptions), "options"),
            arguments(new NewSingleChoiceTaskDTO(1L, "valid statement", 1, tooManyOptions), "options")
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidMultipleChoiceTasks")
    public void newMultipleChoiceTask__should_return_bad_request_when_new_multiple_choice_task_dto_is_invalid(NewMultipleChoiceTaskDTO invalidDto, String field) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/task/new/multiplechoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value(field))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    private static Stream<Arguments> provideInvalidMultipleChoiceTasks() {
        List<OptionDTO> notEnoughOptions = Arrays.asList(new OptionDTO(), new OptionDTO());
        List<OptionDTO> enoughOptions = Arrays.asList(new OptionDTO(), new OptionDTO(), new OptionDTO());
        List<OptionDTO> tooManyOptions = Arrays.asList(new OptionDTO(), new OptionDTO(), new OptionDTO(), new OptionDTO(), new OptionDTO(), new OptionDTO());

        return Stream.of(
            // Invalid courseId
            arguments(new NewMultipleChoiceTaskDTO(null, "valid statement", 1, enoughOptions), "courseId"),
            
            // Invalid order
            arguments(new NewMultipleChoiceTaskDTO(1L, "valid statement", null, enoughOptions), "order"),
            arguments(new NewMultipleChoiceTaskDTO(1L, "valid statement", -1, enoughOptions), "order"),
            
            // Invalid statement
            arguments(new NewMultipleChoiceTaskDTO(1L, null, 1, enoughOptions), "statement"),
            arguments(new NewMultipleChoiceTaskDTO(1L, "", 1, enoughOptions), "statement"),
            arguments(new NewMultipleChoiceTaskDTO(1L, "len", 1, enoughOptions), "statement"),
            
            // Invalid options
            arguments(new NewMultipleChoiceTaskDTO(1L, "valid statement", 1, notEnoughOptions), "options"),
            arguments(new NewMultipleChoiceTaskDTO(1L, "valid statement", 1, tooManyOptions), "options")
        );
    }

    @Test
    public void newOpenTextTask__should_return_not_found_when_course_does_not_exist() throws Exception {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO(1L, "valid statement", 1);
        when(taskUseCase.createOpenTextTask(any())).thenThrow(new EntityNotFoundException("exception"));
        
        mockMvc.perform(MockMvcRequestBuilders.post("/task/new/opentext")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }
    
    @Test
    public void newOpenTextTask__should_return_conflict_when_statement_is_duplicated() throws Exception {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO(1L, "valid statement", 1);
        when(taskUseCase.createOpenTextTask(any())).thenThrow(new ConflictException("exception"));
        
        mockMvc.perform(MockMvcRequestBuilders.post("/task/new/opentext")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    public void newOpenTextTask__should_return_unprocessable_entity_when_course_is_not_in_building_status() throws Exception {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO(1L, "valid statement", 1);
        when(taskUseCase.createOpenTextTask(any())).thenThrow(new InvalidStateException("exception"));
        
        mockMvc.perform(MockMvcRequestBuilders.post("/task/new/opentext")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }
    
    @Test
    public void newOpenTextTask__should_return_created_when_successful() throws Exception {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO(1L, "valid statement", 1);
        Course course = mock(Course.class);
        Task task = new Task("valid statement", 1, course, Type.OPEN_TEXT);
        task.setId(1L);
        
        when(taskUseCase.createOpenTextTask(any())).thenReturn(task);
        
        mockMvc.perform(MockMvcRequestBuilders.post("/task/new/opentext")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.statement").value("valid statement"))
                .andExpect(jsonPath("$.order").value(1));
    }

    @Test
    public void newSingleChoiceTask__should_return_not_found_when_course_does_not_exist() throws Exception {
        List<OptionDTO> options = Arrays.asList(new OptionDTO("option1", true), new OptionDTO("option2", false));
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO(1L, "valid statement", 1, options);
        when(taskUseCase.createSingleChoiceTask(any())).thenThrow(new EntityNotFoundException("exception"));
        
        mockMvc.perform(MockMvcRequestBuilders.post("/task/new/singlechoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }
    
    @Test
    public void newSingleChoiceTask__should_return_conflict_when_statement_is_duplicated() throws Exception {
        List<OptionDTO> options = Arrays.asList(new OptionDTO("option1", true), new OptionDTO("option2", false));
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO(1L, "valid statement", 1, options);
        when(taskUseCase.createSingleChoiceTask(any())).thenThrow(new ConflictException("exception"));
        
        mockMvc.perform(MockMvcRequestBuilders.post("/task/new/singlechoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    public void newSingleChoiceTask__should_return_unprocessable_entity_when_course_is_not_in_building_status() throws Exception {
        List<OptionDTO> options = Arrays.asList(new OptionDTO("option1", true), new OptionDTO("option2", false));
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO(1L, "valid statement", 1, options);
        when(taskUseCase.createSingleChoiceTask(any())).thenThrow(new InvalidStateException("exception"));
        
        mockMvc.perform(MockMvcRequestBuilders.post("/task/new/singlechoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }
    
    @Test
    public void newSingleChoiceTask__should_return_created_when_successful() throws Exception {
        List<OptionDTO> options = Arrays.asList(new OptionDTO("option1", true), new OptionDTO("option2", false));
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO(1L, "valid statement", 1, options);
        Course course = mock(Course.class);
        Task task = new Task("valid statement", 1, course, Type.SINGLE_CHOICE);
        task.setId(1L);
        
        when(taskUseCase.createSingleChoiceTask(any())).thenReturn(task);
        
        mockMvc.perform(MockMvcRequestBuilders.post("/task/new/singlechoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.statement").value("valid statement"))
                .andExpect(jsonPath("$.order").value(1));
    }

    @Test
    public void newMultipleChoiceTask__should_return_not_found_when_course_does_not_exist() throws Exception {
        List<OptionDTO> options = Arrays.asList(new OptionDTO("option1", true), new OptionDTO("option2", true), new OptionDTO("option3", false));
        NewMultipleChoiceTaskDTO dto = new NewMultipleChoiceTaskDTO(1L, "valid statement", 1, options);
        when(taskUseCase.createMultipleChoiceTask(any())).thenThrow(new EntityNotFoundException("exception"));
        
        mockMvc.perform(MockMvcRequestBuilders.post("/task/new/multiplechoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }
    
    @Test
    public void newMultipleChoiceTask__should_return_conflict_when_statement_is_duplicated() throws Exception {
        List<OptionDTO> options = Arrays.asList(new OptionDTO("option1", true), new OptionDTO("option2", true), new OptionDTO("option3", false));
        NewMultipleChoiceTaskDTO dto = new NewMultipleChoiceTaskDTO(1L, "valid statement", 1, options);
        when(taskUseCase.createMultipleChoiceTask(any())).thenThrow(new ConflictException("exception"));
        
        mockMvc.perform(MockMvcRequestBuilders.post("/task/new/multiplechoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    public void newMultipleChoiceTask__should_return_unprocessable_entity_when_course_is_not_in_building_status() throws Exception {
        List<OptionDTO> options = Arrays.asList(new OptionDTO("option1", true), new OptionDTO("option2", true), new OptionDTO("option3", false));
        NewMultipleChoiceTaskDTO dto = new NewMultipleChoiceTaskDTO(1L, "valid statement", 1, options);
        when(taskUseCase.createMultipleChoiceTask(any())).thenThrow(new InvalidStateException("exception"));
        
        mockMvc.perform(MockMvcRequestBuilders.post("/task/new/multiplechoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }
    
    @Test
    public void newMultipleChoiceTask__should_return_created_when_successful() throws Exception {
        List<OptionDTO> options = Arrays.asList(new OptionDTO("option1", true), new OptionDTO("option2", true), new OptionDTO("option3", false));
        NewMultipleChoiceTaskDTO dto = new NewMultipleChoiceTaskDTO(1L, "valid statement", 1, options);
        Course course = mock(Course.class);
        Task task = new Task("valid statement", 1, course, Type.MULTIPLE_CHOICE);
        task.setId(1L);
        
        when(taskUseCase.createMultipleChoiceTask(any())).thenReturn(task);
        
        mockMvc.perform(MockMvcRequestBuilders.post("/task/new/multiplechoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.statement").value("valid statement"))
                .andExpect(jsonPath("$.order").value(1));
    }
}
