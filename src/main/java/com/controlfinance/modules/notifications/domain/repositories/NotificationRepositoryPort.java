package com.controlfinance.modules.notifications.domain.repositories;

import com.controlfinance.modules.notifications.domain.entities.Notification;

public interface NotificationRepositoryPort {
  Notification save(Notification notification);
}