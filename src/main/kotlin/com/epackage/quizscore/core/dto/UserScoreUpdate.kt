package com.epackage.quizscore.core.dto

data class UserScoreUpdate(
    val quizID : String,
    val userID : String,
    val previousScore: Int,
    val score : Int
)