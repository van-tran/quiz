package com.epackage.quizscore

import com.epackage.quizscore.core.QuizServiceImpl
import com.epackage.quizscore.core.dto.Event
import com.epackage.quizscore.core.dto.QuizSubmission
import com.epackage.quizscore.externals.kafka.KafkaProducer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.data.redis.core.ZSetOperations
import org.mockito.kotlin.whenever

class QuizServiceImplTest {

    private lateinit var redisTemplate: RedisTemplate<String, Any>
    private lateinit var kafkaProducer: KafkaProducer
    private lateinit var zSetOperations: ZSetOperations<String, Any>
    private lateinit var quizService: QuizServiceImpl

    @BeforeEach
    fun setUp() {
        redisTemplate = mock()
        kafkaProducer = mock()
        zSetOperations = mock()
        whenever(redisTemplate.opsForZSet()).thenReturn(zSetOperations)
        quizService = QuizServiceImpl(redisTemplate, kafkaProducer)
    }

    @Test
    fun `handleQuizAnswer should score and save to Redis and send Kafka message`() {
        val quizSubmission = QuizSubmission("quizID", "userID", "testAnswer")
        val expectedScore = quizSubmission.answer.length

        quizService.handleQuizAnswer(quizSubmission)

        verify(zSetOperations).add("QuizScores", quizSubmission.userID, expectedScore.toDouble())
        verify(kafkaProducer).sendUpdateScore(Event(quizSubmission.copy(score = expectedScore)))
    }

    @Test
    fun `scoreQuizAnswer returns correct score based on answer length`() {
        val quizSubmission = QuizSubmission("quizID", "userID", "test")
        val expectedScore = 4 // Length of "test"

        val score = quizService.scoreQuizAnswer(quizSubmission)

        assert(score == expectedScore)
    }

    @Test
    fun `saveScoreToRedis saves score with correct key and value`() {
        val quizSubmission = QuizSubmission("quizID", "userID", "answer")
        val score = 42

        quizService.saveScoreToRedis(quizSubmission, score)

        verify(zSetOperations).add("QuizScores", quizSubmission.userID, score.toDouble())
    }
}