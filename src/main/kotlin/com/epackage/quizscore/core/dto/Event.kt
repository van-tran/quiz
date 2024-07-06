package com.epackage.quizscore.core.dto

data class Event(val payload: QuizSubmission) {
	val key = "${payload.userID}_${payload.answer}"
}