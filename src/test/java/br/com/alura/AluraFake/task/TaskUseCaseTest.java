package br.com.alura.AluraFake.task;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.util.exceptions.ConflictException;
import br.com.alura.AluraFake.util.exceptions.EntityNotFoundException;
import br.com.alura.AluraFake.util.exceptions.InvalidArgumentException;
import br.com.alura.AluraFake.util.exceptions.InvalidStateException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Arrays;
import br.com.alura.AluraFake.task.OptionDTO;

@ExtendWith(MockitoExtension.class)
public class TaskUseCaseTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskOptionRepository taskOptionRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private TaskUseCase taskUseCase;

    @Test
    public void createOpenTextTask_should_throw_when_course_does_not_exist() {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO(1L, "valid statement", 1);
        when(courseRepository.findById(dto.getCourseId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            taskUseCase.createOpenTextTask(dto);
        });
    }

    @Test
    public void createOpenTextTask_should_throw_when_statement_exists() {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO(1L, "valid statement", 1);
        Course course = mock(Course.class);
        when(course.getId()).thenReturn(1L);
        when(course.getStatus()).thenReturn(Status.BUILDING);
        when(courseRepository.findById(dto.getCourseId())).thenReturn(Optional.of(course));
        when(taskRepository.existsByStatementAndCourseId(dto.getStatement(), dto.getCourseId())).thenReturn(true);

        assertThrows(ConflictException.class, () -> {
            taskUseCase.createOpenTextTask(dto);
        });
    }

    @Test
    public void createOpenTextTask_should_throw_when_course_is_not_in_building_status() {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO(1L, "valid statement", 1);
        Course course = mock(Course.class);
        when(course.getStatus()).thenReturn(Status.PUBLISHED);
        when(courseRepository.findById(dto.getCourseId())).thenReturn(Optional.of(course));

        assertThrows(InvalidStateException.class, () -> {
            taskUseCase.createOpenTextTask(dto);
        });
    }

    @Test
    public void createOpenTextTask_should_throw_when_there_are_gaps_in_task_order() {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO(1L, "valid statement", 2);
        Course course = mock(Course.class);
        when(course.getId()).thenReturn(1L);
        when(course.getStatus()).thenReturn(Status.BUILDING);
        when(courseRepository.findById(dto.getCourseId())).thenReturn(Optional.of(course));
        when(taskRepository.existsByStatementAndCourseId(dto.getStatement(), dto.getCourseId())).thenReturn(false);
        when(taskRepository.findByCourseIdOrderByOrderAsc(dto.getCourseId())).thenReturn(List.of());

        assertThrows(InvalidArgumentException.class, () -> {
            taskUseCase.createOpenTextTask(dto);
        });
    }

    @Test
    public void createOpenTextTask_should_shift_task_order() {
        List<Task> tasks = List.of(
                new Task("valid statement1", 1, null, Type.OPEN_TEXT),
                new Task("valid statement2", 2, null, Type.OPEN_TEXT),
                new Task("valid statement3", 3, null, Type.OPEN_TEXT));
        List<Task> shiftedTasks = List.of(
                new Task("valid statement2", 3, null, Type.OPEN_TEXT),
                new Task("valid statement3", 4, null, Type.OPEN_TEXT));
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO(1L, "valid statement", 2);
        Course course = mock(Course.class);
        when(course.getId()).thenReturn(1L);
        when(course.getStatus()).thenReturn(Status.BUILDING);
        when(courseRepository.findById(dto.getCourseId())).thenReturn(Optional.of(course));
        when(taskRepository.existsByStatementAndCourseId(dto.getStatement(), dto.getCourseId())).thenReturn(false);
        when(taskRepository.findByCourseIdOrderByOrderAsc(dto.getCourseId())).thenReturn(tasks);
        when(taskRepository.saveAll(argThat(list -> {
            List<Task> taskList = new ArrayList<>((Collection<Task>) list);
            return taskList.size() == shiftedTasks.size() &&
                    taskList.get(0).getOrder() == shiftedTasks.get(0).getOrder() &&
                    taskList.get(1).getOrder() == shiftedTasks.get(1).getOrder();
        }))).thenReturn(shiftedTasks);
        when(taskRepository.save(any(Task.class))).thenReturn(dto.toTask(course));

        Task result = taskUseCase.createOpenTextTask(dto);

        assertNotNull(result);
        assertEquals(dto.getStatement(), result.getStatement());
        assertEquals(dto.getCourseId(), result.getCourse().getId());
    }

    @Test
    public void createOpenTextTask_should_return_task_when_successful() {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO(1L, "valid statement", 1);
        Course course = mock(Course.class);
        when(course.getId()).thenReturn(1L);
        when(course.getStatus()).thenReturn(Status.BUILDING);
        when(courseRepository.findById(dto.getCourseId())).thenReturn(Optional.of(course));
        when(taskRepository.existsByStatementAndCourseId(dto.getStatement(), dto.getCourseId())).thenReturn(false);
        when(taskRepository.findByCourseIdOrderByOrderAsc(dto.getCourseId())).thenReturn(List.of());
        when(taskRepository.save(any(Task.class))).thenReturn(dto.toTask(course));

        Task result = taskUseCase.createOpenTextTask(dto);

        assertNotNull(result);
        assertEquals(dto.getStatement(), result.getStatement());
        assertEquals(dto.getCourseId(), result.getCourse().getId());
    }

    @Test
    public void createSingleChoiceTask_should_throw_when_course_does_not_exist() {
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO(1L, "valid statement", 1, Arrays.asList(
                new OptionDTO("Option 1", true), new OptionDTO("Option 2", false)));
        when(courseRepository.findById(dto.getCourseId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            taskUseCase.createSingleChoiceTask(dto);
        });
    }

    @Test
    public void createSingleChoiceTask_should_throw_when_statement_exists() {
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO(1L, "valid statement", 1, Arrays.asList(
                new OptionDTO("Option 1", true), new OptionDTO("Option 2", false)));
        Course course = mock(Course.class);
        when(course.getId()).thenReturn(1L);
        when(course.getStatus()).thenReturn(Status.BUILDING);
        when(courseRepository.findById(dto.getCourseId())).thenReturn(Optional.of(course));
        when(taskRepository.existsByStatementAndCourseId(dto.getStatement(), dto.getCourseId())).thenReturn(true);

        assertThrows(ConflictException.class, () -> {
            taskUseCase.createSingleChoiceTask(dto);
        });
    }

    @Test
    public void createSingleChoiceTask_should_throw_when_course_is_not_in_building_status() {
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO(1L, "valid statement", 1, Arrays.asList(
                new OptionDTO("Option 1", true), new OptionDTO("Option 2", false)));
        Course course = mock(Course.class);
        when(course.getStatus()).thenReturn(Status.PUBLISHED);
        when(courseRepository.findById(dto.getCourseId())).thenReturn(Optional.of(course));

        assertThrows(InvalidStateException.class, () -> {
            taskUseCase.createSingleChoiceTask(dto);
        });
    }

    @Test
    public void createSingleChoiceTask_should_throw_when_there_are_gaps_in_task_order() {
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO(1L, "valid statement", 2, Arrays.asList(
                new OptionDTO("Option 1", true), new OptionDTO("Option 2", false)));
        Course course = mock(Course.class);
        when(course.getId()).thenReturn(1L);
        when(course.getStatus()).thenReturn(Status.BUILDING);
        when(courseRepository.findById(dto.getCourseId())).thenReturn(Optional.of(course));
        when(taskRepository.existsByStatementAndCourseId(dto.getStatement(), dto.getCourseId())).thenReturn(false);
        when(taskRepository.findByCourseIdOrderByOrderAsc(dto.getCourseId())).thenReturn(List.of());

        assertThrows(InvalidArgumentException.class, () -> {
            taskUseCase.createSingleChoiceTask(dto);
        });
    }

    @Test
    public void createSingleChoiceTask_should_shift_task_order() {
        List<Task> tasks = List.of(
                new Task("valid statement1", 1, null, Type.OPEN_TEXT),
                new Task("valid statement2", 2, null, Type.OPEN_TEXT),
                new Task("valid statement3", 3, null, Type.OPEN_TEXT));
        List<Task> shiftedTasks = List.of(
                new Task("valid statement2", 3, null, Type.OPEN_TEXT),
                new Task("valid statement3", 4, null, Type.OPEN_TEXT));
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO(1L, "valid statement", 2, Arrays.asList(
                new OptionDTO("Option 1", true), new OptionDTO("Option 2", false)));
        Course course = mock(Course.class);
        when(course.getId()).thenReturn(1L);
        when(course.getStatus()).thenReturn(Status.BUILDING);
        when(courseRepository.findById(dto.getCourseId())).thenReturn(Optional.of(course));
        when(taskRepository.existsByStatementAndCourseId(dto.getStatement(), dto.getCourseId())).thenReturn(false);
        when(taskRepository.findByCourseIdOrderByOrderAsc(dto.getCourseId())).thenReturn(tasks);
        when(taskRepository.saveAll(argThat(list -> {
            List<Task> taskList = new ArrayList<>((Collection<Task>) list);
            return taskList.size() == shiftedTasks.size() &&
                    taskList.get(0).getOrder() == shiftedTasks.get(0).getOrder() &&
                    taskList.get(1).getOrder() == shiftedTasks.get(1).getOrder();
        }))).thenReturn(shiftedTasks);
        when(taskRepository.save(any(Task.class))).thenReturn(dto.toTask(course));

        Task result = taskUseCase.createSingleChoiceTask(dto);

        assertNotNull(result);
        assertEquals(dto.getStatement(), result.getStatement());
        assertEquals(dto.getCourseId(), result.getCourse().getId());
    }

    @Test
    public void createSingleChoiceTask_should_return_task_when_successful() {
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO(1L, "valid statement", 1, Arrays.asList(
                new OptionDTO("Option 1", true), new OptionDTO("Option 2", false)));
        Course course = mock(Course.class);
        when(course.getId()).thenReturn(1L);
        when(course.getStatus()).thenReturn(Status.BUILDING);
        when(courseRepository.findById(dto.getCourseId())).thenReturn(Optional.of(course));
        when(taskRepository.existsByStatementAndCourseId(dto.getStatement(), dto.getCourseId())).thenReturn(false);
        when(taskRepository.findByCourseIdOrderByOrderAsc(dto.getCourseId())).thenReturn(List.of());
        when(taskRepository.save(any(Task.class))).thenReturn(dto.toTask(course));

        Task result = taskUseCase.createSingleChoiceTask(dto);

        assertNotNull(result);
        assertEquals(dto.getStatement(), result.getStatement());
        assertEquals(dto.getCourseId(), result.getCourse().getId());
    }

    @Test
    public void createSingleChoiceTask_should_throw_when_no_correct_option_provided() {
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO(1L, "valid statement", 1, Arrays.asList(
                new OptionDTO("Option 1", false), new OptionDTO("Option 2", false)));

        assertThrows(InvalidArgumentException.class, () -> {
            taskUseCase.createSingleChoiceTask(dto);
        });
    }

    @Test
    public void createSingleChoiceTask_should_throw_when_multiple_correct_options_provided() {
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO(1L, "valid statement", 1, Arrays.asList(
                new OptionDTO("Option 1", true), new OptionDTO("Option 2", true)));

        assertThrows(InvalidArgumentException.class, () -> {
            taskUseCase.createSingleChoiceTask(dto);
        });
    }

    @Test
    public void createSingleChoiceTask_should_throw_when_duplicated_options_are_provided() {
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO(1L, "valid statement", 1, Arrays.asList(
                new OptionDTO("Option 1", true), new OptionDTO("Option 1", false)));

        assertThrows(InvalidArgumentException.class, () -> {
            taskUseCase.createSingleChoiceTask(dto);
        });
    }

    @Test
    public void createSingleChoiceTask_should_throw_when_option_equals_statement() {
        String statement = "Statement matches option";
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO(1L, statement, 1, Arrays.asList(
                new OptionDTO(statement, true), new OptionDTO("Other option", false)));

        assertThrows(InvalidArgumentException.class, () -> {
            taskUseCase.createSingleChoiceTask(dto);
        });
    }

    @Test
    public void createMultipleChoiceTask_should_throw_when_course_does_not_exist() {
        NewMultipleChoiceTaskDTO dto = new NewMultipleChoiceTaskDTO(1L, "valid statement", 1, Arrays.asList(
                new OptionDTO("Option 1", true), new OptionDTO("Option 2", true), new OptionDTO("Option 3", false)));
        when(courseRepository.findById(dto.getCourseId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            taskUseCase.createMultipleChoiceTask(dto);
        });
    }

    @Test
    public void createMultipleChoiceTask_should_throw_when_statement_exists() {
        NewMultipleChoiceTaskDTO dto = new NewMultipleChoiceTaskDTO(1L, "valid statement", 1, Arrays.asList(
                new OptionDTO("Option 1", true), new OptionDTO("Option 2", true), new OptionDTO("Option 3", false)));
        Course course = mock(Course.class);
        when(course.getId()).thenReturn(1L);
        when(course.getStatus()).thenReturn(Status.BUILDING);
        when(courseRepository.findById(dto.getCourseId())).thenReturn(Optional.of(course));
        when(taskRepository.existsByStatementAndCourseId(dto.getStatement(), dto.getCourseId())).thenReturn(true);

        assertThrows(ConflictException.class, () -> {
            taskUseCase.createMultipleChoiceTask(dto);
        });
    }

    @Test
    public void createMultipleChoiceTask_should_throw_when_course_is_not_in_building_status() {
        NewMultipleChoiceTaskDTO dto = new NewMultipleChoiceTaskDTO(1L, "valid statement", 1, Arrays.asList(
                new OptionDTO("Option 1", true), new OptionDTO("Option 2", true), new OptionDTO("Option 3", false)));
        Course course = mock(Course.class);
        when(course.getStatus()).thenReturn(Status.PUBLISHED);
        when(courseRepository.findById(dto.getCourseId())).thenReturn(Optional.of(course));

        assertThrows(InvalidStateException.class, () -> {
            taskUseCase.createMultipleChoiceTask(dto);
        });
    }

    @Test
    public void createMultipleChoiceTask_should_throw_when_there_are_gaps_in_task_order() {
        NewMultipleChoiceTaskDTO dto = new NewMultipleChoiceTaskDTO(1L, "valid statement", 2, Arrays.asList(
                new OptionDTO("Option 1", true), new OptionDTO("Option 2", true), new OptionDTO("Option 3", false)));
        Course course = mock(Course.class);
        when(course.getId()).thenReturn(1L);
        when(course.getStatus()).thenReturn(Status.BUILDING);
        when(courseRepository.findById(dto.getCourseId())).thenReturn(Optional.of(course));
        when(taskRepository.existsByStatementAndCourseId(dto.getStatement(), dto.getCourseId())).thenReturn(false);
        when(taskRepository.findByCourseIdOrderByOrderAsc(dto.getCourseId())).thenReturn(List.of());

        assertThrows(InvalidArgumentException.class, () -> {
            taskUseCase.createMultipleChoiceTask(dto);
        });
    }

    @Test
    public void createMultipleChoiceTask_should_shift_task_order() {
        List<Task> tasks = List.of(
                new Task("valid statement1", 1, null, Type.OPEN_TEXT),
                new Task("valid statement2", 2, null, Type.OPEN_TEXT),
                new Task("valid statement3", 3, null, Type.OPEN_TEXT));
        List<Task> shiftedTasks = List.of(
                new Task("valid statement2", 3, null, Type.OPEN_TEXT),
                new Task("valid statement3", 4, null, Type.OPEN_TEXT));
        NewMultipleChoiceTaskDTO dto = new NewMultipleChoiceTaskDTO(1L, "valid statement", 2, Arrays.asList(
                new OptionDTO("Option 1", true), new OptionDTO("Option 2", true), new OptionDTO("Option 3", false)));
        Course course = mock(Course.class);
        when(course.getId()).thenReturn(1L);
        when(course.getStatus()).thenReturn(Status.BUILDING);
        when(courseRepository.findById(dto.getCourseId())).thenReturn(Optional.of(course));
        when(taskRepository.existsByStatementAndCourseId(dto.getStatement(), dto.getCourseId())).thenReturn(false);
        when(taskRepository.findByCourseIdOrderByOrderAsc(dto.getCourseId())).thenReturn(tasks);
        when(taskRepository.saveAll(argThat(list -> {
            List<Task> taskList = new ArrayList<>((Collection<Task>) list);
            return taskList.size() == shiftedTasks.size() &&
                    taskList.get(0).getOrder() == shiftedTasks.get(0).getOrder() &&
                    taskList.get(1).getOrder() == shiftedTasks.get(1).getOrder();
        }))).thenReturn(shiftedTasks);
        when(taskRepository.save(any(Task.class))).thenReturn(dto.toTask(course));

        Task result = taskUseCase.createMultipleChoiceTask(dto);

        assertNotNull(result);
        assertEquals(dto.getStatement(), result.getStatement());
        assertEquals(dto.getCourseId(), result.getCourse().getId());
    }

    @Test
    public void createMultipleChoiceTask_should_return_task_when_successful() {
        NewMultipleChoiceTaskDTO dto = new NewMultipleChoiceTaskDTO(1L, "valid statement", 1, Arrays.asList(
                new OptionDTO("Option 1", true), new OptionDTO("Option 2", true), new OptionDTO("Option 3", false)));
        Course course = mock(Course.class);
        when(course.getId()).thenReturn(1L);
        when(course.getStatus()).thenReturn(Status.BUILDING);
        when(courseRepository.findById(dto.getCourseId())).thenReturn(Optional.of(course));
        when(taskRepository.existsByStatementAndCourseId(dto.getStatement(), dto.getCourseId())).thenReturn(false);
        when(taskRepository.findByCourseIdOrderByOrderAsc(dto.getCourseId())).thenReturn(List.of());
        when(taskRepository.save(any(Task.class))).thenReturn(dto.toTask(course));

        Task result = taskUseCase.createMultipleChoiceTask(dto);

        assertNotNull(result);
        assertEquals(dto.getStatement(), result.getStatement());
        assertEquals(dto.getCourseId(), result.getCourse().getId());
    }

    @Test
    public void createMultipleChoiceTask_should_throw_when_less_than_two_correct_options_provided() {
        NewMultipleChoiceTaskDTO dto = new NewMultipleChoiceTaskDTO(1L, "valid statement", 1, Arrays.asList(
                new OptionDTO("Option 1", true), new OptionDTO("Option 2", false), new OptionDTO("Option 3", false)));

        assertThrows(InvalidArgumentException.class, () -> {
            taskUseCase.createMultipleChoiceTask(dto);
        });
    }

    @Test
    public void createMultipleChoiceTask_should_throw_when_duplicated_options_are_provided() {
        NewMultipleChoiceTaskDTO dto = new NewMultipleChoiceTaskDTO(1L, "valid statement", 1, Arrays.asList(
                new OptionDTO("Option 1", true), new OptionDTO("Option 1", true), new OptionDTO("Option 3", false)));

        assertThrows(InvalidArgumentException.class, () -> {
            taskUseCase.createMultipleChoiceTask(dto);
        });
    }

    @Test
    public void createMultipleChoiceTask_should_throw_when_no_incorrect_option_provided() {
        NewMultipleChoiceTaskDTO dto = new NewMultipleChoiceTaskDTO(1L, "valid statement", 1, Arrays.asList(
                new OptionDTO("Option 1", true), new OptionDTO("Option 2", true), new OptionDTO("Option 3", true)));

        assertThrows(InvalidArgumentException.class, () -> {
            taskUseCase.createMultipleChoiceTask(dto);
        });
    }

    @Test
    public void createMultipleChoiceTask_should_throw_when_option_equals_statement() {
        String statement = "Statement matches option";
        NewMultipleChoiceTaskDTO dto = new NewMultipleChoiceTaskDTO(1L, statement, 1, Arrays.asList(
                new OptionDTO(statement, true), new OptionDTO("Other option", true), new OptionDTO("Another", false)));

        assertThrows(InvalidArgumentException.class, () -> {
            taskUseCase.createMultipleChoiceTask(dto);
        });
    }
}
