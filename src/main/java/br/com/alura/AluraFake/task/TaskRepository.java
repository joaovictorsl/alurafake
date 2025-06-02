package br.com.alura.AluraFake.task;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    boolean existsByStatementAndCourseId(String statement, Long courseId);
    List<Task> findByCourseIdOrderByOrderAsc(Long courseId);
}
