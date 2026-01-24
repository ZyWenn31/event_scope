package com.event.scope.eventScope.service;

import com.event.scope.eventScope.model.TelegramAccount;
import com.event.scope.eventScope.repository.TelegramAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TelegramAccountService {
    private final TelegramAccountRepository telegramAccountRepository;

    public TelegramAccountService(TelegramAccountRepository telegramAccountRepository) {
        this.telegramAccountRepository = telegramAccountRepository;
    }

    public TelegramAccount findById(Long id) {
        return telegramAccountRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Telegram account with id " + id +"not found"));
    }

    public List<TelegramAccount> findAll() {
        return telegramAccountRepository.findAll();
    }

    public TelegramAccount save(TelegramAccount telegramAccount) {
        return telegramAccountRepository.save(telegramAccount);
    }

    public void deleteById(Long id) {
        telegramAccountRepository.deleteById(id);
    }

    public TelegramAccount update(TelegramAccount telegramAccount, Long id) {
        TelegramAccount existing = telegramAccountRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Telegram account with id " + id +"not found"));

        existing.setUser(telegramAccount.getUser());
        existing.setLinkedAt(telegramAccount.getLinkedAt());
        existing.setTelegramChatId(telegramAccount.getTelegramChatId());

        return telegramAccountRepository.save(existing);
    }
}
