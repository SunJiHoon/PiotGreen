function goToParent() {
    window.location.href = "../../";
}


document.getElementById("sendDangerMessage").addEventListener("click", () => {
    const url = "/manage/api/send-danger-message"; // 서버의 엔드포인트
    const payload = {
        message: "위험 발생",
        timestamp: new Date().toISOString()
    };

    fetch(url, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(payload)
    })
        .then(response => {
            if (response.ok) {
                alert("Danger message sent successfully!");
            } else {
                alert("Failed to send danger message.");
            }
        })
        .catch(error => {
            console.error("Error sending danger message:", error);
            alert("An error occurred while sending the danger message.");
        });
});
