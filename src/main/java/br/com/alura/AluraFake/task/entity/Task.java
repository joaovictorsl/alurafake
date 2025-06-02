package br.com.alura.AluraFake.task.entity;

import br.com.alura.AluraFake.course.entity.Course;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

@Entity
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 255)
    @Size(min = 4, max = 255)
    private String statement;
    
    @Column(name = "`order`", nullable = false)
    private Integer order;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('OPEN_TEXT', 'SINGLE_CHOICE', 'MULTIPLE_CHOICE')")
    private Type type;
    
    public Task() {}
    
    public Task(String statement, Integer order, Course course, Type type) {
        this.statement = statement;
        this.order = order;
        this.course = course;
        this.type = type;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public Course getCourse() {
        return course;
    }
    
    public void setCourse(Course course) {
        this.course = course;
    }
    
    public Type getType() {
        return type;
    }
    
    public void setType(Type type) {
        this.type = type;
    }
}
