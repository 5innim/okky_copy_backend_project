package com.innim.okkycopy.global.util.email;

import com.google.common.cache.Cache;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode500Exception;
import com.innim.okkycopy.global.util.EncryptionUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Component
@RequiredArgsConstructor
public class MailUtil {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final Cache<String, EmailAuthenticateValue> emailAuthenticateCache;

    @Value("#{environment['frontend.origin']}")
    private String frontendOrigin;
    @Value("#{environment['frontend.path.mail-authenticate']}")
    private String mailAuthenticatePath;

    public void sendAuthenticateEmailAndPutCache(String email, Long memberId, String name) {
        try {
            sendAuthenticateEmail(
                EncryptionUtil.getEmailEncryptionKeyEncodedWithBase64(email,
                    memberId.toString()),
                name,
                email);
            emailAuthenticateCache.put(EncryptionUtil.encryptWithSHA256(
                    EncryptionUtil.connectStrings(email, memberId.toString())),
                new EmailAuthenticateValue(memberId, email));
        } catch (Exception ex) {
            throw new StatusCode500Exception(ErrorCase._500_SEND_MAIL_FAIL);
        }
    }

    public void sendAuthenticateEmail(String key, String memberName, String receiverEmail) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        Context context = new Context();
        context.setVariable("url", frontendOrigin + mailAuthenticatePath);
        context.setVariable("key", key);
        context.setVariable("memberName", memberName);
        String html = templateEngine.process("email_authenticate", context);

        mimeMessageHelper.setSubject("[OKKY.copy] 환영합니다. 이메일 인증을 완료해주세요");
        mimeMessageHelper.setTo(receiverEmail);
        mimeMessageHelper.setText(html, true);
        mailSender.send(mimeMessage);
    }

    public void sendAuthenticateChangedEmail(String key, boolean isChanged, String receiverEmail) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        Context context = new Context();
        context.setVariable("url", frontendOrigin + mailAuthenticatePath);
        context.setVariable("key", key);
        context.setVariable("prefix", (isChanged) ? "변경 " : "");
        String html = templateEngine.process("email_change_authenticate", context);

        mimeMessageHelper.setSubject((isChanged) ? "[OKKY.copy] 이메일 변경 인증을 완료해주세요" : "[OKKY.copy] 이메일 인증을 완료해주세요");
        mimeMessageHelper.setTo(receiverEmail);
        mimeMessageHelper.setText(html, true);
        mailSender.send(mimeMessage);
    }

}
