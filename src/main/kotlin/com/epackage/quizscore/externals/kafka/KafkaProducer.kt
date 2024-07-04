package com.epackage.quizscore.externals.kafka

import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaProducer(private val kafkaTemplate: KafkaTemplate<String, String>) {
    @Value("\${kafka.topic.score-result-topic}")
    private lateinit var scoreResultTopic: String

    fun sendScoreResult(answer: String, score: Int) {
        val scoreMessage = "Answer: $answer, Score: $score"
        kafkaTemplate.send(scoreResultTopic, scoreMessage)
    }
}