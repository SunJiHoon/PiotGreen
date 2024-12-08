package com.piotgreen.piotgreen.service;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

@Service
@AllArgsConstructor
public class SocketServerService {
//    ## 각 모듈간 tcp server를 사용한다.
//- piotgreen: 8088 *
//- intrusion: 8089
//- irrigation: 8090
//- lighting: 8091
    private static final int SERVER_PORT = 8088; // 서버 포트 설정
    private final ManagerDataStorageService managerDataStorageService;

    private SimpMessagingTemplate messagingTemplate; // WebSocket 메시지 전송 템플릿
    private final LightingDataStorageService lightingDataStorageService;
    private final IrrigationDataStorageService irrigationDataStorageService;
    private final IntrusionDataStorageService intrusionDataStorageService;

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
//                System.out.println("Received: " + inputLine); // 받은 메시지 출력
//                out.println("Echo: " + inputLine); // 클라이언트로 에코 전송
//                messagingTemplate.convertAndSend("/topic/messages", inputLine); // 클라이언트에 실시간 전송

                System.out.println("Received: " + inputLine); // Received message

                // Split the input into major category, subcategory, and data
                String[] parts = inputLine.split(":");
                if (parts.length >= 3) {
                    String majorCategory = parts[0];
                    String subCategory = parts[1];
                    String data = parts[2];

                    // Log received data based on categories
                    System.out.println("Category: " + majorCategory);
                    System.out.println("Subcategory: " + subCategory);
                    System.out.println("Data: " + data);

                    // Process data based on major category
                    if ("lighting_control".equalsIgnoreCase(majorCategory)) {
                        if ("light".equalsIgnoreCase(subCategory)) {
                            processLightData(data);
                        }
                        else if ("led".equalsIgnoreCase(subCategory)) {
                            processLedData(data);
                        }
                        else{
                            System.out.println("Unknown subCategory: " + subCategory);
                        }
                    } else if ("irrigation_system".equalsIgnoreCase(majorCategory)) {
                        if ("moisture".equalsIgnoreCase(subCategory)) {
                            processIrrigationData(data);
                        }
                        else{
                            System.out.println("Unknown subCategory: " + subCategory);
                        }
                    } else if ("intrusion_detection".equalsIgnoreCase(majorCategory)) {
                        if ("danger".equalsIgnoreCase(subCategory)) {
                            processIntrusionData(data);
                        }
                        else{
                            System.out.println("Unknown subCategory: " + subCategory);
                        }
                    } else {
                        System.out.println("Unknown category: " + majorCategory);
                    }
                    // Send data based on the major category via WebSocket
                    messagingTemplate.convertAndSend("/topic/" + majorCategory + "/" + subCategory, data);
                } else {
                    System.out.println("Invalid data format: " + inputLine);
                }

                // Echo back to client
                out.println("Echo: " + inputLine);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void processLedData(String data) {
        // 대괄호 제거
        data = data.replace("[", "").replace("]", "");

        String[] ledValues = data.split(",");
        if (ledValues.length == 2) {
            String led1 = ledValues[0].trim();
            String led2 = ledValues[1].trim();
            String led1Status = led1.compareTo("1") == 0 ? "on" : "off";
            String led2Status = led2.compareTo("1") == 0 ? "on" : "off";
            lightingDataStorageService.saveLedData(led1Status, led2Status);
        } else {
            System.out.println("Invalid lighting data format: " + data);
        }
    }

    private void processLightData(String data) {
        // 대괄호 제거
        data = data.replace("[", "").replace("]", "");

        String[] lightLevels = data.split(",");
        if (lightLevels.length == 2) {
            try {
                int lightLevel1 = Integer.parseInt(lightLevels[0]);
                int lightLevel2 = Integer.parseInt(lightLevels[1]);
                lightingDataStorageService.saveLightData(lightLevel1, lightLevel2);
            } catch (NumberFormatException e) {
                System.out.println("Invalid light data values: " + data);
            }
        } else {
            System.out.println("Invalid light data format: " + data);
        }
    }

    private void processIrrigationData(String data) {
        try {
            int moistureLevel = Integer.parseInt(data);
            irrigationDataStorageService.saveIrrigationData(moistureLevel);
        } catch (NumberFormatException e) {
            System.out.println("Invalid irrigation data value: " + data);
        }
    }

    private void processIntrusionData(String data) {
        try {
//            int dangerLevel = Integer.parseInt(data);
            String dangerLevel = data.compareTo("0") == 0 ? "safe" : "danger";
            intrusionDataStorageService.saveIntrusionData(dangerLevel);
            String dangerMessage = "위험 발생: 불법 침입 감지!";
            managerDataStorageService.sendMessageToAllManger(dangerMessage);
        } catch (NumberFormatException e) {
            System.out.println("Invalid intrusion data value: " + data);
        }
    }

}
