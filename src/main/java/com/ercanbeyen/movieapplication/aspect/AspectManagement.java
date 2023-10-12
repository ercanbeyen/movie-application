package com.ercanbeyen.movieapplication.aspect;

import com.ercanbeyen.movieapplication.constant.message.ResponseMessages;
import com.ercanbeyen.movieapplication.entity.Audience;
import com.ercanbeyen.movieapplication.exception.ResourceForbiddenException;
import com.ercanbeyen.movieapplication.service.AudienceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AspectManagement {
    private final AudienceService audienceService;

    @Around("@annotation(com.ercanbeyen.movieapplication.constant.annotation.CheckSelfAuthentication) && target(bean)")
    public Object checkSelfAuthentication(ProceedingJoinPoint proceedingJoinPoint, Object bean) throws Throwable {
        final String className = getClassName(bean);
        final String methodName = getMethodName(proceedingJoinPoint);
        final Object[] args = getArguments(proceedingJoinPoint);

        log.info("Class name: {} - method name: {}", className, methodName);

        Integer id = (Integer) args[1];
        UserDetails userDetails = (UserDetails) args[0];
        Audience audience = audienceService.findAudienceById(id);
        StringBuilder message = new StringBuilder("User in database and logged in user are ");

        if (!audience.getUsername().equals(userDetails.getUsername())) {
            message.append("different");
            log.error(message.toString());
            throw new ResourceForbiddenException(ResponseMessages.FORBIDDEN);
        }

        message.append("same");
        log.info(message.toString());

        Object result = proceedingJoinPoint.proceed(args);
        ResponseEntity<?> response = (ResponseEntity<?>) result;

        log.info("Recent --> status: {}, body: {}", response.getStatusCode(), response.getBody());

        return response;
    }

    @Around("execution(* com.ercanbeyen.movieapplication.service..*(..)) && target(bean)")
    public Object logStartAndEnd(ProceedingJoinPoint proceedingJoinPoint, Object bean) throws Throwable {
        final String className = getClassName(bean);
        final String methodName = getMethodName(proceedingJoinPoint);
        Object[] args = getArguments(proceedingJoinPoint);

        log.info("Starting method: {} in class {} with args: {}", methodName, className, args);
        final Object result = proceedingJoinPoint.proceed();

        if (Objects.nonNull(result)) {
            log.info("End method: {} in class: {} return value: {}", methodName, className, result);
        } else {
            log.info("End void method: {} in class: {}", methodName, className);
        }

        return result;
    }

    String getClassName(Object object) {
        return object.getClass().getSimpleName();
    }

    String getMethodName(ProceedingJoinPoint proceedingJoinPoint) {
        return proceedingJoinPoint.getSignature().getName();
    }

    Object[] getArguments(ProceedingJoinPoint proceedingJoinPoint) {
        return proceedingJoinPoint.getArgs();
    }
}
