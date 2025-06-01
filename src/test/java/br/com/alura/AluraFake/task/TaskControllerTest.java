package br.com.alura.AluraFake.task;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import java.util.stream.Stream;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @ParameterizedTest
    @MethodSource("provideInvalidOpenTextTasks")
    public void newOpenTextTask__should_return_bad_request_when_invalid(NewOpenTextTaskDTO invalidDto) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/task/new/opentext")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> provideInvalidOpenTextTasks() {
        return Stream.of(
            // Invalid courseId
            arguments(new NewOpenTextTaskDTO(null, "valid statement", 1)),
            
            // Invalid order
            arguments(new NewOpenTextTaskDTO(1L, "valid statement", null)),
            arguments(new NewOpenTextTaskDTO(1L, "valid statement", -1)),
            
            // Invalid statement
            arguments(new NewOpenTextTaskDTO(1L, null, 1)),
            arguments(new NewOpenTextTaskDTO(1L, "", 1)),
            arguments(new NewOpenTextTaskDTO(1L, "len", 1))
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidSingleChoiceTasks")
    public void newSingleChoiceTask__should_return_bad_request_when_new_single_choice_task_dto_is_invalid(NewSingleChoiceTaskDTO invalidDto) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/task/new/singlechoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> provideInvalidSingleChoiceTasks() {
        return Stream.of(
            // Invalid courseId
            arguments(new NewSingleChoiceTaskDTO(null, "valid statement", 1, Arrays.asList(new OptionDTO(), new OptionDTO()))),
            
            // Invalid order
            arguments(new NewSingleChoiceTaskDTO(1L, "valid statement", null, Arrays.asList(new OptionDTO(), new OptionDTO()))),
            arguments(new NewSingleChoiceTaskDTO(1L, "valid statement", -1, Arrays.asList(new OptionDTO(), new OptionDTO()))),
            
            // Invalid statement
            arguments(new NewSingleChoiceTaskDTO(1L, null, 1, Arrays.asList(new OptionDTO(), new OptionDTO()))),
            arguments(new NewSingleChoiceTaskDTO(1L, "", 1, Arrays.asList(new OptionDTO(), new OptionDTO()))),
            arguments(new NewSingleChoiceTaskDTO(1L, "len", 1, Arrays.asList(new OptionDTO(), new OptionDTO()))),
            
            // Invalid options
            arguments(new NewSingleChoiceTaskDTO(1L, "valid statement", 1, Arrays.asList(new OptionDTO()))), // Only 1 option
            arguments(new NewSingleChoiceTaskDTO(1L, "valid statement", 1, Arrays.asList(new OptionDTO(), new OptionDTO(), new OptionDTO(), new OptionDTO(), new OptionDTO(), new OptionDTO()))) // 6 options
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidMultipleChoiceTasks")
    public void newMultipleChoiceTask__should_return_bad_request_when_new_multiple_choice_task_dto_is_invalid(NewMultipleChoiceTaskDTO invalidDto) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/task/new/multiplechoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> provideInvalidMultipleChoiceTasks() {
        return Stream.of(
            // Invalid courseId
            arguments(new NewMultipleChoiceTaskDTO(null, "valid statement", 1, Arrays.asList(new OptionDTO(), new OptionDTO(), new OptionDTO()))),
            
            // Invalid order
            arguments(new NewMultipleChoiceTaskDTO(1L, "valid statement", null, Arrays.asList(new OptionDTO(), new OptionDTO(), new OptionDTO()))),
            arguments(new NewMultipleChoiceTaskDTO(1L, "valid statement", -1, Arrays.asList(new OptionDTO(), new OptionDTO(), new OptionDTO()))),
            
            // Invalid statement
            arguments(new NewMultipleChoiceTaskDTO(1L, null, 1, Arrays.asList(new OptionDTO(), new OptionDTO(), new OptionDTO()))),
            arguments(new NewMultipleChoiceTaskDTO(1L, "", 1, Arrays.asList(new OptionDTO(), new OptionDTO(), new OptionDTO()))),
            arguments(new NewMultipleChoiceTaskDTO(1L, "len", 1, Arrays.asList(new OptionDTO(), new OptionDTO(), new OptionDTO()))),
            
            // Invalid options
            arguments(new NewMultipleChoiceTaskDTO(1L, "valid statement", 1, Arrays.asList(new OptionDTO(), new OptionDTO()))), // Only 2 options
            arguments(new NewMultipleChoiceTaskDTO(1L, "valid statement", 1, Arrays.asList(new OptionDTO(), new OptionDTO(), new OptionDTO(), new OptionDTO(), new OptionDTO(), new OptionDTO()))) // 6 options
        );
    }
}
