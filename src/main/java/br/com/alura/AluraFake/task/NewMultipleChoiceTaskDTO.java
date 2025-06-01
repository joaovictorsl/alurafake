package br.com.alura.AluraFake.task;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

public class NewMultipleChoiceTaskDTO {

    @NotNull
    private Long courseId;
    
    @NotNull
    @NotBlank
    @Length(min = 4, max = 255)
    private String statement;
    
    @NotNull
    @Positive
    private Integer order;
    
    @NotNull
    @Size(min = 3, max = 5)
    private List<OptionDTO> options;

    public NewMultipleChoiceTaskDTO() {}

    public NewMultipleChoiceTaskDTO(Long courseId, String statement, Integer order, List<OptionDTO> options) {
        this.courseId = courseId;
        this.statement = statement;
        this.order = order;
        this.options = options;
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

    public List<OptionDTO> getOptions() {
        return options;
    }

    public void setOptions(List<OptionDTO> options) {
        this.options = options;
    }
}
