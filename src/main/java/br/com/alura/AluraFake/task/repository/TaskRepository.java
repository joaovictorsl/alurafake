package br.com.alura.AluraFake.task.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.alura.AluraFake.task.entity.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    boolean existsByStatementAndCourseId(String statement, Long courseId);
    List<Task> findByCourseIdOrderByOrderAsc(Long courseId);
}
