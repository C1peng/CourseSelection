package com.example.service.Impl;

import com.example.entity.Notification;
import com.example.mapper.NotificationMapper;
import com.example.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    public void sendNotification(Integer userId, String type, String content) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setContent(content);
        notification.setIsRead(0); // 初始为未读
        notification.setCreateTime(LocalDateTime.now());
        notificationMapper.insert(notification);
    }

    @Override
    public List<Notification> getNotificationsByUserId(Integer userId) {
        return notificationMapper.selectByUserId(userId);
    }

    @Override
    public void markAsRead(Integer id) {
        notificationMapper.markAsRead(id);
    }

    @Override
    public void markAllAsRead(Integer userId) {
        notificationMapper.markAllAsRead(userId);
    }

    @Override
    public Integer getUnreadCount(Integer userId) {
        return notificationMapper.countUnreadByUserId(userId);
    }

    @Override
    public void deleteNotification(Integer id) {
        notificationMapper.deleteById(id);
    }

    @Override
    public void deleteAllNotifications(Integer userId) {
        notificationMapper.deleteByUserId(userId);
    }

    @Override
    public List<Notification> getAllNotifications() {
        return notificationMapper.getAll();
    }

    @Override
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            notificationMapper.deleteById(id);
        }
    }
}