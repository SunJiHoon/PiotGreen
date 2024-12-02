let chartInstance = [null,null,null,null];
let TYPE=["temperatureChart","windChart","cloudChart","weatherChart"];
let CHART_NAME=["기온","풍속","구름","강수 확률"];
let LINE_COLOR=["rgba(255, 99, 132, 1)","rgba(54, 162, 235, 1)","rgba(255, 206, 86, 1)","rgba(75, 192, 192, 1)"];

function goToParent() {
    window.location.href = "../../";
}


// Chart.js를 사용하여 그래프 그리기
function renderChart(data, type) {
    const ctx = document.getElementById(type).getContext('2d');
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

    let index=TYPE.indexOf(type);

    // 기존 차트가 있으면 파괴
    if (chartInstance[index]) {
        chartInstance[index].destroy();
    }


    chartInstance[index] = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: CHART_NAME[index],
                data: values,
                borderColor: LINE_COLOR[index], // 라인 색상
                borderWidth: 2, // 라인 두께
                pointRadius: 0, // 데이터 포인트 원 크기 0으로 설정
                pointHoverRadius: 0, // 마우스 오버 시에도 포인트가 나타나지 않도록 설정
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
                    text: `${CHART_NAME[index]} 차트`,
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