package ru.vsu.cs.masalkin.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class SubjectMarks {
    private String subjectName;
    private Integer att1;
    private Integer att2;
    private Integer att3;
    private Integer result;
    private String result5;

    public SubjectMarks(String subjectName, Integer att1, Integer att2, Integer att3, Integer result) {
        this.subjectName = subjectName;
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
                this.result5 = "Удовл.";
            } else {
                this.result5 = "Неудовл.";
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubjectMarks that = (SubjectMarks) o;
        return Objects.equals(subjectName, that.subjectName) &&
               Objects.equals(att1, that.att1) &&
               Objects.equals(att2, that.att2) &&
               Objects.equals(att3, that.att3) &&
               Objects.equals(result, that.result) &&
               Objects.equals(result5, that.result5);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subjectName, att1, att2, att3, result, result5);
    }
}