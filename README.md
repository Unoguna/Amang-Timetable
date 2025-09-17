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

---

# 🛠️ 트러블슈팅: 예약자명이 화면에 표시되지 않는 문제

## 📍 문제 상황
- 예약 입력 후 DB에는 정상적으로 데이터가 저장됨 (`select * from reservation;` 조회 확인 완료).
- Controller 로그에서도 예약 데이터가 잘 불러와져 `reservationMap`에 채워짐.
- 하지만 Thymeleaf 템플릿에서 예약자명이 표시되지 않고, 항상 `<input>` 칸만 보임.

---

## 🔍 원인 분석
1. **`reservationMap` 키 타입 불일치**
   - Controller에서 `reservationMap`을 다음과 같이 구성:
     ```java
     Map<String, Map<Integer, List<Reservation>>> reservationMap
     ```
     - 첫 번째 키: 날짜 `"2025-09-03"` (문자열)
     - 두 번째 키: 시간 `13` (Integer)

   - 템플릿에서는 다음과 같이 접근:
     ```html
     reservationMap[day][h]
     ```
     - `day` → LocalDate 객체
     - `h` → Integer 변수

   - → Map의 키와 템플릿에서 전달하는 타입이 맞지 않아 항상 `null` 반환됨.

2. **Thymeleaf SpEL의 변수 처리 특성**
   - `reservationMap['2025-09-03'][13]` → **리터럴**로 접근 시 정상 동작.
   - `reservationMap[dateKey][h]` → **변수**로 접근 시 타입 매칭이 엄격해 null 반환.
   - int → Integer auto-boxing / SpEL 변환 과정 문제

---

## ✅ 해결 방법

### 1. Controller에서 키를 문자열로 변환
```java
String dateKey = r.getDate().toString(); // "2025-09-03"
String hourKey = String.valueOf(r.getStartTime().getHour()); // "13"

reservationMap
    .computeIfAbsent(dateKey, k -> new HashMap<>())
    .computeIfAbsent(hourKey, k -> new ArrayList<>())
    .add(r);
```

### 2. HTML에서 문자열 키로 접근
```html
<div th:with="
    dateKey=${#temporals.format(day, 'yyyy-MM-dd')},
    hourKey=${h.toString()},
    slotList=${reservationMap[dateKey][hourKey]}">

  <!-- 예약 있음 -->
  <span th:if="${!#lists.isEmpty(slotList)}"
        class="tag"
        th:text="${slotList.get(0).reserver}"></span>

  <!-- 예약 없음 -->
  <input th:if="${#lists.isEmpty(slotList)}"
         class="slot-input"
         th:data-date="${dateKey}"
         th:data-hour="${hourKey}"
         placeholder="예약자 입력"/>
</div>

```

## 💡 배운 점

- Map 키 타입과 템플릿에서 접근하는 변수 타입은 반드시 일치해야 한다.

   - LocalDate ↔ String, Integer ↔ String 불일치 시 null 반환.

- SpEL에서 변수 기반 Map 키 접근은 타입 매칭이 엄격하다.

- 복잡한 중첩 Map 대신 `"yyyy-MM-dd|HH"` 같은 단일 문자열 키 구조를 쓰는 것도 유지보수에 유리하다.

