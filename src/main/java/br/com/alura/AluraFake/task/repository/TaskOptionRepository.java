package br.com.alura.AluraFake.task.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.alura.AluraFake.task.entity.TaskOption;

public interface TaskOptionRepository extends JpaRepository<TaskOption, Long> {
}
