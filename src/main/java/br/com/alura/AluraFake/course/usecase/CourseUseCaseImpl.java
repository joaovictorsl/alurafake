package br.com.alura.AluraFake.course.usecase;

import br.com.alura.AluraFake.course.dto.CourseListItemDTO;
import br.com.alura.AluraFake.course.dto.NewCourseDTO;
import br.com.alura.AluraFake.course.entity.Course;
import br.com.alura.AluraFake.course.entity.Status;
import br.com.alura.AluraFake.course.repository.CourseRepository;
import br.com.alura.AluraFake.task.repository.TaskRepository;
import br.com.alura.AluraFake.user.entity.User;
import br.com.alura.AluraFake.user.repository.UserRepository;

import java.util.Optional;
import br.com.alura.AluraFake.util.exceptions.EntityNotFoundException;
import br.com.alura.AluraFake.util.exceptions.InvalidArgumentException;
import br.com.alura.AluraFake.util.exceptions.InvalidStateException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CourseUseCaseImpl implements CourseUseCase {
    private final CourseRepository courseRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public CourseUseCaseImpl(CourseRepository courseRepository, TaskRepository taskRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void publishCourse(Long id) {
        var possibleCourse = courseRepository.findById(id);
        if (possibleCourse.isEmpty()) {
            throw new EntityNotFoundException("Curso não encontrado");
        }
        Course course = possibleCourse.get();
        if (course.getStatus() != Status.BUILDING) {
            throw new InvalidStateException("Curso não está em construção");
        }
        var tasks = taskRepository.findByCourseIdOrderByOrderAsc(id);
        if (tasks.isEmpty()) {
            throw new InvalidArgumentException("O curso não possui atividades");
        }
        boolean hasOpenText = tasks.stream().anyMatch(t -> t.getType().name().equals("OPEN_TEXT"));
        boolean hasSingleChoice = tasks.stream().anyMatch(t -> t.getType().name().equals("SINGLE_CHOICE"));
        boolean hasMultipleChoice = tasks.stream().anyMatch(t -> t.getType().name().equals("MULTIPLE_CHOICE"));
        if (!hasOpenText || !hasSingleChoice || !hasMultipleChoice) {
            throw new InvalidArgumentException("O curso deve conter pelo menos uma atividade de cada tipo");
        }
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getOrder() != i + 1) {
                throw new InvalidArgumentException("As atividades devem ter ordem contínua começando de 1");
            }
        }
        course.setStatus(Status.PUBLISHED);
        course.setPublishedAt(LocalDateTime.now());
        courseRepository.save(course);
    }

    @Transactional
    public Course createCourse(NewCourseDTO newCourse) {
        Optional<User> possibleAuthor = this.userRepository
                .findByEmail(newCourse.getEmailInstructor())
                .filter(User::isInstructor);
        if (possibleAuthor.isEmpty()) {
            throw new InvalidArgumentException("Usuário não é um instrutor");
        }
        Course course = new Course(newCourse.getTitle(), newCourse.getDescription(), possibleAuthor.get());
        courseRepository.save(course);
        return course;
    }

    public java.util.List<CourseListItemDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(CourseListItemDTO::new)
                .toList();
    }
}
