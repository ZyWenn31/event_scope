package com.event.scope.eventScope.repository;

import com.event.scope.eventScope.model.TelegramAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelegramAccountRepository extends JpaRepository<TelegramAccount, Long> {
}
