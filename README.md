# PiotGreen - IoT 농업 시스템

**팀명**: Team #3  
**프로젝트 명**: PiotGreen  
**과목**: 고급 IoT 프로젝트, 2024년 2학기  
**교수님**: 박상오 교수님  
**팀원**:
-  선지훈
-  채현서
-  정용희
-  김주영

## 목차
1. [소개](#소개)
2. [프로젝트 개요](#프로젝트-개요)
3. [개발 및 구현 내용](#개발-및-구현-내용)
4. [주간 진행 계획](#주간-진행-계획)

---

## 소개

### 1.1 프로젝트 주제
PiotGreen은 Raspberry Pi를 활용하여 IoT 기술을 농업에 적용한 스마트 농업 시스템입니다. 다양한 센서와 Raspberry Pi를 통해 농작물의 생장 환경을 실시간으로 모니터링하고 자동으로 필요한 조치를 취할 수 있도록 설계되었습니다. 주요 기능으로는 야생동물 침입 감지, 토양 수분 관리, 광량 제어가 있으며, 이를 통해 자원의 효율적 활용 및 수확량 증대를 목표로 하고 있습니다.

### 1.2 팀 이름
"PiotGreen"은 다음의 세 가지 요소를 결합한 합성어입니다:
- **Pi**: 프로젝트의 중심 기술인 Raspberry Pi를 의미하며, 센서 연동 및 데이터 처리를 담당합니다.
- **IoT**: Internet of Things의 약자로, 스마트 농업 시스템의 연결성과 데이터 수집 기능을 나타냅니다.
- **Green**: 농업과 환경을 상징하는 녹색을 의미하며, 프로젝트의 목표인 지속 가능하고 효율적인 농업을 반영합니다.

### 1.3 팀원 및 역할 분담
- **선지훈**: 각 Raspberry Pi에서 수집된 신호를 LCD에 표시하고 제어 화면을 개발
- **채현서**: 카메라를 이용한 야생동물 침입 감지 시스템 개발
- **정용희**: 토양 수분 모니터링 및 자동 관수 시스템 개발
- **김주영**: 조도 센서와 LED를 활용한 광량 제어 시스템 개발

---

## 프로젝트 개요

### 2.1 프로젝트 개요
PiotGreen 프로젝트는 농업의 생산성과 효율성을 높이기 위해 IoT 기술을 접목한 스마트 농업 시스템입니다. Raspberry Pi와 다양한 센서를 통해 작물 재배 환경을 실시간으로 모니터링하고 자동 관리가 가능하도록 설계되었습니다. 사용자는 실시간으로 데이터를 확인하고 야생동물 침입 감지, 토양 수분 관리, 광량 조절 등의 기능을 통해 편리하게 농업 관리를 할 수 있습니다.

![image](https://github.com/user-attachments/assets/e6d98615-1bcd-4cdd-ba4a-006804013d73)


### 2.2 프로젝트 동기
기후 변화와 노동력 감소로 농업의 디지털 전환이 요구되고 있습니다. 특히 소규모 농장에서는 고가의 스마트 농업 장비 도입이 어렵기 때문에, 경제적이면서도 기능적인 IoT 농업 솔루션의 필요성이 증가하고 있습니다. Raspberry Pi와 저비용 센서를 활용하여 누구나 접근할 수 있는 IoT 농업 시스템을 개발하여 농업 관리의 부담을 줄이고 생산성을 높이고자 합니다.

### 2.3 프로젝트 목표
PiotGreen 프로젝트의 주요 목표는 다음과 같습니다:
- **실시간 모니터링**: 온도, 습도, 광량 등의 요소를 실시간으로 확인하여 신속하게 대응 가능
- **자동화 관리**: 토양 수분 상태에 따라 자동으로 관수하고 광량을 조절하여 최적의 생장 환경 유지
- **경제적 솔루션**: 저비용 센서와 Raspberry Pi를 사용하여 소규모 농가에서도 쉽게 도입 가능
- **지속 가능한 농업**: 자원 효율성을 높이고 데이터 기반의 지속 가능한 농업 지원

---

## 개발 및 구현 내용

### 3.1 개발 접근 방식
PiotGreen은 기능별로 모듈화된 구조를 채택하여, 독립적으로 개발된 기능을 메인 Raspberry Pi에서 통합 관리하는 방식입니다. 야생동물 침입 감지, 토양 수분 모니터링, 광량 제어 등의 기능을 담당하는 Raspberry Pi가 각각 배정되어 있으며, 메인 Raspberry Pi가 이 데이터를 수집하여 LCD 화면과 웹페이지에 실시간으로 표시하는 사용자 인터페이스를 제공합니다.

### 3.2 기술 스택 및 도구
- **하드웨어**: Raspberry Pi, 카메라 모듈, 토양 수분 센서, 조도 센서, LCD 디스플레이, LED
- **프로그래밍 언어**: Java/Spring(웹), Python(센서 제어), C(소켓 통신)
- **데이터 통신**: 소켓 통신 (C로 구현)
- **데이터 저장**: MySQL
- **개발 툴**: Visual Studio, Vim, PyCharm, IntelliJ IDEA
- **라이브러리**:
  - RPi.GPIO (Raspberry Pi 핀 제어)
- **웹 관리 페이지**: Java, Spring Framework

### 3.3 주요 기능
- **야생동물 침입 감지**: 카메라 모듈과 OpenCV로 모션을 감지하고 침입 시 알림
- **토양 수분 모니터링 및 자동 관수**: 토양 수분 상태 모니터링 및 자동 관수
- **광량 제어**: 조도 센서로 광량 측정 및 부족 시 LED 조명 활성화
- **통합 제어 화면**: LCD 화면에 각 기능 데이터를 통합하여 실시간 표시

### 3.4 디렉터리 구조
각 디렉터리는 팀원들이 담당하는 시스템 기능에 따라 분리되어 있으며, `piotgreen` 디렉터리는 Spring Boot 기반의 웹 애플리케이션을 구성합니다. 주요 기능들은 다음과 같습니다:

- **intrusion_detection**: 야생동물 침입 감지 시스템 코드
- **irrigation_system**: 토양 수분 모니터링 및 자동 관수 시스템 코드
- **lighting_control**: 광량 제어 시스템 코드
- **piotgreen**: Spring Boot를 활용한 웹 애플리케이션으로, 각 기능의 통합 관리 및 사용자 인터페이스 제공

이 구조를 통해 PiotGreen 프로젝트는 확장성과 유지 보수성을 고려하여 개발되었습니다.


# 사용자 관점에서의 주요 기능 소개

## 주요 기능

### 서브 라즈베리 파이 제어 기능 (웹)
- **실시간 데이터 조회**: 현재 광원량, 흙 습도, 침입 감지 여부 등을 확인 가능.

![image](https://github.com/user-attachments/assets/7c7dae74-96c0-460a-8ea1-33681769d078)


- **관리하기 기능**:

![image](https://github.com/user-attachments/assets/f7c04455-e297-4db0-af22-e6c35cb084d7)


  - **광량 데이터 조회 및 LED 자동 제어**:
    - LED On/Off 수동 제어.

![image](https://github.com/user-attachments/assets/125161f7-589b-4824-b8f3-9f35201a0670)

![image](https://github.com/user-attachments/assets/cea2f5e0-2a5b-4284-bd54-88ca16d47d06)


  - **관수 데이터 조회 및 자동 제어**:
    - 관수 수동 제어.
   
![image](https://github.com/user-attachments/assets/19cf747d-bd72-46c2-8ff2-343762d46a2f)

![image](https://github.com/user-attachments/assets/fe1e5e28-0ff4-440e-8292-7607d89979cf)

   

  - **영상 데이터 조회**.
    - 위험 감지 기능 On/Off.

![image](https://github.com/user-attachments/assets/6a2b4776-5ab7-4f6c-a757-ad87de38c12f)

![image](https://github.com/user-attachments/assets/60687b81-bdcf-4d45-ac45-6fcc9ab70160)



- **제어 명령 예약하기**:

![image](https://github.com/user-attachments/assets/28665fe7-b7d1-48ba-bbb0-11d5e3dcad4f)

  - **광량 제어 명령어 예약**:
    - 예약 내역 확인.
  
![image](https://github.com/user-attachments/assets/d13a5ec0-77fa-418c-8421-bf0e559817d3)

![image](https://github.com/user-attachments/assets/88c41a80-c2b1-4fee-bab7-391fe9e38da1)


  - **관수 제어 명령어 예약**:
    - 예약 내역 확인.
   
![image](https://github.com/user-attachments/assets/03afc3dd-db14-4adc-a058-82e439b224b7)

![image](https://github.com/user-attachments/assets/889c086b-5404-4774-b64d-8e2f6930b703)


  
  - **침입 감지 관련 명령어 예약**:
    - 예약 내역 확인.

![image](https://github.com/user-attachments/assets/881d68cf-8463-43d5-9726-2853bfc27899)

![image](https://github.com/user-attachments/assets/3c3f9fc1-3bc5-4f30-84b6-21b63234c612)

  
- **기록 조회하기**:

![image](https://github.com/user-attachments/assets/8ad2a6b2-1b4b-4e23-a1ce-330e372a27bf)

  - **감지한 광량 기록 차트**.

![image](https://github.com/user-attachments/assets/bc076a83-e525-49c8-a591-e692a90f7994)

  - **감지한 광량 기록**.

![image](https://github.com/user-attachments/assets/a4f8a205-1886-41ae-a7e7-e0abc3898fa9)

  - **감지한 습도량 기록 차트**.

![image](https://github.com/user-attachments/assets/95801b9e-d64e-483c-9334-c8b3a95975ab)

  - **감지한 습도량 기록**.

![image](https://github.com/user-attachments/assets/7a1de142-ac50-44b2-97df-4672b70d3ac1)

  - **감지한 위험 기록**.

![image](https://github.com/user-attachments/assets/77aeeea0-b6db-40d6-912f-d2919fc0afc4)


- **날씨 조회하기**

![image](https://github.com/user-attachments/assets/80ebc101-3dbb-4391-b954-fb2df72a45ac)

  - **지역별 날씨 조회 창**.

![image](https://github.com/user-attachments/assets/10841bc0-31d4-42b9-824a-34e281c5fa7f)

  - **날씨 정보에 따른 차트**.

![image](https://github.com/user-attachments/assets/56183b6f-5f84-4a35-a9e4-eb7c1235c4df)

  - **날씨 정보의 시간별 상세 데이터**.

![image](https://github.com/user-attachments/assets/a9bdf542-a2e2-4a22-ad20-31224b557513)


### 주요 시스템
- **카메라를 이용한 야생동물 침입 감지 시스템**.

![image](https://github.com/user-attachments/assets/6bd3c8c6-f3e0-4739-8bc1-099fd060584b)

![image](https://github.com/user-attachments/assets/3661e602-3dea-4fce-8d1f-6c5a7783a497)

- **토양 수분 모니터링 및 자동 관수 시스템**.
- **조도 센서와 LED를 활용한 광량 제어 시스템**.

![image](https://github.com/user-attachments/assets/3f6c78d0-8453-4ada-a092-0ecf8692659b)

![image](https://github.com/user-attachments/assets/a5acb801-c362-4ca4-bc18-197614732a39)

---

# 결론 및 향후 과제

## 결론
PiotGreen 프로젝트는 스마트 농업 관리의 가능성을 성공적으로 입증하였습니다. 본 프로젝트는 저비용으로 효과적인 농업 자동화 시스템을 구현함으로써 생산성과 효율성을 크게 향상시킬 수 있음을 확인하였습니다. 특히, 라즈베리파이와 IoT 기술을 활용하여 실시간 환경 데이터를 모니터링하고, 자동화된 조명 및 관수 시스템을 통해 작물의 최적 성장 환경을 유지할 수 있도록 설계되었습니다.

이 과정에서 하드웨어와 소프트웨어 간의 유기적인 연동을 성공적으로 구현하였으며, 이는 향후 다양한 농업 환경에 적용할 수 있는 유연한 시스템 개발의 가능성을 열어주었습니다. 농작물의 생장 환경을 실시간으로 모니터링하고, 자동으로 필요한 조치를 취할 수 있도록 설계했고, 본래의 목표를 모두 이루었습니다.

## PiotGreen 팀

---

# 주간 진행 일정

| 주차   | 주간 목표                                  | 역할 및 상세 계획                                                                       |
| ------ | ----------------------------------------- | --------------------------------------------------------------------------------------- |
| 11주차 | Raspberry Pi 간 통신을 위한 소켓 개발      | **선지훈**: TCP 클라이언트 및 서버 제작 후 스프링 웹 소켓 연동. 소켓 통신 후 받은 내용 바탕으로 웹 상에서 출력.<br>**채현서**: 시제품 외관 모델링 및 출력.<br>**정용희**: TCP 소켓 이용해서 메인 서버와 연결 및 테스트.<br>**김주영**: 소켓 연결 및 센서 테스트(조도센서, LED). |
| 12주차 | 소켓 통신을 통한 데이터 전달 및 Sub Raspberry Pi의 하드웨어 동작 구현 | **선지훈**: 소켓 통신 후 받은 내용을 LCD로 출력. 기존 개발한 소켓을 이용하여 웹 인터페이스 제작 (흙 수분 정보와 일조량 정보 실시간 확인 기능).<br>**채현서**: 카메라 연결 후 실시간 영상 전송(웹).<br>**정용희**: 수분 센서를 이용해서 토양 수분 측정 테스트 및 서보모터를 이용한 워터펌프 연결 테스트.<br>**김주영**: 데이터베이스 설계 및 수동/자동 모드 구현 (수동: 메인 서버에서 받은 지시 사항, 자동: 조도 센서를 통해 광량 자동 조절). |
| 13주차 | 메인 Raspberry Pi 를 통해 Sub Raspberry Pi 제어 기능 개발 (통합 시스템) | **선지훈**: 각 라즈베리 파이 제어 기능 개발. 실시간 영상 조회 기능 개발.<br>**채현서**: 영상에서 객체 인식 (OpenCV 객체 인식 기초 학습).<br>**정용희**: 메인 서버에 주기적으로 수분량 업로드, 서버에서 요청 시 현재 수분량 응답 및 명령 수행.<br>**김주영**: 농장을 구간별로 나누어 조도 센서 설치 및 특정 구간 LED 조명 제어. |
| 14주차 | 유저 편의성 증대 및 Sub Raspberry Pi 기능 심화 개발 (1) | **선지훈**: 지난 기록 데이터 조회 (과거 흙 수분 정보와 일조량 정보) 기능 개발. 유저 편의를 위해 웹에서 실제 날씨 데이터 제공.<br>**채현서**: 영상에서 동물 객체 인식 심화.<br>**정용희**: 날씨 데이터셋에 따른 관수량 조절.<br>**김주영**: 웹 인터페이스 프론트 구성 (날씨 구성 관련 컴포넌트). |
| 15주차 | 유저 편의성 증대 및 Sub Raspberry Pi 기능 심화 개발 (2) | **선지훈**: IoT 제어 기능에 예약 기능 추가 (#월 #일부터 #월 #일까지 일조량 조절 기능).<br>**채현서**: 동물 침입 시 알림(소켓 통신) 및 물리적 대응 방법 개발.<br>**정용희**: 메인 서버와 통신하여 받은 날씨 정보를 바탕으로 관수량 조절 (자동 관수 시스템 개발).<br>**김주영**: 웹 인터페이스 프론트 구성 (달력 기능 관련 컴포넌트). |
