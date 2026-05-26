package com.example.aspen.Service;


import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Async
@Service
public class MailService {

    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtp(String email , String otp) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Aspen Email Verification");
        message.setText("Your OTP is: " + otp);

        mailSender.send(message);
    }

    public void sendResetEmail(String email, String token) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset Request");

        //fake deep link for testing
        String resetLink = "aspen://reset-password?token=" + token;

        String body = """
                Click below link to reset password:
                
                %s
                
                Link expires in 10 minutes.
                """.formatted(resetLink);

        message.setText(body);

        mailSender.send(message);
    }
}
