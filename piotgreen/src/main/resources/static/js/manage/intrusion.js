const videoElement = document.getElementById("video");
const socket = new WebSocket("/video-stream"); // WebSocket 서버 경로

socket.binaryType = "arraybuffer"; // 이진 데이터로 WebSocket 메시지 수신

socket.onopen = () => {
    console.log("WebSocket connection established");
};

socket.onmessage = (event) => {
    const blob = new Blob([event.data], { type: 'image/jpeg' }); // 프레임을 Blob으로 변환
    const url = URL.createObjectURL(blob); // Blob을 URL로 변환
    videoElement.src = url; // 이미지의 src로 설정
};

socket.onerror = (error) => {
    console.error("WebSocket error:", error);
};

socket.onclose = () => {
    console.log("WebSocket connection closed");
};

function goBack() {
    window.history.back();
}
