// index.js

// WebSocket을 통해 실시간 데이터를 받을 수 있도록 설정 (예시)
const socket = new WebSocket('ws://example.com/socket'); // 실제 서버 주소로 변경 필요

socket.onmessage = function(event) {
    const data = JSON.parse(event.data);
    document.getElementById('light-level').textContent = data.lightLevel;
    document.getElementById('soil-moisture').textContent = data.soilMoisture;
};

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
