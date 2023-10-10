package com.ercanbeyen.movieapplication.aspect;

import com.ercanbeyen.movieapplication.constant.message.ResponseMessages;
import com.ercanbeyen.movieapplication.constant.names.ResourceNames;
import com.ercanbeyen.movieapplication.entity.Audience;
import com.ercanbeyen.movieapplication.exception.ResourceForbiddenException;
import com.ercanbeyen.movieapplication.exception.ResourceNotFoundException;
import com.ercanbeyen.movieapplication.repository.AudienceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AspectManagement {
    private final AudienceRepository audienceRepository;

    @Around("@annotation(com.ercanbeyen.movieapplication.constant.annotation.CheckSelfAuthentication) && target(bean)")
    public ResponseEntity<?> checkSelfAuthentication(ProceedingJoinPoint joinPoint, Object bean) throws Throwable {
        final String className = bean.getClass().getSimpleName();
        final String methodName = joinPoint.getSignature().getName();
        final Object[] args = joinPoint.getArgs();

        log.info("Class name: {} - method name: {}", className, methodName);

       Integer id = (Integer) args[1];
       UserDetails userDetails = (UserDetails) args[0];
        Audience audience = audienceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, ResourceNames.AUDIENCE)));

        StringBuilder message = new StringBuilder("User in database and logged in user are ");

        if (!audience.getUsername().equals(userDetails.getUsername())) {
            message.append("different");
            log.error(message.toString());
            throw new ResourceForbiddenException(ResponseMessages.FORBIDDEN);
        }

        message.append("same");
        log.info(message.toString());

        Object result = joinPoint.proceed(args);
        ResponseEntity<?> response = (ResponseEntity<?>) result;

        log.info("Recent --> status: {}, body: {}", response.getStatusCode(), response.getBody());

        return response;
    }
}
