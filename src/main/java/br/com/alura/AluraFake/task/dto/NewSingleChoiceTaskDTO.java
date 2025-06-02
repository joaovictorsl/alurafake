package br.com.alura.AluraFake.task.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

import br.com.alura.AluraFake.course.entity.Course;
import br.com.alura.AluraFake.task.entity.Task;
import br.com.alura.AluraFake.task.entity.Type;

public class NewSingleChoiceTaskDTO extends NewOpenTextTaskDTO {

    @NotNull
    @Size(min = 2, max = 5)
    private List<OptionDTO> options;

    public NewSingleChoiceTaskDTO() {}

    public NewSingleChoiceTaskDTO(Long courseId, String statement, Integer order, List<OptionDTO> options) {
        super(courseId, statement, order);
        this.options = options;
    }

    public List<OptionDTO> getOptions() {
        return options;
    }

    public void setOptions(List<OptionDTO> options) {
        this.options = options;
    }

    public Task toTask(Course course) {
        return new Task(this.getStatement(), this.getOrder(), course, Type.SINGLE_CHOICE);
    }
}
