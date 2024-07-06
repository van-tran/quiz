package com.epackage.quizscore.externals.kafka

import com.epackage.quizscore.core.dto.Event
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer


@Configuration
@EnableKafka
class KafkaConfig {

    @Value("\${spring.kafka.bootstrap-servers}")
    private lateinit var kafkaBootstrapHost: String
    @Value("\${spring.kafka.bootstrap-servers.port}")
    private lateinit var kafkaBootstrapPort: String

    @Value("\${kafka.group.quiz_service_group}")
    private lateinit var quizServiceGroup : String



    @Value("\${kafka.topic.score-result-topic}")
    private lateinit var scoreResultTopic: String
    @Value("\${kafka.topic.quiz_submission}")
    private lateinit var answerSubmissionTopic: String

    val kafkaBootstrapServers : String
        get() = "$kafkaBootstrapHost:$kafkaBootstrapPort"

    @Bean
    fun topicSubmission(): NewTopic {
        return TopicBuilder.name(answerSubmissionTopic).build()
    }

    @Bean
    fun topicScoreResult(): NewTopic {
        return TopicBuilder.name(scoreResultTopic).build()
    }

    // Producer Configuration
    @Bean
    fun producerFactory(): ProducerFactory<String, Event> {
        val configProps = mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaBootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java
        )
        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, Event> {
        return KafkaTemplate(producerFactory())
    }


    // Consumer Configuration
    @Bean
    fun consumerFactory(): DefaultKafkaConsumerFactory<String, Event> {
        val configProps = mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaBootstrapServers,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,
            ConsumerConfig.GROUP_ID_CONFIG to quizServiceGroup,
            JsonDeserializer.TRUSTED_PACKAGES to "*"
        )
        return DefaultKafkaConsumerFactory(configProps)
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, Event> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, Event>()
        factory.consumerFactory = consumerFactory()
        return factory
    }
}