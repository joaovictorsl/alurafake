package br.com.alura.AluraFake.task;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.util.exceptions.ConflictException;
import br.com.alura.AluraFake.util.exceptions.EntityNotFoundException;
import br.com.alura.AluraFake.util.exceptions.InvalidArgumentException;
import br.com.alura.AluraFake.util.exceptions.InvalidStateException;

@Service
public class TaskUseCase {

    private final TaskRepository taskRepository;
    private final CourseRepository courseRepository;

    public TaskUseCase(TaskRepository taskRepository, CourseRepository courseRepository) {
        this.taskRepository = taskRepository;
        this.courseRepository = courseRepository;
    }

    private void checkIfCourseCanReceiveTask(Optional<Course> course, String statement) throws RuntimeException {
        if (course.isEmpty()) {
            throw new EntityNotFoundException("Curso não encontrado");
        } else if (course.get().getStatus() != Status.BUILDING) {
            throw new InvalidStateException("Curso não está em construção");
        }

        if (taskRepository.existsByStatementAndCourseId(statement, course.get().getId())) {
            throw new ConflictException("Questão já existe no curso");
        }
    }

    private void checkIfOptionsAreValid(List<OptionDTO> options, String statement) throws RuntimeException {
        Set<String> optionTexts = new HashSet<>();
        for (OptionDTO option : options) {

            if (option.getOption().equals(statement)) {
                throw new InvalidArgumentException(String.format("Opção não pode ser igual ao enunciado: \"%s\"", option.getOption()));
            }

            if (optionTexts.contains(option.getOption())) {
                throw new InvalidArgumentException(String.format("Opções duplicadas: \"%s\"", option.getOption()));
            }
            optionTexts.add(option.getOption());
        }
    }

    @Transactional
    public Task createOpenTextTask(NewOpenTextTaskDTO newOpenTextTaskDTO) throws RuntimeException {
        Optional<Course> course = courseRepository.findById(newOpenTextTaskDTO.getCourseId());
        checkIfCourseCanReceiveTask(course, newOpenTextTaskDTO.getStatement());
        return taskRepository.save(newOpenTextTaskDTO.toTask(course.get()));
    }

    @Transactional
    public Task createSingleChoiceTask(NewSingleChoiceTaskDTO newSingleChoiceTaskDTO) throws RuntimeException {
        checkIfOptionsAreValid(newSingleChoiceTaskDTO.getOptions(), newSingleChoiceTaskDTO.getStatement());

        long correctOptionsCount = newSingleChoiceTaskDTO.getOptions().stream().filter(option -> option.getIsCorrect()).count();
        if (correctOptionsCount != 1) {
            throw new InvalidArgumentException("Exatamente uma alternativa deve ser correta");
        }

        Optional<Course> course = courseRepository.findById(newSingleChoiceTaskDTO.getCourseId());
        checkIfCourseCanReceiveTask(course, newSingleChoiceTaskDTO.getStatement());

        return taskRepository.save(newSingleChoiceTaskDTO.toTask(course.get()));
    }

    @Transactional
    public Task createMultipleChoiceTask(NewMultipleChoiceTaskDTO newMultipleChoiceTaskDTO) throws RuntimeException {
        checkIfOptionsAreValid(newMultipleChoiceTaskDTO.getOptions(), newMultipleChoiceTaskDTO.getStatement());

        long correctOptionsCount = newMultipleChoiceTaskDTO.getOptions().stream().filter(option -> option.getIsCorrect()).count();
        long incorrectOptionsCount = newMultipleChoiceTaskDTO.getOptions().size() - correctOptionsCount;
        if (correctOptionsCount < 2) {
            throw new InvalidArgumentException("Pelo menos duas alternativas devem ser corretas");
        } else if (incorrectOptionsCount < 1) {
            throw new InvalidArgumentException("Pelo menos uma alternativa deve ser incorreta");
        }

        Optional<Course> course = courseRepository.findById(newMultipleChoiceTaskDTO.getCourseId());
        checkIfCourseCanReceiveTask(course, newMultipleChoiceTaskDTO.getStatement());

        return taskRepository.save(newMultipleChoiceTaskDTO.toTask(course.get()));
    }
}
