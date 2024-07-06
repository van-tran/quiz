package com.epackage.quizscore

import com.epackage.quizscore.core.dto.Event
import com.epackage.quizscore.core.dto.QuizSubmission
import com.epackage.quizscore.externals.kafka.KafkaConsumer
import com.epackage.quizscore.externals.kafka.KafkaProducer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:9093", "port=9093"])
internal class EmbeddedKafkaIntegrationTest {
	@Autowired
	lateinit var consumer: KafkaConsumer

	@Autowired
	lateinit var producer: KafkaProducer

	@Value("\${kafka.topic.quiz_submission}")
	lateinit var topic: String


	@BeforeEach
	fun setup() {
		println("reset latch")
//		consumer.resetLatch()
	}

	@Test
	@Throws(Exception::class)
	fun givenEmbeddedKafkaBroker_whenSendingWithSimpleProducer_thenMessageReceived() {
		val length = Random.nextInt(1, 11) // This will generate a random number between 1 and 10
		val chars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
		val randomUserID = List(10){ chars.random() }.joinToString("")
		val randomAnswer = List(length) { chars.random() }.joinToString("")


		val data = Event(QuizSubmission("quizID", randomUserID, randomAnswer))

		producer.send(topic, data)

//		val messageConsumed = consumer.latch.await(10, TimeUnit.SECONDS)
//		assertTrue(messageConsumed)
//		assertEquals(consumer.payload.answer, data.payload.answer)
	}
}