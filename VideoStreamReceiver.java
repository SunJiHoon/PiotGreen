import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class VideoStreamReceiver {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Video Stream Receiver");
        JLabel label = new JLabel();
        frame.getContentPane().add(label, BorderLayout.CENTER);
        frame.setSize(640, 480);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        try (ServerSocket serverSocket = new ServerSocket(8081)) {
            System.out.println("서버가 실행 중입니다... 클라이언트 연결 대기 중입니다.");

            Socket clientSocket = serverSocket.accept();
            System.out.println("클라이언트 연결됨: " + clientSocket.getInetAddress());

            InputStream inputStream = clientSocket.getInputStream();
            byte[] sizeBuffer = new byte[4]; // 프레임 크기를 담기 위한 4바이트 버퍼

            while (true) {
                // 프레임 크기 읽기
                int bytesRead = inputStream.read(sizeBuffer);
                if (bytesRead == -1) {
                    break;
                }

                // 프레임 크기 해독 (작은 엔디안 방식)
                int frameSize = ((sizeBuffer[3] & 0xFF) << 24) |
                                ((sizeBuffer[2] & 0xFF) << 16) |
                                ((sizeBuffer[1] & 0xFF) << 8) |
                                (sizeBuffer[0] & 0xFF);

                // 프레임 데이터 읽기
                byte[] frameData = new byte[frameSize];
                bytesRead = 0;
                while (bytesRead < frameSize) {
                    int result = inputStream.read(frameData, bytesRead, frameSize - bytesRead);
                    if (result == -1) {
                        break;
                    }
                    bytesRead += result;
                }

                // JPEG 데이터를 BufferedImage로 변환
                BufferedImage image = ImageIO.read(new java.io.ByteArrayInputStream(frameData));
                if (image != null) {
                    // 이미지를 JLabel에 표시
                    ImageIcon icon = new ImageIcon(image);
                    label.setIcon(icon);
                    frame.revalidate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
