package com.epackage.quizscore.externals.controllers

import com.epackage.quizscore.core.QuizService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import com.epackage.quizscore.externals.kafka.KafkaProducer
import com.epackage.quizscore.core.dto.Event
import com.epackage.quizscore.core.dto.QuizSubmission

@RestController
class AnswerController(@Autowired private val kafkaProducer: KafkaProducer,
					   val quizService: QuizService) {

	@PostMapping("/submit-answer")
	fun submitAnswer(@RequestBody submission: AnswerSubmission): ResponseEntity<String> {
		val event = Event(QuizSubmission(submission.quizId, submission.userId, submission.answer))
//		quizService.handleQuizAnswer(event.payload)
		kafkaProducer.sendAnswerSubmission(event)
		return ResponseEntity.ok("Answer submitted successfully")
	}
}