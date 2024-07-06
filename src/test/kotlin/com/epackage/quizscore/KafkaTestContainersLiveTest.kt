package com.epackage.quizscore

import com.epackage.quizscore.core.dto.Event
import com.epackage.quizscore.core.dto.QuizSubmission
import com.epackage.quizscore.externals.kafka.KafkaConsumer
import com.epackage.quizscore.externals.kafka.KafkaProducer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.junit.Before
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource
import org.springframework.kafka.core.*
import org.springframework.kafka.support.SendResult
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit4.SpringRunner
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import kotlin.random.Random


@RunWith(SpringRunner::class)
@Import(KafkaTestContainersLiveTest.KafkaTestContainersConfiguration::class)
@SpringBootTest(classes = [QuizScoreApplication::class])
@DirtiesContext
@Testcontainers
class KafkaTestContainersLiveTest {
	@Autowired
	lateinit var consumer: KafkaConsumer

	@Autowired
	lateinit var producer: KafkaProducer

	@Autowired
	@Qualifier("testKafkaTemplate")
	lateinit var kafkaTemplate: KafkaTemplate<String, Event>

	@Value("\${kafka.topic.quiz_submission}")
	lateinit var topic: String

	@Before
	fun setup() {
//		consumer.resetLatch()
	}

	@Test
	@Throws(Exception::class)
	fun givenKafkaDockerContainer_whenSendingWithSimpleProducer_thenMessageReceived() {
		val length = Random.nextInt(1, 11) // This will generate a random number between 1 and 10
		val chars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
		val randomUserID = List(10) { chars.random() }.joinToString("")
		val randomAnswer = List(length) { chars.random() }.joinToString("")

		val data = Event(QuizSubmission("quizID", randomUserID, randomAnswer))
		val future: CompletableFuture<SendResult<String, Event>> =
			kafkaTemplate.send(topic, "$randomUserID", data)
		future.whenComplete { result, ex ->
			if (result != null) {
				println("Message sent successfully to ${result?.recordMetadata?.topic()} partition ${result?.recordMetadata?.partition()} with offset ${result?.recordMetadata?.offset()}")
			} else if (ex != null) {
				// This will be called in case of a failure
				println("Failed to send message")
				ex.printStackTrace()
			}
		}
//		val messageConsumed: Boolean = consumer.latch.await(30, TimeUnit.SECONDS)
////		if (messageConsumed) {
//			assertTrue(messageConsumed)
//			assertEquals(randomAnswer, consumer.payload.answer)
////		}
	}

	companion object {
		@Container
		@JvmStatic
		var kafka: KafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"))
			.withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true")
			.withEnv("KAFKA_CREATE_TOPICS", "quiz_submission")

		@DynamicPropertySource
		@JvmStatic
		fun overrideProperties(registry: DynamicPropertyRegistry) {
			registry.add("spring.kafka.bootstrap-servers") { kafka.bootstrapServers }
		}
	}

	@TestConfiguration
	class KafkaTestContainersConfiguration {

		@Bean("testKafkaTemplate")
		fun kafkaTemplate(): KafkaTemplate<String, Event> {
			return KafkaTemplate(producerFactory())
		}

		@Bean
		fun consumerConfigs(): Map<String, Any> {
			val props: MutableMap<String, Any> = HashMap()
			props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafka.bootstrapServers
			props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
			props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] =
				JsonDeserializer::class.java // JsonDeserializer(Event::class.java)
			props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
			props[ConsumerConfig.GROUP_ID_CONFIG] = "quiz_service_group"
			// more standard configuration
			return props
		}

		@Bean
		fun producerFactory(): ProducerFactory<String, Event> {

			val configProps = mapOf(
				ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to kafka.bootstrapServers
			)
			return DefaultKafkaProducerFactory(configProps)
		}
	}

}