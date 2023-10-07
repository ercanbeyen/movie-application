package com.ercanbeyen.movieapplication.util;

import com.ercanbeyen.movieapplication.constant.message.ResponseMessages;
import com.ercanbeyen.movieapplication.entity.Audience;
import com.ercanbeyen.movieapplication.exception.ResourceForbiddenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;

@Slf4j
public class SecurityUtil {
    public static void checkUserLoggedIn(Audience audience, UserDetails userDetails) {
        StringBuilder message = new StringBuilder("User in database and logged in user are ");
        if (audience.getUsername().equals(userDetails.getUsername())) {
            message.append("different");
            log.error(message.toString());
            throw new ResourceForbiddenException(ResponseMessages.FORBIDDEN);
        }

        message.append("same");
        log.info(message.toString());
    }
}
