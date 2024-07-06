package com.epackage.quizscore.logging

@Target(
	AnnotationTarget.FUNCTION,
	AnnotationTarget.PROPERTY_GETTER,
	AnnotationTarget.PROPERTY_SETTER,
	AnnotationTarget.CLASS
)
@Retention(
	AnnotationRetention.RUNTIME
)
annotation class Loggable 