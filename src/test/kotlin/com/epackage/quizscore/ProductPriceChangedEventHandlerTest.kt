package com.epackage.quizscore

import com.epackage.quizscore.core.dto.Event
import com.epackage.quizscore.core.dto.QuizSubmission
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.TestPropertySource
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@SpringBootTest
@TestPropertySource(
	properties = ["spring.kafka.consumer.auto-offset-reset=earliest"
	]
)
@Testcontainers
internal class ProductPriceChangedEventHandlerTest {
	@Autowired
	private val kafkaTemplate: KafkaTemplate<String, Any>? = null


	@BeforeEach
	fun setUp() {
		val e = Event(QuizSubmission("quizID", "randomUserID", "randomAnswer"))
	}

	@Test
	fun shouldHandleProductPriceChangedEvent() {
		val e = Event(QuizSubmission("quizID", "randomUserID", "randomAnswer"))

		kafkaTemplate!!.send("product-price-changes", e.payload.userID, e)

		//        val messageConsumed: Boolean = consumer.latch.await(10, TimeUnit.SECONDS)

//        assertTrue(messageConsumed)
//        assertEquals("randomAnswer", consumer.payload.answer)
	}

	companion object {
		@Container
		val kafka: KafkaContainer = KafkaContainer(
			DockerImageName.parse("confluentinc/cp-kafka:7.6.1")
		)

		@DynamicPropertySource
		fun overrideProperties(registry: DynamicPropertyRegistry) {
			registry.add("spring.kafka.bootstrap-servers") { kafka.bootstrapServers }
		}
	}
}