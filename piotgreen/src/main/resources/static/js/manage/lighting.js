// 초기 데이터 설정
let currentMode = 'auto';
// let ledStates = [0, 0, 0, 0, 0];


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

    // Spring 서버에 POST 요청 보내기
    fetch('/lighting/mode/set', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            mode: mode
        })
    })
        .then(data => {
            // 서버 응답 처리
            console.log('서버 응답:', data);

            // 추가적으로 데이터를 화면에 반영할 수 있음
            if (data.includes("mode:pass")) {
                console.log("수동 모드로 전환 성공");
                // 예시: 화면에 상태 업데이트
                document.getElementById('current-mode-display').textContent = "수동 모드 활성화됨";
            } else {
                console.warn("응답에 예상된 데이터가 포함되지 않음:", data);
            }
        })
        .catch(error => {
            console.error('POST 요청 실패:', error);
        });
}

// // LED 상태 토글 함수
// function toggleLed(section, index) {
//     // LED 상태 토글
//     ledSections[section][index] = ledSections[section][index] === 0 ? 1 : 0;
//
//     // 해당 LED 상태 업데이트
//     const ledElement = document.getElementById(`led-${section}-${index}`);
//     if (ledSections[section][index] === 1) {
//         ledElement.classList.remove('off');
//         ledElement.classList.add('on');
//     } else {
//         ledElement.classList.remove('on');
//         ledElement.classList.add('off');
//     }
// }

// LED 상태 토글 함수
function toggleLed(index) {
    if (currentMode !== 'manual') return; // 수동 모드에서만 활성화

    // ledStates[index] = ledStates[index] === 0 ? 1 : 0; // 토글
    // document.getElementById('led-status').textContent = ledStates.join(' ');
    let currState = 0;
    if(index === 0){
        const led1Element = document.getElementById('led-1-0'); // LED 요소 선택

        if (led1Element.classList.contains('on')) {
            currState = 1; // 켜짐 상태
        } else if (led1Element.classList.contains('off')) {
            currState = 0; // 꺼짐 상태
        }
    }
    else if(index===1){
        const led2Element = document.getElementById('led-2-0'); // LED 요소 선택

        if (led2Element.classList.contains('on')) {
            currState = 1; // 켜짐 상태
        } else if (led2Element.classList.contains('off')) {
            currState = 0; // 꺼짐 상태
        }
    }
    let nextState = currState === 0 ? 1 : 0; // 토글


    // Spring 서버에 POST 요청 보내기
    fetch('/lighting/led/toggle', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            index: index,
            // state: ledStates[index] // 토글된 상태를 전송
            state: nextState
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

// 데이터 갱신 예제 (수신된 값으로 업데이트)
function updateData(lightLevel, ledArray) {
    document.getElementById('light-level').textContent = lightLevel;
    document.getElementById('led-status').textContent = ledArray.join(' ');
}

// 테스트 데이터 (실제 구현에서는 수신 데이터로 대체)
setInterval(() => {
    const fakeLightLevel = Math.floor(Math.random() * 100); // 0~100 범위
    // const fakeLedArray = ledStates;
    // updateData(fakeLightLevel, fakeLedArray);
}, 1000); // 1초마다 데이터 갱신




// LED 상태를 업데이트하는 함수
function updateLedSections(data) {
    if (!Array.isArray(data) || data.length < 2) {
        console.error("유효하지 않은 LED 데이터 형식:", data);
        return;
    }

    // 섹션 1 LED 상태 업데이트
    const section1 = data[0];
    const led1_0 = document.getElementById('led-1-0');
    // const led1_1 = document.getElementById('led-1-1');

    // 섹션 2 LED 상태 업데이트
    const section2 = data[1];
    const led2_0 = document.getElementById('led-2-0');
    // const led2_1 = document.getElementById('led-2-1');

    // 섹션 1 LED 상태 반영
    updateLedState(led1_0, section1 === 1);
    // updateLedState(led1_1, section1 === 1);

    // 섹션 2 LED 상태 반영
    updateLedState(led2_0, section2 === 1);
    // updateLedState(led2_1, section2 === 1);
}

// LED 상태를 on/off로 업데이트하는 함수
function updateLedState(ledElement, isOn) {
    if (isOn) {
        ledElement.classList.remove('off');
        ledElement.classList.add('on');
    } else {
        ledElement.classList.remove('on');
        ledElement.classList.add('off');
    }
}

// 광량 섹션 업데이트 함수
function updateLightLevel(section, value) {
    if (section === 1) {
        document.getElementById('light-level-1').textContent = value;
    } else if (section === 2) {
        document.getElementById('light-level-2').textContent = value;
    }
}

const lightLevelElement = document.getElementById("light-level");
// const ledStatusElement = document.getElementById("led-status");
const socket = new SockJS('/websocket');
const stompClient = Stomp.over(socket);

// STOMP 클라이언트 연결
stompClient.connect({}, function () {
        console.log("STOMP 클라이언트 연결 성공");

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


    // 광원량 토픽에 구독
    stompClient.subscribe('/topic/lighting_control/led', function (message) {
        console.log("Received light level message:", message);

        if (message.body) {
            try {
                const data = JSON.parse(message.body);
                if (data !== undefined) {
                    // ledStatusElement.textContent = data;
                    console.log("Parsed LED data:", data);
                    updateLedSections(data);
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