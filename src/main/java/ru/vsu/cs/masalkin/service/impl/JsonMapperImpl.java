package ru.vsu.cs.masalkin.service.impl;

import org.springframework.stereotype.Service;
import ru.vsu.cs.masalkin.entity.SubjectMarks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class JsonMapperImpl implements JsonMapper {
    @Override
    public List<SubjectMarks> getStudentMarksBySemester(List<Map<String, Object>> json, int semesterNumber) {
        List<SubjectMarks> subjectMarksList = new ArrayList<>();
        for (Map<String, Object> map : json){
            if (map.get("semester").equals(semesterNumber)){
                subjectMarksList.add(new SubjectMarks(map.get("subject_name").toString().replaceAll("\\s?\\(.*?\\)", ""), (Integer) map.get("att1"), (Integer) map.get("att2"), (Integer) map.get("att3"), (Integer) map.get("result")));
            }
        }
        return subjectMarksList;
    }

    @Override
    public List<SubjectMarks> getStudentMarks(List<Map<String, Object>> json) {
        List<SubjectMarks> subjectMarksList = new ArrayList<>();
        for (Map<String, Object> map : json){
            subjectMarksList.add(new SubjectMarks(map.get("subject_name").toString().replaceAll("\\s?\\(.*?\\)", ""), (Integer) map.get("att1"), (Integer) map.get("att2"), (Integer) map.get("att3"), (Integer) map.get("result")));
        }
        return subjectMarksList;
    }
}
