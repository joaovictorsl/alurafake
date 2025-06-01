package br.com.alura.AluraFake.task;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
public class TaskController {

    @PostMapping("/task/new/opentext")
    public ResponseEntity newOpenTextExercise(@Valid @RequestBody NewOpenTextTaskDTO newOpenTextTaskDTO) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/task/new/singlechoice")
    public ResponseEntity newSingleChoice(@Valid @RequestBody NewSingleChoiceTaskDTO newSingleChoiceTaskDTO) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/task/new/multiplechoice")
    public ResponseEntity newMultipleChoice(@Valid @RequestBody NewMultipleChoiceTaskDTO newMultipleChoiceTaskDTO) {
        return ResponseEntity.ok().build();
    }

}