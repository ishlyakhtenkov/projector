package ru.javaprojects.projector.users.util;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.javaprojects.projector.users.model.Token;
import ru.javaprojects.projector.users.repository.TokenRepository;

import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class TokenCleaner {
    private final List<TokenRepository<? extends Token>> tokenRepositories;

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteExpiredTokens() {
        log.info("delete expired tokens");
        tokenRepositories.forEach(TokenRepository::deleteExpired);
    }
}
