package br.com.alura.AluraFake.task.usecase;

import br.com.alura.AluraFake.task.dto.NewMultipleChoiceTaskDTO;
import br.com.alura.AluraFake.task.dto.NewOpenTextTaskDTO;
import br.com.alura.AluraFake.task.dto.NewSingleChoiceTaskDTO;
import br.com.alura.AluraFake.task.entity.Task;

public interface TaskUseCase {
    public Task createOpenTextTask(NewOpenTextTaskDTO newOpenTextTaskDTO) throws RuntimeException;
    public Task createSingleChoiceTask(NewSingleChoiceTaskDTO newSingleChoiceTaskDTO) throws RuntimeException;
    public Task createMultipleChoiceTask(NewMultipleChoiceTaskDTO newMultipleChoiceTaskDTO) throws RuntimeException;
}
