package com.controlfinance.modules.notifications.infrastructure.persistence;

import com.controlfinance.modules.notifications.domain.entities.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationMongoRepository extends MongoRepository<Notification, String> {
  List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);
}
