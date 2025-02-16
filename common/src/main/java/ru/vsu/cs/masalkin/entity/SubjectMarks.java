package ru.vsu.cs.masalkin.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubjectMarks {
    private String subject_name;
    private String teacher;
    private Integer att1;
    private Integer att2;
    private Integer att3;
    private Integer result;
    private String result5;

    public SubjectMarks(String subject_name, String teacher, Integer att1, Integer att2, Integer att3, Integer result) {
        this.subject_name = subject_name;
        this.teacher = teacher;
        this.att1 = att1;
        this.att2 = att2;
        this.att3 = att3;
        this.result = result;
        if (result == null) {
            this.result5 = null;
        } else {
            if (result >= 90) {
                this.result5 = "Отлично";
            } else if (result >= 70) {
                this.result5 = "Хорошо";
            } else if (result >= 50) {
                this.result5 = "Удовлетворительно";
            } else {
                this.result5 = "Неудовлетворительно";
            }
        }
    }
}