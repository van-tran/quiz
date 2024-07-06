package com.epackage.quizscore.core

import com.epackage.quizscore.core.dto.QuizSubmission

interface QuizService {
    fun handleQuizAnswer(answer: QuizSubmission)
}
