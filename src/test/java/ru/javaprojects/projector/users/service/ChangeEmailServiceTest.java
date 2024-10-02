package ru.javaprojects.projector.users.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import ru.javaprojects.projector.common.error.IllegalRequestDataException;
import ru.javaprojects.projector.users.mail.MailSender;
import ru.javaprojects.projector.users.model.ChangeEmailToken;
import ru.javaprojects.projector.users.repository.ChangeEmailTokenRepository;

import java.util.Date;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static ru.javaprojects.projector.users.UserTestData.*;
import static ru.javaprojects.projector.users.service.TokenService.LINK_TEMPLATE;

@SpringBootTest
@Sql(scripts = "classpath:data.sql", config = @SqlConfig(encoding = "UTF-8"))
@ActiveProfiles({"dev", "test"})
class ChangeEmailServiceTest {

    @Value("${change-email.confirm-url}")
    private String confirmChangeEmailUrl;

    @MockBean
    private MailSender mailSender;

    @Autowired
    private ChangeEmailService changeEmailService;

    @Autowired
    private ChangeEmailTokenRepository changeEmailTokenRepository;

    @Autowired
    private MessageSource messageSource;

    @Test
    void changeEmail() {
        changeEmailService.changeEmail(USER_ID, NEW_EMAIL);
        ChangeEmailToken createdToken = changeEmailTokenRepository.findByUser_Id(USER_ID).orElseThrow();
        assertTrue(createdToken.getExpiryDate().after(new Date()));
        Locale locale = LocaleContextHolder.getLocale();
        String changeEmailUrlLinkText = messageSource.getMessage("change-email.message-link-text", null, locale);
        String changeEmailMessageSubject = messageSource.getMessage("change-email.message-subject", null, locale);
        String changeEmailMessageText = messageSource.getMessage("change-email.message-text", null, locale);
        String link = String.format(LINK_TEMPLATE, confirmChangeEmailUrl, createdToken.getToken(),
                changeEmailUrlLinkText);
        String emailText = changeEmailMessageText + link;
        Mockito.verify(mailSender, Mockito.times(1)).sendEmail(NEW_EMAIL, changeEmailMessageSubject, emailText);
    }

    @Test
    void changeEmailDuplicateEmail() {
        IllegalRequestDataException exception =
                assertThrows(IllegalRequestDataException.class, () -> changeEmailService.changeEmail(USER_ID, ADMIN_MAIL));
        assertEquals("change-email.already-in-use", exception.getMessageCode());
        assertEquals("email=" + ADMIN_MAIL + " already in use", exception.getMessage());
        assertTrue(changeEmailTokenRepository.findByUser_Id(USER_ID).isEmpty());
        Mockito.verify(mailSender, Mockito.times(0)).sendEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void changeEmailAlreadyHas() {
        IllegalRequestDataException exception =
                assertThrows(IllegalRequestDataException.class, () -> changeEmailService.changeEmail(USER_ID, USER_MAIL));
        assertEquals("change-email.already-has-email", exception.getMessageCode());
        assertEquals("user with id=" + USER_ID + " already has email=" + USER_MAIL, exception.getMessage());
        assertTrue(changeEmailTokenRepository.findByUser_Id(USER_ID).isEmpty());
        Mockito.verify(mailSender, Mockito.times(0)).sendEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void changeEmailWhenChangeEmailTokenExists() {
        changeEmailService.changeEmail(ADMIN_ID, NEW_EMAIL);
        ChangeEmailToken updatedToken = changeEmailTokenRepository.findByUser_Id(ADMIN_ID).orElseThrow();
        assertTrue(updatedToken.getExpiryDate().after(new Date()));
        assertEquals(NEW_EMAIL, updatedToken.getNewEmail());
        Locale locale = LocaleContextHolder.getLocale();
        String changeEmailUrlLinkText = messageSource.getMessage("change-email.message-link-text", null, locale);
        String changeEmailMessageSubject = messageSource.getMessage("change-email.message-subject", null, locale);
        String changeEmailMessageText = messageSource.getMessage("change-email.message-text", null, locale);
        String link = String.format(LINK_TEMPLATE, confirmChangeEmailUrl, updatedToken.getToken(),
                changeEmailUrlLinkText);
        String emailText = changeEmailMessageText + link;
        Mockito.verify(mailSender, Mockito.times(1)).sendEmail(NEW_EMAIL, changeEmailMessageSubject, emailText);
    }

    @Test
    void changeEmailWhenSomeOneHasTokenWithThisEmail() {
        changeEmailService.changeEmail(USER_ID, NEW_EMAIL_SOMEONE_HAS_TOKEN);
        ChangeEmailToken createdToken = changeEmailTokenRepository.findByUser_Id(USER_ID).orElseThrow();
        assertTrue(createdToken.getExpiryDate().after(new Date()));
        Locale locale = LocaleContextHolder.getLocale();
        String changeEmailUrlLinkText = messageSource.getMessage("change-email.message-link-text", null, locale);
        String changeEmailMessageSubject = messageSource.getMessage("change-email.message-subject", null, locale);
        String changeEmailMessageText = messageSource.getMessage("change-email.message-text", null, locale);
        String link = String.format(LINK_TEMPLATE, confirmChangeEmailUrl, createdToken.getToken(),
                changeEmailUrlLinkText);
        String emailText = changeEmailMessageText + link;
        Mockito.verify(mailSender, Mockito.times(1)).sendEmail(NEW_EMAIL_SOMEONE_HAS_TOKEN, changeEmailMessageSubject,
                emailText);
    }
}