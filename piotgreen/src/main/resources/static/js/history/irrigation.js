let currentMonth = new Date().getMonth() + 1;
let currentYear = new Date().getFullYear();
let chartInstance = null; // Chart 인스턴스 변수

// 초기 데이터 가져오기
fetchAndRenderData();

// 달 변경
function changeMonth(offset) {
    currentMonth += offset;
    if (currentMonth > 12) {
        currentMonth = 1;
        currentYear++;
    } else if (currentMonth < 1) {
        currentMonth = 12;
        currentYear--;
    }

    const monthText = `${currentYear}년 ${currentMonth}월`;
    document.getElementById('current-month').innerText = monthText;
    fetchAndRenderData();
}

// 데이터 가져오기 및 그래프 업데이트
function fetchAndRenderData() {
    fetch(`/irrigation/data?year=${currentYear}&month=${currentMonth}`)
        .then(response => response.json())
        .then(data => renderChart(data))
        .catch(error => console.log('Error fetching data:', error));
}

// Chart.js를 사용하여 그래프 그리기
function renderChart(data) {
    const ctx = document.getElementById('moistureChart').getContext('2d');
    const labels = data.map(item => item.day);
    const values = data.map(item => item.averageMoisture);
    // 기존 차트가 있으면 파괴
    if (chartInstance) {
        chartInstance.destroy();
    }


    chartInstance = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: '일 평균 습도량',
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
                    text: '일 평균 습도량 차트',
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

function goBack() {
    window.history.back();
}
