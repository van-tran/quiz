package com.epackage.quizscore.externals.controllers

data class AnswerSubmission(
	val quizId: String,
	val userId: String,
	val answer: String
)