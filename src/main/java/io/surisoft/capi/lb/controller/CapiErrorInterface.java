package io.surisoft.capi.lb.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
@Slf4j
public class CapiErrorInterface implements ErrorController {
    @GetMapping(value = "/error")
    public ResponseEntity<String> handleError(HttpServletRequest request) {
        log.info("HANDLING........");
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {

            Integer statusCode = Integer.valueOf(status.toString());

            if(statusCode == HttpStatus.NOT_FOUND.value()) {
                return new ResponseEntity<>("error-404", HttpStatus.NOT_FOUND);
            }
            else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return new ResponseEntity<>("error-500", HttpStatus.INTERNAL_SERVER_ERROR);

            }
        }
       return new ResponseEntity<>("error-500", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
