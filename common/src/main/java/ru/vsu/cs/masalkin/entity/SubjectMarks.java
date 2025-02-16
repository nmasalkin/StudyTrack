package ru.vsu.cs.masalkin.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubjectMarks {
    private String subject_name;
    private String teacher;
    private int att1;
    private int att2;
    private int att3;
    private Integer result;
    private String result5;

    public SubjectMarks(String subject_name, String teacher, int att1, int att2, int att3, Integer result) {
        this.subject_name = subject_name;
        this.teacher = teacher;
        this.att1 = att1;
        this.att2 = att2;
        this.att3 = att3;
        this.result = result;
        this.result5 = result.toString();
    }
}