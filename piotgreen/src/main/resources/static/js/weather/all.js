let chartInstance = null;

function goToParent() {
    window.location.href = "../../";
}


// Chart.js를 사용하여 그래프 그리기
function renderChart(data) {
    const ctx = document.getElementById('weatherChart').getContext('2d');
    // const labels = data.map(item => item.day);
    // const values = data.map(item => item.averageMoisture);
    // const labels = data.map((_, index) => `${index + 1}시간`); // x축 레이블 예: 1시간, 2시간 ...
    const startHour = 3; // 시작 시간 (03시)
    const currentDate = new Date(); // 현재 날짜

    const labels = data.map((_, index) => {
        const hour = (startHour + index) % 24; // 24시간 형식으로 시간 계산
        const isNextDay = Math.floor((startHour + index) / 24); // 날짜가 넘어갔는지 확인
        const displayDate = new Date(currentDate); // 현재 날짜 복사
        displayDate.setDate(currentDate.getDate() + isNextDay); // 넘어간 날짜만큼 추가
        const displayMonth = String(displayDate.getMonth() + 1).padStart(2, '0');
        const displayDay = String(displayDate.getDate()).padStart(2, '0');
        return `${displayMonth}월${displayDay}일 ${String(hour).padStart(2, '0')}시`;
    });
    const values = data.map(value => parseInt(value, 10)); // 강수확률 값
    // 기존 차트가 있으면 파괴
    if (chartInstance) {
        chartInstance.destroy();
    }


    chartInstance = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: '강수확률',
                data: values,
                backgroundColor: [
                    'rgba(255, 99, 132, 0.2)', // Red
                    'rgba(54, 162, 235, 0.2)', // Blue
                    'rgba(255, 206, 86, 0.2)', // Yellow
                    'rgba(75, 192, 192, 0.2)', // Green
                    'rgba(153, 102, 255, 0.2)', // Purple
                    'rgba(255, 159, 64, 0.2)'  // Orange
                ],
                borderColor: [
                    'rgba(255, 99, 132, 1)', // Red
                    'rgba(54, 162, 235, 1)', // Blue
                    'rgba(255, 206, 86, 1)', // Yellow
                    'rgba(75, 192, 192, 1)', // Green
                    'rgba(153, 102, 255, 1)', // Purple
                    'rgba(255, 159, 64, 1)'  // Orange
                ],
                borderWidth: 2
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: true,
                    labels: {
                        font: {
                            size: 14,
                            family: 'Arial',
                            style: 'bold',
                        },
                        color: '#333' // Legend text color
                    }
                },
                title: {
                    display: true,
                    text: '강수 확률 차트',
                    font: {
                        size: 20,
                        family: 'Arial',
                        weight: 'bold',
                    },
                    color: '#111' // Title color
                },
                tooltip: {
                    backgroundColor: 'rgba(0,0,0,0.7)',
                    titleColor: '#fff',
                    bodyColor: '#fff',
                    borderColor: '#fff',
                    borderWidth: 1,
                    callbacks: {
                        label: function(context) {
                            return `일광량: ${context.raw}`;
                        }
                    }
                }
            },
            scales: {
                x: {
                    grid: {
                        color: 'rgba(200, 200, 200, 0.2)',
                    },
                    ticks: {
                        color: '#666',
                        font: {
                            size: 12
                        }
                    }
                },
                y: {
                    beginAtZero: true,
                    grid: {
                        color: 'rgba(200, 200, 200, 0.2)',
                    },
                    ticks: {
                        color: '#666',
                        font: {
                            size: 12
                        }
                    }
                }
            },
            animation: {
                duration: 1500,
                easing: 'easeInOutBounce'
            }
        }
    });

}