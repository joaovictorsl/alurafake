package br.com.alura.AluraFake.course.usecase;

import br.com.alura.AluraFake.course.dto.CourseListItemDTO;
import br.com.alura.AluraFake.course.dto.NewCourseDTO;
import br.com.alura.AluraFake.course.entity.Course;

public interface CourseUseCase {
    public void publishCourse(Long id);
    public Course createCourse(NewCourseDTO newCourse);
    public java.util.List<CourseListItemDTO> getAllCourses();
}
