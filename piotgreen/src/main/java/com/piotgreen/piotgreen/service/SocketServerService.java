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
//    ## 각 모듈간 tcp server를 사용한다.
//- piotgreen: 8088 *
//- intrusion: 8089
//- irrigation: 8090
//- lighting: 8091
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
                    // 새 클라이언트 연결마다 독립적인 스레드로 처리
                    new Thread(() -> handleClient(clientSocket)).start();
//                    Socket clientSocket = serverSocket.accept(); // 클라이언트 연결 대기
//                    handleClient(clientSocket);
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
//                out.println("Echo: " + inputLine); // 클라이언트로 에코 전송
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
