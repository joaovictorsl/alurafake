package br.com.alura.AluraFake.course;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class CourseController {

    private final CourseUseCase courseUseCase;

    @Autowired
    public CourseController(CourseUseCase courseUseCase) {
        this.courseUseCase = courseUseCase;
    }

    @Transactional
    @PostMapping("/course/new")
    public ResponseEntity<?> createCourse(@Valid @RequestBody NewCourseDTO newCourse) {
        courseUseCase.createCourse(newCourse);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/course/all")
    public ResponseEntity<List<CourseListItemDTO>> getAllCourses() {
        List<CourseListItemDTO> courses = courseUseCase.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @PostMapping("/course/{id}/publish")
    @Transactional
    public ResponseEntity<?> publishCourse(@PathVariable("id") Long id) {
        courseUseCase.publishCourse(id);
        return ResponseEntity.ok().build();
    }
}
