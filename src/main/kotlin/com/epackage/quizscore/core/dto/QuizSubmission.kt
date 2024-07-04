package com.epackage.quizscore.core.dto

data class QuizSubmission(
    val quizID : String,
    val userID : String,
    val answer : String
)