package br.com.alura.AluraFake.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.alura.AluraFake.course.entity.Course;

public interface CourseRepository extends JpaRepository<Course, Long>{

}
