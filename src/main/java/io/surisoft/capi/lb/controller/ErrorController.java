package io.surisoft.capi.lb.controller;

import io.surisoft.capi.lb.schema.CapiRestException;
import io.surisoft.capi.lb.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
public class ErrorController {
    @GetMapping(path = Constants.CAPI_INTERNAL_REST_ERROR_PATH + "/**")
    public ResponseEntity<CapiRestException> get(HttpServletRequest request) {
        return buildResponse(request);
    }

    @PostMapping(path = Constants.CAPI_INTERNAL_REST_ERROR_PATH + "/**")
    public ResponseEntity<CapiRestException> post(HttpServletRequest request) {
        return buildResponse(request);
    }

    @PutMapping(path = Constants.CAPI_INTERNAL_REST_ERROR_PATH)
    public ResponseEntity<CapiRestException> put(HttpServletRequest request) {
        return buildResponse(request);
    }

    @DeleteMapping(path = Constants.CAPI_INTERNAL_REST_ERROR_PATH)
    public ResponseEntity<CapiRestException> delete(HttpServletRequest request) {
        return buildResponse(request);
    }

    private ResponseEntity<CapiRestException> buildResponse(HttpServletRequest request) {

        String routeId = request.getHeader(Constants.ROUTE_ID_HEADER);
        CapiRestException capiRestException = new CapiRestException();

        String errorMessage =  request.getHeader(Constants.REASON_MESSAGE_HEADER);

        if(request.getHeader(Constants.CAPI_URI_IN_ERROR) != null) {
          capiRestException.setHttpUri(request.getHeader(Constants.CAPI_URI_IN_ERROR));
        }

        if(request.getHeader(Constants.CAPI_URL_IN_ERROR) != null) {
            capiRestException.setHttpUrl(request.getHeader(Constants.CAPI_URL_IN_ERROR));
        }

        if(Boolean.parseBoolean(request.getHeader(Constants.ERROR_API_SHOW_TRACE_ID))) {
            capiRestException.setZipkinTraceID(request.getHeader(Constants.TRACE_ID_HEADER));
        }
        if(Boolean.parseBoolean(request.getHeader(Constants.ERROR_API_SHOW_INTERNAL_ERROR_MESSAGE))) {
            capiRestException.setInternalExceptionMessage(request.getHeader(Constants.CAPI_INTERNAL_ERROR));
            capiRestException.setException(request.getHeader(Constants.CAPI_INTERNAL_ERROR_CLASS_NAME));
        }

        if(errorMessage != null) {
            capiRestException.setErrorMessage(errorMessage);
        } else {
            capiRestException.setErrorMessage("There was an exception connecting to your api");
        }

        if(request.getHeader(Constants.REASON_CODE_HEADER) != null) {
            int returnedCode = Integer.parseInt(request.getHeader(Constants.REASON_CODE_HEADER));
            capiRestException.setErrorCode(returnedCode);
        } else {
            capiRestException.setErrorCode(HttpStatus.SERVICE_UNAVAILABLE.value());
        }

        capiRestException.setRouteID(request.getHeader(Constants.ROUTE_ID_HEADER));
        return new ResponseEntity<>(capiRestException, HttpStatus.valueOf(capiRestException.getErrorCode()));
    }
}
