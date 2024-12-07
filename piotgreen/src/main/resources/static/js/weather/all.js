document.addEventListener('DOMContentLoaded', function () {
    const tableBody = document.querySelector('table tbody');

    // MutationObserver 설정
    const observer = new MutationObserver(mutations => {
        mutations.forEach(() => {
            console.log("테이블 데이터가 변경되었습니다.");
            processTableData();
        });
    });

    // 테이블 tbody의 변경 사항 감지 시작
    observer.observe(tableBody, {
        childList: true, // 자식 노드 추가/제거 감지
        subtree: true,   // 하위 노드 변경 감지
    });

    // 초기 데이터 처리
    processTableData();

    function processTableData() {
        TYPE=["temperatureChart","windChart","cloudChart","weatherChart"];
        for (let i = 0; i < 4; i++) {
            const precipitationData = [];
            tableBody.querySelectorAll('tr').forEach(row => {
                const precipitationCell = row.cells[2+i]; // 강수확률 열
                if (precipitationCell) {
                    precipitationData.push(precipitationCell.textContent.trim());
                }
            });

            console.log("데이터:", precipitationData);

            renderPrecipitationChart(precipitationData,TYPE[i]);
        }
    }

    function renderPrecipitationChart(data,type) {
        console.log("차트를 그릴 데이터:", data);
        // 차트 업데이트 로직 추가
        renderChart(data,type);
    }
});

// 도시 이름에 맞는 nx, ny 값 설정
function updateCoordinates() {
    var city = document.getElementById("city").value;
    var nxSelect = document.getElementById("nx");
    var nySelect = document.getElementById("ny");

    // nx, ny 값 초기화
    nxSelect.innerHTML = "";
    nySelect.innerHTML = "";

    var coordinates = [];

    // 도시별 nx, ny 값 정의
    if (city === "Seoul") {
        coordinates = [{ nx: "60", ny: "127" }];
    } else if (city === "Busan") {
        coordinates = [{ nx: "98", ny: "76" }];
    } else if (city === "Incheon") {
        coordinates = [{ nx: "55", ny: "124" }];
    } else if (city === "Daegu") {
        coordinates = [{ nx: "89", ny: "90" }];
    }
    else if (city==="Gwangju"){
        coordinates = [{ nx: "58", ny: "74" }];
    }
    else if (city==="Daejeon"){
        coordinates = [{ nx: "67", ny: "100" }];
    }
    else if (city==="Ulsan"){
        coordinates = [{ nx: "102", ny: "84" }];
    }
    else if (city==="Gyeonggi"){
        coordinates = [{ nx: "60", ny: "120" }];
    }
    else if (city==="Gangwon"){
        coordinates = [{ nx: "73", ny: "134" }];
    }
    else if (city==="Chungbuk"){
        coordinates = [{ nx: "69", ny: "107" }];
    }
    else if (city==="Chungnam"){
        coordinates = [{ nx: "68", ny: "100" }];
    }
    else if (city==="Jeonbuk"){
        coordinates = [{ nx: "63", ny: "89" }];
    }
    else if (city==="Jeonnam"){
        coordinates = [{ nx: "51", ny: "67" }];
    }
    else if (city==="Gyeongbuk"){
        coordinates = [{ nx: "87", ny: "106" }];
    }
    else if (city==="Gyeongnam"){
        coordinates = [{ nx: "91", ny: "77" }];
    }
    else if (city==="Jeju"){
        coordinates = [{ nx: "52", ny: "38" }];
    }

    // nx, ny 선택 옵션 추가
    coordinates.forEach(function(coord) {
        var nxOption = document.createElement("option");
        nxOption.value = coord.nx;
        nxOption.textContent = coord.nx;
        nxSelect.appendChild(nxOption);

        var nyOption = document.createElement("option");
        nyOption.value = coord.ny;
        nyOption.textContent = coord.ny;
        nySelect.appendChild(nyOption);
    });

    // 기본적으로 첫 번째 값 선택
    if (coordinates.length > 0) {
        nxSelect.value = coordinates[0].nx;
        nySelect.value = coordinates[0].ny;
    }
}

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