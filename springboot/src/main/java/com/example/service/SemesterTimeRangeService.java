package com.example.service;

import com.example.entity.SemesterTimeRange;
import java.util.List;

public interface SemesterTimeRangeService {
    SemesterTimeRange getCurrentSemesterTimeRange();
    SemesterTimeRange getSemesterTimeRange(String semester);
    boolean isInCourseCreateTimeRange(String semester);
    boolean isInCourseSelectTimeRange(String semester);
    void setSemesterTimeRange(SemesterTimeRange timeRange);
    List<SemesterTimeRange> getTimeRangeList();
}
