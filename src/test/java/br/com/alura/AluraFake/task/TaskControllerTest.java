package br.com.alura.AluraFake.task;

import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import java.util.stream.Stream;
import java.util.Arrays;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;

import org.springframework.http.MediaType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskRepository taskRepository;

    @MockBean
    private CourseRepository courseRepository;

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
    public void newOpenTextTask__should_return_bad_request_when_course_does_not_exist() throws Exception {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO(1L, "valid statement", 1);
        when(courseRepository.findById(dto.getCourseId())).thenReturn(Optional.empty());
        
        mockMvc.perform(MockMvcRequestBuilders.post("/task/new/opentext")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("courseId"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }
    
    @Test
    public void newOpenTextTask__should_return_conflict_when_statement_exists() throws Exception {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO(1L, "valid statement", 1);
        Course course = mock(Course.class);
        when(course.getId()).thenReturn(1L);
        when(course.getStatus()).thenReturn(Status.BUILDING);
        when(courseRepository.findById(dto.getCourseId())).thenReturn(Optional.of(course));
        when(taskRepository.existsByStatementAndCourseId(dto.getStatement(), dto.getCourseId())).thenReturn(true);
        
        mockMvc.perform(MockMvcRequestBuilders.post("/task/new/opentext")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.field").value("statement"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    public void newOpenTextTask__should_return_unprocessable_entity_when_course_is_not_in_building_status() throws Exception {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO(1L, "valid statement", 1);
        Course course = mock(Course.class);
        when(course.getId()).thenReturn(1L);
        when(course.getStatus()).thenReturn(Status.PUBLISHED);
        when(courseRepository.findById(dto.getCourseId())).thenReturn(Optional.of(course));
        
        mockMvc.perform(MockMvcRequestBuilders.post("/task/new/opentext")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.field").value("courseId"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }
    
    @Test
    public void newOpenTextTask__should_return_created_when_successful() throws Exception {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO(1L, "valid statement", 1);
        Course course = mock(Course.class);
        when(course.getId()).thenReturn(1L);
        when(course.getStatus()).thenReturn(Status.BUILDING);
        when(courseRepository.findById(dto.getCourseId())).thenReturn(Optional.of(course));
        when(taskRepository.existsByStatementAndCourseId(dto.getStatement(), dto.getCourseId())).thenReturn(false);
        
        mockMvc.perform(MockMvcRequestBuilders.post("/task/new/opentext")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }
}
