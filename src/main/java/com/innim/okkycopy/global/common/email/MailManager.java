package com.innim.okkycopy.global.common.email;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode500Exception;
import com.innim.okkycopy.global.util.EncryptionUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Component
@Getter
@RequiredArgsConstructor
public class MailManager {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    public static final Cache<String, EmailAuthenticateValue> emailAuthenticateCache;
    public static final Cache<String, EmailAuthenticateValue> emailChangeAuthenticateCache;
    @Value("#{environment['frontend.origin']}")
    private String frontendOrigin;
    @Value("#{environment['frontend.path.mail-authenticate']}")
    private String mailAuthenticatePath;
    @Value("#{environment['frontend.path.mail-change-authenticate']}")
    private String mailChangeAuthenticatePath;
    @Value("#{environment['encrypt.divider']}")
    private String encryptDivider;

    static {
        emailAuthenticateCache = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build();
        emailChangeAuthenticateCache = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build();
    }
    @Async("mailAsyncExecutor")
    public void sendAuthenticateChangedEmailAndPutCache(String email, Long memberId, boolean isChanged) {
        try {
            String key = EncryptionUtil.encryptWithSHA256(
                EncryptionUtil.connectStrings(email, memberId.toString(), encryptDivider));
            if (isChanged) {
                emailChangeAuthenticateCache.asMap().entrySet().removeIf(entry -> {
                        return Objects.equals(entry.getValue().getMemberId(), memberId);
                    }
                );
                emailChangeAuthenticateCache.put(key, new EmailAuthenticateValue(memberId, email));
            } else {
                emailAuthenticateCache.put(key, new EmailAuthenticateValue(memberId, email));
            }
            try {
                sendAuthenticateChangedEmail(EncryptionUtil.base64Encode(key), isChanged, email);
            } catch (Exception ex) {
                emailAuthenticateCache.invalidate(key);
                throw ex;
            }
        } catch (Exception ex) {
            throw new StatusCode500Exception(ErrorCase._500_SEND_MAIL_FAIL);
        }
    }
    @Async("mailAsyncExecutor")
    public void sendAuthenticateEmailAndPutCache(String email, Long memberId, String name) {
        try {
            String key = EncryptionUtil.encryptWithSHA256(
                EncryptionUtil.connectStrings(email, memberId.toString(), encryptDivider));
            emailAuthenticateCache.put(key, new EmailAuthenticateValue(memberId, email));
            try {
                sendAuthenticateEmail(
                    EncryptionUtil.base64Encode(key),
                    name,
                    email);
            } catch (Exception ex) {
                emailAuthenticateCache.invalidate(key);
                throw ex;
            }
        } catch (Exception ex) {
            throw new StatusCode500Exception(ErrorCase._500_SEND_MAIL_FAIL);
        }
    }

    @Async("mailAsyncExecutor")
    public void sendAuthenticateEmail(String key, String memberName, String receiverEmail) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        Context context = new Context();
        context.setVariable("url", frontendOrigin + mailAuthenticatePath);
        context.setVariable("key", key);
        context.setVariable("memberName", memberName);
        String html = templateEngine.process("email_authenticate", context);

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        mimeMessageHelper.setSubject("[OKKY.copy] 환영합니다. 이메일 인증을 완료해주세요");
        mimeMessageHelper.setTo(receiverEmail);
        mimeMessageHelper.setText(html, true);
        mimeMessageHelper.setFrom("okky.innim@gmail.com");
        mailSender.send(mimeMessage);
    }

    @Async("mailAsyncExecutor")
    public void sendAuthenticateChangedEmail(String key, boolean isChanged, String receiverEmail)
        throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        Context context = new Context();
        context.setVariable("url", frontendOrigin + ((isChanged) ? mailChangeAuthenticatePath : mailAuthenticatePath));
        context.setVariable("key", key);
        context.setVariable("prefix", (isChanged) ? "변경 " : "");
        String html = templateEngine.process("email_change_authenticate", context);

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        mimeMessageHelper.setSubject((isChanged) ? "[OKKY.copy] 이메일 변경 인증을 완료해주세요" : "[OKKY.copy] 이메일 인증을 완료해주세요");
        mimeMessageHelper.setTo(receiverEmail);
        mimeMessageHelper.setText(html, true);
        mimeMessageHelper.setFrom("okky.innim@gmail.com");
        mailSender.send(mimeMessage);
    }

    public Optional<EmailAuthenticateValue> findValueByEmailAuthenticate(String key) {
        return Optional.ofNullable(emailAuthenticateCache.getIfPresent(key));
    }

    public Optional<EmailAuthenticateValue> findValueByEmailChangeAuthenticate(String key) {
        return Optional.ofNullable(emailChangeAuthenticateCache.getIfPresent(key));
    }

    public void removeKey(String key) {
        emailAuthenticateCache.invalidate(key);
        emailChangeAuthenticateCache.invalidate(key);
    }

}
