package com.piotgreen.piotgreen.service;

import com.piotgreen.piotgreen.handler.VideoStreamHandlerJpeg;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.List;

@Service
@AllArgsConstructor
public class VideoStreamService {
    private final int PORT = 8081; // Python 클라이언트와 동일한 포트
    private final VideoStreamHandlerJpeg handler;
//    private final List<WebSocketSession> sessions;


//    public void asdf() {
//        startServer();
//    }
    @PostConstruct
    private void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                System.out.println("Video Stream Server is running on port " + PORT);

                while (true) {
                    try (Socket socket = serverSocket.accept();
                         InputStream inputStream = socket.getInputStream()) {

                        System.out.println("Client connected: " + socket.getInetAddress());
                        while (true) {
                            // 1. 프레임 크기 읽기
                            byte[] sizeBuffer = new byte[4];
                            if (inputStream.read(sizeBuffer) != 4) {
                                System.out.println("Failed to read frame size");
                                break;
                            }
                            int frameSize = ByteBuffer.wrap(sizeBuffer).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();

                            // 2. 프레임 데이터 읽기
                            byte[] frameData = new byte[frameSize];
                            int totalBytesRead = 0;

                            while (totalBytesRead < frameSize) {
                                int bytesRead = inputStream.read(frameData, totalBytesRead, frameSize - totalBytesRead);
                                if (bytesRead == -1) {
                                    System.out.println("Connection closed before complete frame was received");
                                    break;
                                }
                                totalBytesRead += bytesRead;
                            }

                            if (totalBytesRead != frameSize) {
                                System.out.println("Failed to read complete frame data");
                                break;
                            }

                            System.out.println("Received frame of size: " + frameSize);
                            // WebSocket으로 전송하는 코드는 유지
                            BinaryMessage message = new BinaryMessage(frameData);
                            List<WebSocketSession> sessions = handler.getSessions();
                            for (WebSocketSession session : sessions) {
                                if (session.isOpen()) {
                                    session.sendMessage(message);
                                }
                            }

                        }
                    } catch (Exception e) {
                        System.err.println("Error handling client connection: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


}
