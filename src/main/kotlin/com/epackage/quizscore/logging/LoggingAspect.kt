package com.epackage.quizscore.logging

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.CodeSignature
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*
import kotlin.math.min

@Aspect
@Component
class LoggingAspect {
	@Pointcut("@within(Loggable)")
	fun loggable() {
	}

	//    @Pointcut("@within(org.springframework.stereotype.Repository)")
	//    public void dao() {
	//    }
	//
	//    @Pointcut("@within(org.springframework.stereotype.Controller)")
	//    public void controller() {
	//    }
	//    @Pointcut("execution(public * *(..))") //this should work for the public pointcut
	//    private void anyPublicOperation() {}
	//
	//    @Pointcut("anyPublicOperation() && loggable()")
	//    private void loggingExecutedTime() {}
	@Before("loggable()")
	fun logParams(joinPoint: JoinPoint) {
		val className = joinPoint.sourceLocation.withinType.name
		val codeSignature = joinPoint.signature as CodeSignature
		val logger = LoggerFactory.getLogger(className)
		val parameterNames = codeSignature.parameterNames
		val args = joinPoint.args
		for (i in parameterNames.indices) {
			val paramName = parameterNames[i]
			if (paramName != null && paramName.lowercase(Locale.getDefault()).contains("pass")) {
				if (args.size > i) {
					args[i] = "*****"
				}
			}
			try {
				val logStr = args[i].toString()
				args[i] = logStr.substring(0, min(100.0, logStr.length.toDouble()).toInt())
			} catch (ignored: Exception) {
			}
		}

		logger.info("call {} with params: {} values {}", joinPoint.signature.toShortString(), parameterNames, args)
	}

	@Around("loggable()")
	@Throws(Throwable::class)
	fun logExecutionTime(joinPoint: ProceedingJoinPoint): Any? {
		val start = System.currentTimeMillis()

		val proceed = joinPoint.proceed()

		val executionTime = System.currentTimeMillis() - start

		var proceedStr = proceed?.toString() ?: "NULL"
		proceedStr = proceedStr.substring(0, min(1000.0, proceedStr.length.toDouble()).toInt())
		logger.info("{} executed in {}ms, return = {}", joinPoint.signature.toShortString(), executionTime, proceedStr)
		return proceed
	}

	companion object {
		val logger: Logger = LoggerFactory.getLogger("LoggingAspect")
	}
}
