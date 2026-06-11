package com.example.mapper;

import com.example.entity.SemesterTimeRange;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface SemesterTimeRangeMapper {
    SemesterTimeRange selectBySemester(String semester);
    void insert(SemesterTimeRange timeRange);
    void update(SemesterTimeRange timeRange);
    SemesterTimeRange selectCurrentSemester();
    List<SemesterTimeRange> selectAll();
}
