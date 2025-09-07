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

