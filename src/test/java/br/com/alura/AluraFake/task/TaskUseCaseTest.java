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
import br.com.alura.AluraFake.util.exceptions.InvalidStateException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TaskUseCaseTest {

    @Mock
    private TaskRepository taskRepository;

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
    public void createOpenTextTask_should_return_task_when_successful() {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO(1L, "valid statement", 1);
        Course course = mock(Course.class);
        when(course.getId()).thenReturn(1L);
        when(course.getStatus()).thenReturn(Status.BUILDING);
        when(courseRepository.findById(dto.getCourseId())).thenReturn(Optional.of(course));
        when(taskRepository.existsByStatementAndCourseId(dto.getStatement(), dto.getCourseId())).thenReturn(false);
        when(taskRepository.save(any(Task.class))).thenReturn(dto.toTask(course));
        
        Task result = taskUseCase.createOpenTextTask(dto);
        
        assertNotNull(result);
        assertEquals(dto.getStatement(), result.getStatement());
        assertEquals(dto.getCourseId(), result.getCourse().getId());
    }
}
