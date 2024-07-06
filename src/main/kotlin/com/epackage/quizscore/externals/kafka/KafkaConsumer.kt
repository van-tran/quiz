package com.epackage.quizscore.externals.kafka

import com.epackage.quizscore.core.QuizService
import com.epackage.quizscore.core.dto.Event
import com.epackage.quizscore.core.dto.QuizSubmission
import com.epackage.quizscore.logging.Loggable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import java.util.concurrent.CountDownLatch
@Loggable
@Component
class KafkaConsumer(val quizService: QuizService) {

    // Initializing instance of Logger for Service
    val logger: Logger = LoggerFactory.getLogger(KafkaConsumer::class.java)

    @KafkaListener(topics = ["\${kafka.topic.quiz_submission}"],
        groupId = "\${kafka.group.quiz_service_group}",
        containerFactory = "kafkaListenerContainerFactory")
    fun consume(@Payload event: Event) {
        logger.info("Consumed message: $event")
        quizService.handleQuizAnswer(answer = event.payload)
    }


}