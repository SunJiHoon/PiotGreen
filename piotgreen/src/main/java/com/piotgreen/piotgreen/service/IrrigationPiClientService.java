package com.piotgreen.piotgreen.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Service
public class IrrigationPiClientService {
//    ## 각 모듈간 tcp server를 사용한다.
//- piotgreen: 8088
//- intrusion: 8089
//- irrigation: 8090 *
//- lighting: 8091
private static final String SUB_PI_IP = "irrigation.putiez.com"; // 서브 Pi IP 주소
    private static final int SUB_PI_PORT = 8090; // 서브 Pi의 포트 번호

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

//    @PostConstruct
    public void initializeConnection() {
        try {
            socket = new Socket(SUB_PI_IP, SUB_PI_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Connected to Irrigation Pi server at " + SUB_PI_IP + ":" + SUB_PI_PORT);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to connect to Irrigation Pi server: " + e.getMessage());
        }
    }

    public String sendCommand(String command) {
        String response = "";
        try {
            if (out != null && in != null) {
                // 서브 Pi에 명령어 전송
                out.println(command);
                out.flush();

                // 서브 Pi로부터 응답 수신
                response = in.readLine(); // 응답의 첫 줄을 수신
                System.out.println("Received response from Sub Pi: " + response);
            } else {
                response = "Connection not established. Command not sent.";
                System.out.println(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response = "Error receiving response from Sub Pi: " + e.getMessage();
        }
        return response;
    }


    // 필요 시 종료 시 리소스를 정리할 메서드
    public void closeConnection() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
            System.out.println("Connection to Sub Pi server closed.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
