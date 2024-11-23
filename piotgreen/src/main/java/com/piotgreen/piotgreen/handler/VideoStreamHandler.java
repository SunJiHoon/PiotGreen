//package com.piotgreen.piotgreen.handler;
//
//import org.springframework.web.socket.BinaryMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.BinaryWebSocketHandler;
//
//import java.io.InputStream;
//
////비디오 스트림을 송신하는 자는
//// ffmpeg -f video4linux2 -i /dev/video0 -vcodec h264 -f mpegts udp://main.putiez.com:5000를 사용한다.
////현재 이 파일에 작성된 코드는 비디오 스트림 수신 코드이다.
//public class VideoStreamHandler extends BinaryWebSocketHandler {
//    private Process ffmpegProcess;
//
//    public VideoStreamHandler() {
//        try {
//            ffmpegProcess = new ProcessBuilder(
//                    "ffmpeg", "-i", "udp://main.putiez.com:5000",
//                    "-vf", "fps=10", "-f", "image2pipe", "-vcodec", "mjpeg", "-"
//            ).start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        new Thread(() -> {
//            try (InputStream inputStream = ffmpegProcess.getInputStream()) {
//                byte[] buffer = new byte[4096];
//                int bytesRead;
//                while ((bytesRead = inputStream.read(buffer)) != -1) {
////                    session.sendMessage(new BinaryMessage(buffer, 0, bytesRead));
//                    session.sendMessage(new BinaryMessage(buffer));
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start();
//    }
//
//}
