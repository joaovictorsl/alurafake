package br.com.alura.AluraFake.task.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import br.com.alura.AluraFake.course.entity.Course;
import br.com.alura.AluraFake.task.entity.Task;
import br.com.alura.AluraFake.task.entity.Type;

public class NewOpenTextTaskDTO {

    @NotNull
    private Long courseId;
    
    @NotNull
    @NotBlank
    @Length(min = 4, max = 255)
    private String statement;
    
    @NotNull
    @Positive
    private Integer order;

    public NewOpenTextTaskDTO() {}

    public NewOpenTextTaskDTO(Long courseId, String statement, Integer order) {
        this.courseId = courseId;
        this.statement = statement;
        this.order = order;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Task toTask(Course course) {
        return new Task(this.statement, this.order, course, Type.OPEN_TEXT);
    }
}
