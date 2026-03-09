# Langdy Backend Task
Spring Boot 기반으로 구현했으며, 다음 기능을 포함합니다.
- TASK #1: 수업 가능한 선생님 목록 조회
- TASK #2: 수업 신청
- TASK #3: 수업 신청 이후 알림 발송 설계

---

## 기술 스택
- Kotlin
- Spring Boot 3.5.11
- Spring Web
- Spring Data JPA
- QueryDSL
- H2
- MySQL
- Gradle

---

## 패키지 구조
```text
src/main/kotlin/com/langdy/langdy_task
├── controller
│   ├── request
│   ├── response
│   ├── LessonController.kt
│   └── TeacherController.kt
├── entity
│   ├── enums
│   ├── Course.kt
│   ├── Lesson.kt
│   ├── Student.kt
│   └── Teacher.kt
├── global
│   ├── config
│   ├── exception
│   ├── policy
│   └── response
├── notification
├── repository
├── service
│   ├── command
│   ├── LessonService.kt
│   └── TeacherAvailabilityService.kt
└── LangdyTaskApplication.kt
```

### 패키지 역할
- `controller`
    - API 엔드포인트 정의
    - request / response DTO 관리

- `entity`
    - 도메인 엔티티 정의
    - enum 관리

- `repository`
    - JPA Repository 및 QueryDSL 기반 조회 로직

- `service`
    - 비즈니스 로직 처리
    - `command` 패키지를 통해 Controller DTO와 분리

- `global`
    - 공통 설정, 정책, 예외 처리, 공통 응답

- `notification`
    - 알림 발송 관련 설계 클래스

---

## 실행 방법

### 1. 기본 실행 (H2)
기본 프로필은 H2 기반으로 구성되어 있어 별도 DB 설치 없이 바로 실행할 수 있습니다. 

애플리케이션 실행 후 H2 Console 접속: `http://localhost:8080/h2-console`

기본 설정:
- JDBC URL: `jdbc:h2:mem:langdy`
- User Name: `sa`
- Password: 없음

### 2. MySQL 실행 (prod 프로필)
운영 환경을 고려하여 MySQL 기반 `prod` 프로필을 별도로 구성했습니다.
현재 설정은 로컬 환경에서 쉽게 테스트할 수 있도록 구성했습니다.

---

## API 명세

### TASK #1 - 수업 가능한 선생님 목록 조회
특정 `courseId`와 `startAt` 기준으로 해당 시간에 수업 가능한 선생님 목록을 조회합니다.

#### Request
```http
GET /teachers/available?courseId=1&startAt=2026-03-06T10:00:00
```

#### Query Params
| Name     | Type          | Description         |
| -------- | ------------- | ------------------- |
| courseId | Long          | 코스 ID               |
| startAt  | LocalDateTime | 수업 시작 시각 |

#### Response
```json
{
  "courseId": 1,
  "startAt": "2026-03-06T10:00:00",
  "teachers": [
    {
      "id": 2,
      "name": "teacher2"
    },
    {
      "id": 3,
      "name": "teacher3"
    }
  ]
}
```

### TASK #2 - 수업 신청
학습자가 특정 코스와 선생님을 선택하여 수업을 신청합니다.

#### Request
```http
POST /lessons
X-Student-Id: 1
Content-Type: application/json
```

#### Request Body
```json
{
  "courseId": 1,
  "teacherId": 2,
  "startAt": "2026-03-06T10:00:00"
}
```

#### Header
| Name         | Type | Description |
| ------------ | ---- | ----------- |
| X-Student-Id | Long | 인증된 학습자 ID  |

#### Response
```json
{
  "lessonId": 1
}
```

#### Error Response Example
```json
{
  "timestamp": "2026-03-06T10:00:00",
  "message": "Teacher already booked",
  "path": "/lessons"
}
```

---

## 주요 구현 내용

### TASK #1 - 수업 가능한 선생님 목록 조회
특정 시간에 이미 `BOOKED` 상태의 lesson이 존재하는 teacher를 제외하여 조회합니다.

#### 조회 방식
- 특정 시간의 예약 lesson 조회
- 해당 시간에 예약이 없는 teacher만 조회

---

### TASK #2 - 수업 신청
수업 신청 시 다음 정책을 검증합니다.
- 수업 시작 시간은 매시 `00분`, `30분`만 가능
- 수업 길이는 20분 고정
- teacher는 동일 시간에 하나의 lesson만 가능
- student는 동일 시간에 하나의 lesson만 가능

#### 시간 정책
`TimePolicy`를 통해 다음 도메인 규칙을 검증합니다.

- `startAt.minute == 0 || startAt.minute == 30`
- `startAt.second == 0 && startAt.nano == 0`
- `endAt = startAt + 20분`

#### 동시성 처리 전략
문제 조건 상 서버는 여러 인스턴스 환경에서 동작한다고 가정했습니다.
따라서 애플리케이션 레벨의 사전 조회만으로는 중복 예약을 완전히 방지할 수 없다고 판단했습니다.

이를 위해 DB 레벨에서 다음 제약을 설정했습니다.
- `uk_teacher_start (teacher_id, start_at)`
- `uk_student_start (student_id, start_at)`

또한 DB 제약 충돌 발생 시 `DataIntegrityViolationException`을 처리하여 일관된 에러 응답을 반환하도록 구성했습니다.

#### 예외 처리
비즈니스 예외 처리를 위해 공통 예외 처리 구조를 적용했습니다.
- `BusinessException`
- `GlobalExceptionHandler`
- `ErrorResponse`

이를 통해 예외 발생 시 API 응답 형식을 일관되게 유지했습니다.

---

## TASK #3 - 알림 발송 설계
수업 신청 완료 이후 알림 발송은 핵심 비즈니스 로직과 분리하여 이벤트 기반 구조로 설계했습니다.

### 설계 의도
- 수업 생성과 알림 발송 책임 분리
- 알림 실패가 수업 생성 트랜잭션에 영향을 주지 않도록 처리

### 구현 방식 1 - Spring Application Event
1. LessonService에서 lesson 생성 완료
2. publishNotification() 호출
3. 이벤트 생성
4. ApplicationEventPublisher를 통해 이벤트를 발행
5. Listener에서 @TransactionalEventListener(phase = AFTER_COMMIT)를 사용하여 트랜잭션 커밋 이후 알림 발송

### 구현 방식 2 - Transactional Outbox + Message Broker
1. LessonService에서 lesson 생성 완료
2. publishNotification() 호출
3. 이벤트 생성
4. Transactional Outbox 패턴을 사용하여 이벤트를 DB에 저장
5. 별도의 프로세스를 통해 메시지 브로커(Kafka / RabbitMQ / SQS 등)로 전달
6. Consumer가 이벤트를 소비하여 알림 발송

---

## 테스트
단위 테스트를 통해 주요 서비스 로직을 검증했습니다.

### 테스트 대상
- `TeacherAvailabilityServiceTest`
  - 수업 가능한 선생님 목록 조회

- `LessonServiceTest`
  - 정상 수업 생성
  - 선생님 중복 예약 예외
  - 학생 중복 예약 예외
  - 잘못된 수업 시작 시간 예외

---

## 구현 시 고려한 점
* H2를 기본 실행 환경으로 구성하여 빠르게 테스트 가능하도록 했습니다.
* MySQL `prod` 프로필을 별도로 구성하여 운영 환경을 고려했습니다.
* 멀티 인스턴스 환경을 고려해 DB Unique Constraint 기반 동시성 제어를 적용했습니다.
* 알림 발송은 이벤트 기반 구조로 설계하였습니다.

