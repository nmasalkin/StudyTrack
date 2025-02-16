package ru.vsu.cs.masalkin.service;

import ru.vsu.cs.masalkin.entity.SubjectMarks;

import java.util.List;
import java.util.Map;

public interface JsonMapper {
    List<SubjectMarks> jsonToSubjectMarks(List<Map<String, Object>> json, int semesterNumber);
}
