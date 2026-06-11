package com.example.controller;

import com.example.common.Result;
import com.example.entity.Notification;
import com.example.service.NotificationService;
import com.example.utils.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notification")
@Slf4j
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * 获取用户的通知列表
     * @return 通知列表
     */
    @GetMapping("/list")
    public Result getNotifications() {
        Map<String, Object> currentUser = UserUtil.getCurrentUser();
        if (currentUser == null) {
            return Result.error("未登录");
        }
        Integer userId = (Integer) currentUser.get("userId");
        List<Notification> notifications = notificationService.getNotificationsByUserId(userId);
        return Result.success(notifications);
    }

    /**
     * 标记通知为已读
     * @param id 通知ID
     * @return 操作结果
     */
    @PutMapping("/read/{id}")
    public Result markAsRead(@PathVariable Integer id) {
        notificationService.markAsRead(id);
        return Result.success();
    }

    /**
     * 标记所有通知为已读
     * @return 操作结果
     */
    @PutMapping("/read/all")
    public Result markAllAsRead() {
        Map<String, Object> currentUser = UserUtil.getCurrentUser();
        if (currentUser == null) {
            return Result.error("未登录");
        }
        Integer userId = (Integer) currentUser.get("userId");
        notificationService.markAllAsRead(userId);
        return Result.success();
    }

    /**
     * 获取未读通知数量
     * @return 未读通知数量
     */
    @GetMapping("/unread/count")
    public Result getUnreadCount() {
        Map<String, Object> currentUser = UserUtil.getCurrentUser();
        if (currentUser == null) {
            return Result.error("未登录");
        }
        Integer userId = (Integer) currentUser.get("userId");
        Integer count = notificationService.getUnreadCount(userId);
        return Result.success(count);
    }

    /**
     * 删除通知
     * @param id 通知ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result deleteNotification(@PathVariable Integer id) {
        notificationService.deleteNotification(id);
        return Result.success();
    }

    /**
     * 删除所有通知
     * @return 操作结果
     */
    @DeleteMapping("/all")
    public Result deleteAllNotifications() {
        Map<String, Object> currentUser = UserUtil.getCurrentUser();
        if (currentUser == null) {
            return Result.error("未登录");
        }
        Integer userId = (Integer) currentUser.get("userId");
        notificationService.deleteAllNotifications(userId);
        return Result.success();
    }

    /**
     * 管理端获取通知列表（支持分页和查询条件）
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param userId 用户ID
     * @param type 通知类型
     * @return 通知列表
     */
    @GetMapping("/manager/list")
    public Result getManagerNotifications(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String type) {
        List<Notification> notifications = notificationService.getAllNotifications();
        
        // 简单的分页实现
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, notifications.size());
        List<Notification> pageList = notifications.subList(start, end);
        
        // 构建返回数据
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("list", pageList);
        result.put("total", notifications.size());
        
        return Result.success(result);
    }

    /**
     * 批量删除通知
     * @param ids 通知ID列表
     * @return 操作结果
     */
    @DeleteMapping("/batch")
    public Result deleteBatch(@RequestBody Map<String, List<Integer>> request) {
        List<Integer> ids = request.get("ids");
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的通知");
        }
        notificationService.deleteBatch(ids);
        return Result.success();
    }
}