package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.course.dto.CourseListItemDTO;
import br.com.alura.AluraFake.course.dto.NewCourseDTO;
import br.com.alura.AluraFake.course.entity.Course;
import br.com.alura.AluraFake.course.entity.Status;
import br.com.alura.AluraFake.course.repository.CourseRepository;
import br.com.alura.AluraFake.course.usecase.CourseUseCaseImpl;
import br.com.alura.AluraFake.task.entity.Task;
import br.com.alura.AluraFake.task.repository.TaskRepository;
import br.com.alura.AluraFake.util.exceptions.EntityNotFoundException;
import br.com.alura.AluraFake.util.exceptions.InvalidStateException;
import br.com.alura.AluraFake.user.*;
import br.com.alura.AluraFake.user.entity.Role;
import br.com.alura.AluraFake.user.entity.User;
import br.com.alura.AluraFake.user.repository.UserRepository;
import br.com.alura.AluraFake.util.exceptions.InvalidArgumentException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseUseCaseTest {

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private CourseUseCaseImpl courseUseCase;

    @Test
    void createCourse_should_throw_when_instructor_email_not_found() {
        NewCourseDTO dto = new NewCourseDTO();
        dto.setTitle("Java");
        dto.setDescription("Curso de Java");
        dto.setEmailInstructor("notfound@alura.com.br");
        when(userRepository.findByEmail(dto.getEmailInstructor())).thenReturn(Optional.empty());
        assertThrows(InvalidArgumentException.class, () -> courseUseCase.createCourse(dto));
    }

    @Test
    void createCourse_should_throw_when_user_is_not_instructor() {
        NewCourseDTO dto = new NewCourseDTO();
        dto.setTitle("Java");
        dto.setDescription("Curso de Java");
        dto.setEmailInstructor("paulo@alura.com.br");
        User user = mock(User.class);
        when(user.isInstructor()).thenReturn(false);
        when(userRepository.findByEmail(dto.getEmailInstructor())).thenReturn(Optional.of(user));
        assertThrows(InvalidArgumentException.class, () -> courseUseCase.createCourse(dto));
    }

    @Test
    void createCourse_should_create_course_when_data_is_valid() {
        NewCourseDTO dto = new NewCourseDTO();
        dto.setTitle("Java");
        dto.setDescription("Curso de Java");
        dto.setEmailInstructor("paulo@alura.com.br");
        User user = mock(User.class);
        when(user.isInstructor()).thenReturn(true);
        when(userRepository.findByEmail(dto.getEmailInstructor())).thenReturn(Optional.of(user));
        Course course = courseUseCase.createCourse(dto);
        assertEquals("Java", course.getTitle());
        assertEquals("Curso de Java", course.getDescription());
        assertEquals(user, course.getInstructor());
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void getAllCourses_should_return_all_courses_as_dto_list() {
        User instructor = new User("Instructor Name", "instructor@email.com", Role.INSTRUCTOR, "password");
        Course course1 = new Course("Java Basics", "Learn Java", instructor);
        Course course2 = new Course("Spring Boot", "Spring Boot in depth", instructor);
        course1.setStatus(Status.PUBLISHED);
        course2.setStatus(Status.BUILDING);

        when(courseRepository.findAll()).thenReturn(Arrays.asList(course1, course2));

        List<CourseListItemDTO> result = courseUseCase.getAllCourses();

        assertEquals(2, result.size());

        assertEquals(course1.getTitle(), result.get(0).getTitle());
        assertEquals(course1.getDescription(), result.get(0).getDescription());
        assertEquals(course1.getStatus(), result.get(0).getStatus());

        assertEquals(course2.getTitle(), result.get(1).getTitle());
        assertEquals(course2.getDescription(), result.get(1).getDescription());
        assertEquals(course2.getStatus(), result.get(1).getStatus());
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    void publishCourse_should_publish_when_all_rules_satisfied() {
        Long courseId = 1L;
        User instructor = mock(User.class);
        when(instructor.isInstructor()).thenReturn(true);
        Course course = new Course("Test Course", "desc", instructor);
        course.setStatus(Status.BUILDING);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        Task t1 = mock(Task.class);
        when(t1.getType()).thenReturn(br.com.alura.AluraFake.task.entity.Type.OPEN_TEXT);
        when(t1.getOrder()).thenReturn(1);
        Task t2 = mock(Task.class);
        when(t2.getType()).thenReturn(br.com.alura.AluraFake.task.entity.Type.SINGLE_CHOICE);
        when(t2.getOrder()).thenReturn(2);
        Task t3 = mock(Task.class);
        when(t3.getType()).thenReturn(br.com.alura.AluraFake.task.entity.Type.MULTIPLE_CHOICE);
        when(t3.getOrder()).thenReturn(3);
        when(taskRepository.findByCourseIdOrderByOrderAsc(courseId)).thenReturn(List.of(t1, t2, t3));

        courseUseCase.publishCourse(courseId);
        assertEquals(Status.PUBLISHED, course.getStatus());
        assertNotNull(course.getPublishedAt());
        verify(courseRepository).save(course);
    }

    @Test
    void publishCourse_should_throw_when_course_not_found() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> courseUseCase.publishCourse(99L));
    }

    @Test
    void publishCourse_should_throw_when_status_not_building() {
        Long courseId = 2L;
        User instructor = mock(User.class);
        when(instructor.isInstructor()).thenReturn(true);
        Course course = new Course("Test", "desc", instructor);
        course.setStatus(Status.PUBLISHED);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        assertThrows(InvalidStateException.class, () -> courseUseCase.publishCourse(courseId));
    }

    @Test
    void publishCourse_should_throw_when_no_tasks() {
        Long courseId = 3L;
        User instructor = mock(User.class);
        when(instructor.isInstructor()).thenReturn(true);
        Course course = new Course("Test", "desc", instructor);
        course.setStatus(Status.BUILDING);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(taskRepository.findByCourseIdOrderByOrderAsc(courseId)).thenReturn(List.of());
        assertThrows(InvalidArgumentException.class, () -> courseUseCase.publishCourse(courseId));
    }

    @Test
    void publishCourse_should_throw_when_missing_task_type() {
        Long courseId = 4L;
        User instructor = mock(User.class);
        when(instructor.isInstructor()).thenReturn(true);
        Course course = new Course("Test", "desc", instructor);
        course.setStatus(Status.BUILDING);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        Task t1 = mock(Task.class);
        when(t1.getType()).thenReturn(br.com.alura.AluraFake.task.entity.Type.OPEN_TEXT);
        Task t2 = mock(Task.class);
        when(t2.getType()).thenReturn(br.com.alura.AluraFake.task.entity.Type.SINGLE_CHOICE);
        // Missing MULTIPLE_CHOICE
        when(taskRepository.findByCourseIdOrderByOrderAsc(courseId)).thenReturn(List.of(t1, t2));
        assertThrows(InvalidArgumentException.class, () -> courseUseCase.publishCourse(courseId));
    }

    @Test
    void publishCourse_should_throw_when_task_order_not_continuous() {
        Long courseId = 5L;
        User instructor = mock(User.class);
        when(instructor.isInstructor()).thenReturn(true);
        Course course = new Course("Test", "desc", instructor);
        course.setStatus(Status.BUILDING);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        Task t1 = mock(Task.class);
        when(t1.getType()).thenReturn(br.com.alura.AluraFake.task.entity.Type.OPEN_TEXT);
        when(t1.getOrder()).thenReturn(1);
        Task t2 = mock(Task.class);
        when(t2.getType()).thenReturn(br.com.alura.AluraFake.task.entity.Type.SINGLE_CHOICE);
        when(t2.getOrder()).thenReturn(3); // should be 2
        Task t3 = mock(Task.class);
        when(t3.getType()).thenReturn(br.com.alura.AluraFake.task.entity.Type.MULTIPLE_CHOICE);
        when(taskRepository.findByCourseIdOrderByOrderAsc(courseId)).thenReturn(List.of(t1, t2, t3));
        assertThrows(InvalidArgumentException.class, () -> courseUseCase.publishCourse(courseId));
    }

}
