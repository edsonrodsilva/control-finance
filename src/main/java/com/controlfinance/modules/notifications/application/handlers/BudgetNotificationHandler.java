package com.controlfinance.modules.notifications.application.handlers;

import com.controlfinance.modules.budget.domain.events.BudgetLimitReachedEvent;
import com.controlfinance.modules.notifications.domain.entities.Notification;
import com.controlfinance.modules.notifications.domain.repositories.NotificationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BudgetNotificationHandler {

  private final NotificationRepositoryPort repo;

  @EventListener
  public void on(BudgetLimitReachedEvent event) {
    String sev = event.severity().equals("EXCEEDED") ? "CRITICAL" : "WARNING";
    Notification n = Notification.builder()
        .title("Alerta de Orçamento")
        .message(event.message())
        .severity(sev)
        .read(false)
        .build();
    n.setUserId(event.userId());
    repo.save(n);
  }
}
