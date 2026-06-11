package com.example.task;

import com.example.entity.SemesterTimeRange;
import com.example.service.SemesterTimeRangeService;
import com.example.service.StudentService;
import com.example.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class CreditReminderTask {

    @Autowired
    private SemesterTimeRangeService semesterTimeRangeService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private NotificationService notificationService;

    // 每分钟执行一次（测试用）
    @Scheduled(cron = "0 * * * * ?")
    public void checkCourseSelectStart() {
        System.out.println("========== 学分提醒定时任务开始执行 ==========");
        // 获取所有学期时间范围
        List<SemesterTimeRange> timeRanges = semesterTimeRangeService.getTimeRangeList();
        LocalDateTime now = LocalDateTime.now();

        for (SemesterTimeRange timeRange : timeRanges) {
            // 检查是否是今天开始选课
            LocalDateTime courseSelectStart = timeRange.getCourseSelectStart();
            System.out.println("检查学期: " + timeRange.getSemester() + ", 选课开始时间: " + courseSelectStart + ", 当前时间: " + now);
            if (courseSelectStart != null && 
                courseSelectStart.toLocalDate().equals(now.toLocalDate())) {
                // 只要是今天开始选课就发送通知（测试用）
                System.out.println("检测到今天开始选课，发送学分提醒通知！");
                // 发送学分提醒通知
                sendCreditReminderNotifications(timeRange.getSemester());
            }
        }
    }

    private void sendCreditReminderNotifications(String semester) {
        // 获取所有学生ID
        List<Integer> studentIds = studentService.getAllStudentIds();
        System.out.println("开始为 " + studentIds.size() + " 个学生发送学分提醒通知");
        
        for (Integer studentId : studentIds) {
            // 计算学分差距
            String creditGapInfo = studentService.calculateCreditGap(studentId);
            System.out.println("学生ID: " + studentId + "，学分差距：" + creditGapInfo);
            
            // 构建通知内容
            String content = "【选课提醒】\n" +
                            "学期：" + semester + "\n" +
                            "当前学分差距：\n" + creditGapInfo + "\n" +
                            "请及时选课，确保学分达标。";
            
            // 发送通知
            notificationService.sendNotification(studentId, "CREDIT_REMINDER", content);
            System.out.println("已为学生ID: " + studentId + " 发送通知");
        }
        System.out.println("学分提醒通知发送完成");
    }
}