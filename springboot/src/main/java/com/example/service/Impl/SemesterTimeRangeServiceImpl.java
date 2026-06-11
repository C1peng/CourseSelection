package com.example.service.Impl;

import com.example.entity.SemesterTimeRange;
import com.example.mapper.SemesterTimeRangeMapper;
import com.example.service.SemesterTimeRangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SemesterTimeRangeServiceImpl implements SemesterTimeRangeService {
    @Autowired
    private SemesterTimeRangeMapper timeRangeMapper;

    @Override
    public SemesterTimeRange getCurrentSemesterTimeRange() {
        return timeRangeMapper.selectCurrentSemester();
    }

    @Override
    public SemesterTimeRange getSemesterTimeRange(String semester) {
        return timeRangeMapper.selectBySemester(semester);
    }

    @Override
    public boolean isInCourseCreateTimeRange(String semester) {
        SemesterTimeRange timeRange = timeRangeMapper.selectBySemester(semester);
        if (timeRange == null) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(timeRange.getCourseCreateStart()) && now.isBefore(timeRange.getCourseCreateEnd());
    }

    @Override
    public boolean isInCourseSelectTimeRange(String semester) {
        SemesterTimeRange timeRange = timeRangeMapper.selectBySemester(semester);
        if (timeRange == null) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(timeRange.getCourseSelectStart()) && now.isBefore(timeRange.getCourseSelectEnd());
    }

    @Override
    public void setSemesterTimeRange(SemesterTimeRange timeRange) {
        SemesterTimeRange existing = timeRangeMapper.selectBySemester(timeRange.getSemester());
        if (existing == null) {
            timeRangeMapper.insert(timeRange);
        } else {
            timeRangeMapper.update(timeRange);
        }
    }

    @Override
    public List<SemesterTimeRange> getTimeRangeList() {
        return timeRangeMapper.selectAll();
    }
}
