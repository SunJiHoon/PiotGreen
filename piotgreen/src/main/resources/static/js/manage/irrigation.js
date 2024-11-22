// 초기 상태 설정
let isPumpActive = true; // true: 작동 중, false: 중단

// 펌프 상태 토글 함수
function togglePump() {
    // 상태 전환
    isPumpActive = !isPumpActive;

    // 상태를 반영하여 화면 업데이트
    const pumpStatusElement = document.getElementById('led-status');
    pumpStatusElement.textContent = isPumpActive ? '작동 중' : '중단';

    // 콘솔에 상태 출력 (디버깅 용도)
    console.log(`펌프 상태: ${isPumpActive ? '작동 중' : '중단'}`);
}

// 모드 설정 함수 (수동/자동)
function setMode(mode) {
    const currentModeElement = document.getElementById('current-mode');
    const manualControls = document.querySelector('.manual-controls');

    // 모드에 따라 화면 업데이트
    currentModeElement.textContent = mode === 'auto' ? '자동' : '수동';

    // 수동 모드에서만 버튼 활성화
    manualControls.style.display = mode === 'manual' ? 'block' : 'none';

    // 콘솔에 모드 변경 출력 (디버깅 용도)
    console.log(`현재 모드: ${mode === 'auto' ? '자동' : '수동'}`);
}

// 초기 모드 설정
document.addEventListener('DOMContentLoaded', () => {
    setMode('auto'); // 초기 모드는 자동으로 설정
});
