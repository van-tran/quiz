package com.epackage.quizscore.core

import com.epackage.quizscore.core.dto.Event
import com.epackage.quizscore.core.dto.QuizSubmission
import com.epackage.quizscore.externals.kafka.KafkaProducer
import com.epackage.quizscore.logging.Loggable
import jakarta.annotation.PostConstruct
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Service

@Loggable
@Service
class QuizServiceImpl(val redisTemplate: RedisTemplate<String, String>,
                      val kafkaProducer: KafkaProducer) : QuizService {

    val opsForZSet: ZSetOperations<String, String> by lazy {
        redisTemplate.opsForZSet()
    }

    override fun handleQuizAnswer(quizSubmission: QuizSubmission) {
        val score = scoreQuizAnswer(quizSubmission)
        saveScoreToRedis(quizSubmission, score)
    }
    fun scoreQuizAnswer(quizSubmission: QuizSubmission): Int {
        // Implement your scoring logic here
        // This is a placeholder for simplicity
        return quizSubmission.answer.length // Example scoring logic
    }

    fun saveScoreToRedis(quizSubmission: QuizSubmission, score: Int) {
        val scoreBoardKey = "QuizScores" // Redis key for the sorted set
        val userId = quizSubmission.userID
        // Use the userId as the member and score as the score for the sorted set
        opsForZSet.add(scoreBoardKey, userId, score.toDouble())
        println("Score saved to Redis: $score for userId $userId")

        quizSubmission.score = score
        kafkaProducer.sendUpdateScore(Event(quizSubmission))
    }
}