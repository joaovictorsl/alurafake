package br.com.alura.AluraFake.task;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

import br.com.alura.AluraFake.course.Course;

public class NewMultipleChoiceTaskDTO extends NewOpenTextTaskDTO {

    @NotNull
    @Size(min = 3, max = 5)
    private List<OptionDTO> options;

    public NewMultipleChoiceTaskDTO() {}

    public NewMultipleChoiceTaskDTO(Long courseId, String statement, Integer order, List<OptionDTO> options) {
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
        return new Task(this.getStatement(), this.getOrder(), course, Type.MULTIPLE_CHOICE);
    }
}
