package com.epackage.quizscore

import com.epackage.quizscore.core.QuizServiceImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations

class QuizServiceImplTest {

    private lateinit var redisTemplate: RedisTemplate<String, String>
    private lateinit var valueOperations: ValueOperations<String, String>
    private lateinit var quizService: QuizServiceImpl

    @BeforeEach
    fun setUp() {
        redisTemplate = mock()
        valueOperations = mock()
        Mockito.`when`(redisTemplate.opsForValue()).thenReturn(valueOperations)
        quizService = QuizServiceImpl(redisTemplate)
    }

    @Test
    fun `handleQuizAnswer should score and save to Redis`() {
        val answer = "testAnswer"
        val expectedScore = answer.length

        quizService.handleQuizAnswer(answer)

        verify(redisTemplate).opsForValue()
        verify(valueOperations).set("QuizScore:$answer", expectedScore.toString())
    }

    @Test
    fun `scoreQuizAnswer returns correct score based on answer length`() {
        val answer = "test"
        val expectedScore = 4 // Length of "test"

        val score = quizService.scoreQuizAnswer(answer)

        assert(score == expectedScore)
    }

    @Test
    fun `saveScoreToRedis saves score with correct key`() {
        val answer = "answer"
        val score = 42

        quizService.saveScoreToRedis(answer, score)

        verify(valueOperations).set("QuizScore:$answer", score.toString())
    }
}