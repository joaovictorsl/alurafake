package br.com.alura.AluraFake.task.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import br.com.alura.AluraFake.task.dto.NewMultipleChoiceTaskDTO;
import br.com.alura.AluraFake.task.dto.NewOpenTextTaskDTO;
import br.com.alura.AluraFake.task.dto.NewSingleChoiceTaskDTO;
import br.com.alura.AluraFake.task.entity.Task;
import br.com.alura.AluraFake.task.usecase.TaskUseCase;
import jakarta.validation.Valid;

@RestController
public class TaskController {

    private final TaskUseCase taskUseCase;

    @Autowired
    public TaskController(TaskUseCase taskUseCase) {
        this.taskUseCase = taskUseCase;
    }

    @Transactional
    @PostMapping("/task/new/opentext")
    public ResponseEntity<Task> newOpenTextExercise(@Valid @RequestBody NewOpenTextTaskDTO newOpenTextTaskDTO) {
        Task task = taskUseCase.createOpenTextTask(newOpenTextTaskDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PostMapping("/task/new/singlechoice")
    public ResponseEntity<Task> newSingleChoice(@Valid @RequestBody NewSingleChoiceTaskDTO newSingleChoiceTaskDTO) {
        Task task = taskUseCase.createSingleChoiceTask(newSingleChoiceTaskDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PostMapping("/task/new/multiplechoice")
    public ResponseEntity<Task> newMultipleChoice(@Valid @RequestBody NewMultipleChoiceTaskDTO newMultipleChoiceTaskDTO) {
        Task task = taskUseCase.createMultipleChoiceTask(newMultipleChoiceTaskDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }
}