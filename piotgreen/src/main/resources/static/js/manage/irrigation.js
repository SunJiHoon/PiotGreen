// 초기 상태 설정
let currentMode = 'auto';
let currentHumidity = 50; // 초기 습도 값
let targetHumidity = null; // 희망 습도 값

// 모드 설정 함수
function setMode(mode) {
    currentMode = mode;
    const currentModeElement = document.getElementById('current-mode');
    const manualControls = document.querySelector('.manual-controls');

    // 모드에 따라 화면 업데이트
    currentModeElement.textContent = mode === 'auto' ? '자동' : '수동';
    manualControls.style.display = mode === 'manual' ? 'block' : 'none';

    console.log(`현재 모드: ${mode === 'auto' ? '자동' : '수동'}`);


    // Spring 서버에 POST 요청 보내기
    fetch('/irrigation/mode/set', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            mode: mode
        })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('서버 응답 오류');
            }
            return response.json();
        })
        .then(data => {
            console.log('서버 응답:', data);
        })
        .catch(error => {
            console.error('POST 요청 실패:', error);
        });
}

// 희망 습도 설정 함수
function setTargetHumidity() {
    const inputElement = document.getElementById('target-humidity');
    targetHumidity = parseInt(inputElement.value);

    if (isNaN(targetHumidity) || targetHumidity < 0 || targetHumidity > 100) {
        alert('올바른 습도 값을 입력하세요 (0~100 사이).');
        return;
    }

    console.log(`희망 습도 설정: ${targetHumidity}`);
    alert(`희망 습도가 ${targetHumidity}%로 설정되었습니다.`);

    // 희망 습도 값 화면에 반영
    const targetHumidityDisplay = document.getElementById('target-humidity-display');
    targetHumidityDisplay.textContent = `${targetHumidity}%`;

    // Spring 서버에 POST 요청 보내기
    fetch('/irrigation/humidity/set', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            targetHumidity: targetHumidity
        })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('서버 응답 오류');
            }
            return response.json();
        })
        .then(data => {
            console.log('서버 응답:', data);
        })
        .catch(error => {
            console.error('POST 요청 실패:', error);
        });

}

// 실시간 데이터 업데이트 함수 (예제용)
function updateCurrentHumidity() {
    const humidityElement = document.getElementById('humidity-level');
    currentHumidity = Math.floor(Math.random() * 100); // 임의의 값 (0~100)
    humidityElement.textContent = `${currentHumidity}%`;

    console.log(`현재 습도: ${currentHumidity}%`);
}

// 초기화
document.addEventListener('DOMContentLoaded', () => {
    // setMode('auto'); // 초기 모드는 자동
    setInterval(updateCurrentHumidity, 3000); // 3초마다 현재 습도 갱신
});