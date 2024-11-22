// 초기 데이터 설정
let currentMode = 'auto';
let ledStates = [0, 0, 0, 0, 0];

// 모드 변경 함수
function setMode(mode) {
    currentMode = mode;
    document.getElementById('current-mode').textContent = mode === 'auto' ? '자동' : '수동';

    // 수동 모드 버튼 활성화
    const manualControls = document.querySelector('.manual-controls');
    if (mode === 'manual') {
        manualControls.style.display = 'block';
    } else {
        manualControls.style.display = 'none';
    }
}

// LED 상태 토글 함수
function toggleLed(index) {
    if (currentMode !== 'manual') return; // 수동 모드에서만 활성화

    ledStates[index] = ledStates[index] === 0 ? 1 : 0; // 토글
    document.getElementById('led-status').textContent = ledStates.join(' ');
}

// 데이터 갱신 예제 (수신된 값으로 업데이트)
function updateData(lightLevel, ledArray) {
    document.getElementById('light-level').textContent = lightLevel;
    document.getElementById('led-status').textContent = ledArray.join(' ');
}

// 테스트 데이터 (실제 구현에서는 수신 데이터로 대체)
setInterval(() => {
    const fakeLightLevel = Math.floor(Math.random() * 1000); // 0~1000 범위
    const fakeLedArray = ledStates;
    updateData(fakeLightLevel, fakeLedArray);
}, 1000); // 1초마다 데이터 갱신