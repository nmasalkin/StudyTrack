package ru.vsu.cs.masalkin.service.impl;

import ru.vsu.cs.masalkin.entity.SubjectMarks;

import java.util.List;
import java.util.Map;

public interface JsonMapper {
    List<SubjectMarks> getStudentMarksBySemester(List<Map<String, Object>> json, int semesterNumber);
    List<SubjectMarks> getStudentMarks(List<Map<String, Object>> json);
}
