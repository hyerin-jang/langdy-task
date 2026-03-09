package com.langdy.langdy_task.notification

/**
 * 수업 신청 완료 이후 알림 발송을 위한 클래스
 *
 * LessonService에서 수업 생성이 완료된 이후 호출됩니다.
 *
 * 설계 의도
 * - 수업 생성과 알림 발송을 분리합니다.
 * - 알림 발송 실패가 수업 생성 트랜잭션에 영향을 주지 않도록 합니다.
 *
 * 구현 방식 (1)
 * 1. LessonService에서 lesson 생성 완료
 * 2. publishNotification() 호출
 * 3. 이벤트 생성
 * 4. ApplicationEventPublisher를 통해 이벤트를 발행
 * 5. Listener에서 @TransactionalEventListener(phase = AFTER_COMMIT)를 사용하여 트랜잭션 커밋 이후 알림 발송
 *
 * 구현 방식 (2)
 * 1. LessonService에서 lesson 생성 완료
 * 2. publishNotification() 호출
 * 3. 이벤트 생성
 * 4. Transactional Outbox 패턴을 사용하여 이벤트를 DB에 저장
 * 5. 별도의 프로세스를 통해 메시지 브로커(Kafka / RabbitMQ / SQS 등)로 전달
 * 6. Consumer가 이벤트를 소비하여 알림 발송
 */
class NotificationPublisher {
    fun publishNotification() {
    }
}