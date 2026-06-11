package com.example.mapper;

import com.example.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationMapper {
    /**
     * 插入通知
     * @param notification 通知信息
     */
    void insert(Notification notification);

    /**
     * 根据用户ID查询通知列表
     * @param userId 用户ID
     * @return 通知列表
     */
    List<Notification> selectByUserId(Integer userId);

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
     * 根据用户ID查询未读通知数量
     * @param userId 用户ID
     * @return 未读通知数量
     */
    Integer countUnreadByUserId(Integer userId);

    /**
     * 根据ID删除通知
     * @param id 通知ID
     */
    void deleteById(Integer id);

    /**
     * 根据用户ID删除通知
     * @param userId 用户ID
     */
    void deleteByUserId(Integer userId);

    /**
     * 获取所有通知
     * @return 通知列表
     */
    List<Notification> getAll();
}