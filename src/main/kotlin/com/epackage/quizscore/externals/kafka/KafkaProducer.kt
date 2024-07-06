package com.epackage.quizscore.externals.kafka

import com.epackage.quizscore.core.dto.Event
import com.epackage.quizscore.logging.Loggable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
@Loggable
@Component
class KafkaProducer(private val kafkaTemplate: KafkaTemplate<String, Event>) {

    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Value("\${kafka.topic.score-result-topic}")
    private lateinit var scoreResultTopic: String
    @Value("\${kafka.topic.quiz_submission}")
    private lateinit var answerSubmissionTopic: String

    fun sendUpdateScore(event : Event) {
        send(scoreResultTopic, event)
    }
    fun sendAnswerSubmission(event : Event) {
        send(answerSubmissionTopic, event)

    }
    private fun send(topic : String, event : Event) {
        logger.info("Sent event $event to topic $topic")
        kafkaTemplate.send(topic, event)
            .whenCompleteAsync{ result, ex ->
            if (result != null) {
                logger.info("Message sent successfully to ${result?.recordMetadata?.topic()} partition ${result?.recordMetadata?.partition()} with offset ${result?.recordMetadata?.offset()}")
            } else if (ex != null) {
                // This will be called in case of a failure
                logger.info("Failed to send message")
                ex.printStackTrace()
            }
        }

    }
}