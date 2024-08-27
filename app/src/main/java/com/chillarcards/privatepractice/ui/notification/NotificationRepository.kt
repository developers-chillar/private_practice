package com.chillarcards.privatepractice.ui.notification

import com.chillarcards.privatepractice.model.NotificationItem

class NotificationRepository {
    private val notifications = mutableListOf<NotificationItem>()

    fun getNotifications(): List<NotificationItem> {
        return notifications
    }

    fun addNotification(notification: NotificationItem) {
        notifications.add(notification)
    }

}
