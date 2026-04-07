package com.controlfinance.modules.notifications.infrastructure.persistence;

import com.controlfinance.modules.notifications.domain.entities.Notification;
import com.controlfinance.modules.notifications.domain.repositories.NotificationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryAdapter implements NotificationRepositoryPort {
  private final NotificationMongoRepository repo;

  @Override
  public Notification save(Notification notification) {
    return repo.save(notification);
  }
}