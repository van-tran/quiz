package com.epackage.quizscore.core

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service


@Service
class QuizServiceImpl(val redisTemplate: RedisTemplate<String, String>) : QuizService {

    override fun handleQuizAnswer(answer: String) {
        val score = scoreQuizAnswer(answer)
        saveScoreToRedis(answer, score)
    }
    fun scoreQuizAnswer(answer: String): Int {
        // Implement your scoring logic here
        // This is a placeholder for simplicity
        return answer.length // Example scoring logic
    }

    fun saveScoreToRedis(answer: String, score: Int) {
        // Use the answer as the key and score as the value for simplicity
        redisTemplate.opsForValue().set("QuizScore:$answer", score.toString())
        println("Score saved to Redis: $score for answer $answer")
    }
}