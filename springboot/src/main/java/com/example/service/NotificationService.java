package com.example.service;

import com.example.entity.Notification;
import java.util.List;

public interface NotificationService {
    /**
     * 发送通知
     * @param userId 用户ID
     * @param type 通知类型
     * @param content 通知内容
     */
    void sendNotification(Integer userId, String type, String content);

    /**
     * 获取用户的通知列表
     * @param userId 用户ID
     * @return 通知列表
     */
    List<Notification> getNotificationsByUserId(Integer userId);

    /**
     * 标记通知为已读
     * @param id 通知ID
     */
    void markAsRead(Integer id);

    /**
     * 标记用户所有通知为已读
     * @param userId 用户ID
     */
    void markAllAsRead(Integer userId);

    /**
     * 获取用户未读通知数量
     * @param userId 用户ID
     * @return 未读通知数量
     */
    Integer getUnreadCount(Integer userId);

    /**
     * 删除通知
     * @param id 通知ID
     */
    void deleteNotification(Integer id);

    /**
     * 删除用户所有通知
     * @param userId 用户ID
     */
    void deleteAllNotifications(Integer userId);

    /**
     * 获取所有通知
     * @return 通知列表
     */
    List<Notification> getAllNotifications();

    /**
     * 批量删除通知
     * @param ids 通知ID列表
     */
    void deleteBatch(List<Integer> ids);
}