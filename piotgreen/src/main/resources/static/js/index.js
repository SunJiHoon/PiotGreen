// index.js

// WebSocket을 통해 실시간 데이터를 받을 수 있도록 설정 (예시)
// const socket = new WebSocket('ws://example.com/socket'); // 실제 서버 주소로 변경 필요
//
// socket.onmessage = function(event) {
//     const data = JSON.parse(event.data);
//     document.getElementById('light-level').textContent = data.lightLevel;
//     document.getElementById('soil-moisture').textContent = data.soilMoisture;
// };


const soilMoistureElement = document.getElementById("soil-moisture");
const lightLevelElement = document.getElementById("light-level");
const socket = new SockJS('/websocket');
const stompClient = Stomp.over(socket);

// STOMP 클라이언트 연결
stompClient.connect({}, function () {
        console.log("STOMP 클라이언트 연결 성공");

        // 흙 습도 토픽에 구독
        stompClient.subscribe('/topic/irrigation_system/moisture', function (message) {
            console.log("Received moisture message:", message);

            if (message.body) {
                try {
                    // const data = JSON.parse(message.body);
                    const data = message.body;
                    if (data !== undefined) {
                        soilMoistureElement.textContent = data;
                    }
                } catch (error) {
                    console.error("흙 습도 메시지 본문을 파싱하는 중 오류 발생:", error);
                }
            } else {
                console.log("Received an empty moisture message body.");
            }
        });

        // 광원량 토픽에 구독
        stompClient.subscribe('/topic/lighting_control/light', function (message) {
            console.log("Received light level message:", message);

            if (message.body) {
                try {
                    const data = JSON.parse(message.body);
                    if (data !== undefined) {
                        lightLevelElement.textContent = data;
                    }
                } catch (error) {
                    console.error("광원량 메시지 본문을 파싱하는 중 오류 발생:", error);
                }
            } else {
                console.log("Received an empty light level message body.");
            }
        });
    }, function (error) {
        console.error("STOMP 클라이언트 연결 실패:", error);
    }
);


function manageLight() {
    alert("광원량 관리 기능을 실행합니다.");
    // 실제 구현 필요
}

function manageSoil() {
    alert("흙 습도 및 관수 관리 기능을 실행합니다.");
    // 실제 구현 필요
}

function monitorFarm() {
    alert("실시간 농장 모니터링 기능을 실행합니다.");
    // 실제 구현 필요
}
