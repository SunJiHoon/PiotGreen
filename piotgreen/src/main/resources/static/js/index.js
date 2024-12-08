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
// const intrusionLevelElement = document.getElementById("intrusion-level-1");
const intrusionLevelElement99 = document.getElementById("intrusion-level-199");
// const intrusionIndicatorElement = document.getElementById('intrusion-indicator');
// const intrusionIndicatorElement99 = document.getElementById('intrusion-indicator99');
const intrusionIndicatorElement2 = document.getElementById('intrusion-section-1');

const socket = new SockJS('/websocket');
const stompClient = Stomp.over(socket);


// 광량 섹션 업데이트 함수
function updateLightLevel(section, value) {
    if (section === 1) {
        document.getElementById('light-level-1').textContent = value;
    } else if (section === 2) {
        document.getElementById('light-level-2').textContent = value;
    }
}


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
                    if (Array.isArray(data) && data.length === 2) {
                        // data[0]은 섹션 1, data[1]은 섹션 2의 광량 값
                        updateLightLevel(1, data[0]);
                        updateLightLevel(2, data[1]);
                    } else {
                        console.error("유효하지 않은 광량 데이터 형식:", data);
                    }
                } catch (error) {
                    console.error("광원량 메시지 본문을 파싱하는 중 오류 발생:", error);
                }
            } else {
                console.log("Received an empty light level message body.");
            }
        });

        stompClient.subscribe('/topic/intrusion_detection/danger', function (message) {
            console.log("Received intrusion message:", message);
            if (message.body) {
                try {
                    // const data = JSON.parse(message.body);
                    const data = message.body;
                    if (data !== undefined) {
                        // intrusionLevelElement.textContent = data;
                        if (data === "0") {
                            // intrusionLevelElement.textContent = "SAFE";
                            // intrusionIndicatorElement.className = "indicator safe";
                            intrusionIndicatorElement2.className = "intrusion-container safe"
                            // intrusionIndicatorElement99.className ="indicator safe";
                            intrusionLevelElement99.textContent = "SAFE";
                        } else if (data === "1") {
                            // intrusionLevelElement.textContent = "DANGER";
                            // intrusionIndicatorElement.className = "indicator danger";
                            intrusionIndicatorElement2.className = "intrusion-container danger"
                            // intrusionIndicatorElement99.className ="indicator danger";
                            intrusionLevelElement99.textContent = "DANGER";
                        }

                    }
                } catch (error) {
                    console.error("흙 습도 메시지 본문을 파싱하는 중 오류 발생:", error);
                }
            } else {
                console.log("Received an empty moisture message body.");
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


// 공통으로 POST 요청을 보내는 함수
function initializeConnection(endpoint) {
    fetch(endpoint, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        }
    })
        .then(response => response.text())
        .then(data => {
            alert(data); // 서버 응답 메시지를 알림으로 표시
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Failed to initialize connection.');
        });
}
