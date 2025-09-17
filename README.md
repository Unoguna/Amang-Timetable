# 🏫 동아리방 예약 타임테이블

Spring Boot + Thymeleaf 기반으로 구현한 **동아리방 예약 관리 서비스**입니다.
엑셀처럼 달력에서 직접 시간 칸을 클릭/입력하여 예약을 등록·수정할 수 있습니다.

---

## ✨ 주요 기능

* 📅 **달력 기반 UI**

  * 현재 월 + 이전/다음 월 네비게이션
  * 달력은 **해당 월 전체 + 전후 주(옆달 날짜)** 까지 표시
* 🕔 **시간 단위 슬롯**

  * 매일 **05:00 \~ 24:00**까지 1시간 단위 슬롯 제공
* ✍️ **인라인 예약 입력**

  * 빈 칸 → 예약자 입력 가능
  * 이미 예약된 칸도 수정 가능 (DB 업데이트)
* 💾 **DB 연동**

  * H2 (in-memory) 또는 MySQL 연동 가능
  * `reservation` 테이블에 `date`, `startTime`, `endTime`, `reserver` 저장
  * 옆달 날짜(예: 8월 31일, 10월 1일)도 조회 가능
* 🔄 **자동 새로고침**

  * 입력 후 `Enter` 또는 `blur` 이벤트 시 DB 저장 → 화면 리로드

---

## 🛠️ 기술 스택

* **Backend** : Spring Boot 3, Spring Data JPA
* **Frontend** : Thymeleaf, Vanilla JS, CSS Grid/Flexbox
* **Database** : H2 (기본), MySQL (선택)

---

## 📂 프로젝트 구조

```
src
 └── main
     ├── java/com/amang_timetable/domain/reservation/reservation
     │   ├── controller/ReservationController.java
     │   ├── entity/Reservation.java
     │   ├── dto/ReservationForm.java
     │   ├── repository/ReservationRepository.java
     │   └── service/ReservationService.java
     └── resources
         ├── templates/reservations.html
         ├── static/css/style.css
         └── application.yml
```

---

## ⚙️ 실행 방법

### 1. 프로젝트 클론

```bash
git clone https://github.com/username/amang-timetable.git
cd amang-timetable
```

### 2. 실행

```bash
./mvnw spring-boot:run
```

### 3. 접속

```
http://localhost:8080/reservations
```

---

## 🗄️ DB 설정 (H2)

`application.yml`

```yaml
spring:
  application:
    name: Amang_timetable
  h2:
    console:
      enabled: true
      path: /h2-console
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

접속: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
JDBC URL: `jdbc:h2:mem:testdb`

---

## 📸 화면 예시

* 달력 뷰: 월/주/요일 단위 표시
* 시간 슬롯: 05:00 \~ 24:00
* 예약자 입력/수정: 셀에 직접 입력 후 Enter
