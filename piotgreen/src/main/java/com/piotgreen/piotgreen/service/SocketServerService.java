package com.piotgreen.piotgreen.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

@Service
public class SocketServerService {
    private static final int SERVER_PORT = 8088; // 서버 포트 설정

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // WebSocket 메시지 전송 템플릿

    @PostConstruct
    public void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
                System.out.println("Socket Server started on port " + SERVER_PORT);

                while (true) {
                    Socket clientSocket = serverSocket.accept(); // 클라이언트 연결 대기
                    handleClient(clientSocket);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void handleClient(Socket clientSocket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received: " + inputLine); // 받은 메시지 출력
                out.println("Echo: " + inputLine); // 클라이언트로 에코 전송
                messagingTemplate.convertAndSend("/topic/messages", inputLine); // 클라이언트에 실시간 전송
                out.println("Echo: " + inputLine); // 클라이언트로 에코 전송
                out.flush(); // 명시적으로 flush 호출
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
