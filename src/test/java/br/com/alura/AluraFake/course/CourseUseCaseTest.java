package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.TaskRepository;
import br.com.alura.AluraFake.user.*;
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
    private CourseUseCase courseUseCase;

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
}
