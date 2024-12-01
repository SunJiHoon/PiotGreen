package com.piotgreen.piotgreen.service;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

@Service
public class SendMessageService {

//    @Value("${phone.number}")
    private final String phoneNumber;

    private final DefaultMessageService messageService;

    public SendMessageService(
            @Value("${sms.primary.key}") String smsPrimaryKey,
            @Value("${sms.secret.key}") String smsSecretKey,
            @Value("${phone.number}") String phoneNumber
    ){
        this.messageService = NurigoApp.INSTANCE.initialize(smsPrimaryKey, smsSecretKey, "https://api.coolsms.co.kr");
        this.phoneNumber = phoneNumber;
    }

    public void sendMessage(String phoneNumberSetTo, String messageText) {
        Message message = new Message();
        message.setFrom(phoneNumber); // 01012345678 형태여야 함.
        message.setTo(phoneNumberSetTo); // 01012345678 형태여야 함.
        message.setText(messageText);
        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
        System.out.println(response);
    }

//    @PostConstruct
    private void sendMessageTest() {
        sendMessage("01023607644", "service start");
    }
}
