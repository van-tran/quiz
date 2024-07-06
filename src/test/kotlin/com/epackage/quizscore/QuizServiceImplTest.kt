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

class QuizServiceImplTest {

    private lateinit var redisTemplate: StringRedisTemplate
    private lateinit var kafkaProducer: KafkaProducer
    private lateinit var valueOperations: ZSetOperations<String, String>
    private lateinit var quizService: QuizServiceImpl

    @BeforeEach
    fun setUp() {
        redisTemplate = mock()
        valueOperations = mock()
        kafkaProducer = mock()
        Mockito.`when`(redisTemplate.opsForZSet()).thenReturn(valueOperations)
        quizService = QuizServiceImpl(redisTemplate,kafkaProducer)
    }

    @Test
    fun `handleQuizAnswer should score and save to Redis`() {
        val answer = "testAnswer"
        val expectedScore = answer.length

        quizService.handleQuizAnswer(QuizSubmission("quizID", "userID", answer))

        verify(redisTemplate).opsForValue()
        verify(valueOperations).add("QuizScores","QuizScore:$answer", expectedScore.toDouble())
    }

    @Test
    fun `scoreQuizAnswer returns correct score based on answer length`() {
        val answer = "test"
        val expectedScore = 4 // Length of "test"

        val score = quizService.scoreQuizAnswer(QuizSubmission("quizID", "userID", answer))

        assert(score == expectedScore)
    }

    @Test
    fun `saveScoreToRedis saves score with correct key`() {
        val answer = "answer"
        val score = 42

        quizService.saveScoreToRedis(QuizSubmission("quizID", "userID", answer), score)

        verify(valueOperations).add("QuizScores","QuizScore:$answer", score.toDouble())
    }
}