package com.piotgreen.piotgreen.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

@Service
public class SocketClientService implements Runnable {

    private static final String SERVER_IP = "main.putiez.com"; // 서버 IP 주소 입력
    private static final int SERVER_PORT = 8088;
    private static final int BUFSIZE = 1024;

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // WebSocket 전송용 템플릿

    @Override
    public void run() {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String receivedMessage;
            while ((receivedMessage = in.readLine()) != null) {
                // WebSocket을 통해 클라이언트로 메시지 전송
                messagingTemplate.convertAndSend("/topic/messages", receivedMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}