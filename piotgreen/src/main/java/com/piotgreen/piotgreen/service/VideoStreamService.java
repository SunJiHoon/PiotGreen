package com.piotgreen.piotgreen.service;

import com.piotgreen.piotgreen.handler.VideoStreamHandlerJpeg;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

@Service
public class VideoStreamService {
    private final int PORT = 8081; // Python 클라이언트와 동일한 포트
    private final VideoStreamHandlerJpeg handler;

    public VideoStreamService(VideoStreamHandlerJpeg handler) {
        this.handler = handler;
        startServer();
    }

    private void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                System.out.println("ServerSocket started on port " + PORT);
                while (true) {
                    Socket socket = serverSocket.accept();
                    InputStream inputStream = socket.getInputStream();

                    byte[] buffer = new byte[4096];
                    int bytesRead;

                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        System.out.println("Received data of size: " + bytesRead);
//                        BinaryMessage message = new BinaryMessage(buffer, 0, bytesRead);
                        BinaryMessage message = new BinaryMessage(buffer, 0, bytesRead, true);
//                        BinaryMessage message = new BinaryMessage(buffer);
                        for (WebSocketSession session : handler.getSessions()) {
                            if (session.isOpen()) {
                                System.out.println("Sending data to WebSocket session");
                                session.sendMessage(message);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}
