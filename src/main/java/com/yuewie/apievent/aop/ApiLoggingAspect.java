package com.yuewie.apievent.aop;

import com.yuewie.apievent.aop.log.Loggable;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

@Aspect
@Component
public class ApiLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(ApiLoggingAspect.class);

    // Le pointcut se déclenche si l'annotation est sur la classe (grace whithin) OU sur la méthode (annotation).
    @Around("@within(com.yuewie.apievent.aop.log.Loggable) || @annotation(com.yuewie.apievent.aop.log.Loggable)")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
       MethodSignature signature = (MethodSignature) joinPoint.getSignature();
       Optional<Loggable> loggableAnnotationOpt = findLoggableAnnotation(joinPoint,signature);

        // Si le logging est désactivé pour cette portée, on arrête tout et on exécute la méthode.
        if (loggableAnnotationOpt.isEmpty() || !loggableAnnotationOpt.get().active()) {
            return joinPoint.proceed();
        }

        String methodName = signature.toShortString();

        if (loggableAnnotationOpt.get().logParams() && logger.isTraceEnabled()) {
            logger.trace("--> Entrée: {} avec arguments: {}", methodName, Arrays.toString(joinPoint.getArgs()));
        }

        long startTime = System.currentTimeMillis();
        Object result;

        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            logger.error("!!! Exception dans {}: {}", methodName, e.toString(), e);
            throw e;
        }

        long duration = System.currentTimeMillis() - startTime;
        logger.info("Temps d'exécution de {}: {} ms", methodName, duration);

        return result;
    }

    /**
     * Trouve l'annotation @Loggable applicable, en donnant la priorité à la méthode
     * puis à la classe.
     * @return Un Optional contenant l'annotation si trouvée.
     */
    private Optional<Loggable> findLoggableAnnotation(ProceedingJoinPoint joinPoint, MethodSignature signature) {

        Method method = signature.getMethod();

        // Priorité n°1 : l'annotation sur la méthode
        Loggable annotation = method.getAnnotation(Loggable.class);

        if (annotation != null) {
            return Optional.of(annotation);
        }

        // Priorité n°2 : l'annotation sur la classe
        Class<?> targetClass = joinPoint.getTarget().getClass();
        return Optional.ofNullable(targetClass.getAnnotation(Loggable.class));
    }
}
