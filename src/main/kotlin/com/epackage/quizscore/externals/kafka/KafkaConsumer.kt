package com.epackage.quizscore.externals.kafka

import com.epackage.quizscore.core.QuizService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
@Service
class KafkaConsumer(@Autowired val quizService: QuizService) {

    @KafkaListener(topics = ["\${kafka.topic.quiz_submission}"], groupId = "\${kafka.group.quiz_service_group}")
    fun consume(message: String) {
        println("Consumed message: $message")
        quizService.handleQuizAnswer(answer = message)
    }


}