package com.kustlik.medicalclinic.remote.error;

import com.kustlik.medicalclinic.remote.exception.InternalServerErrorException;
import com.kustlik.medicalclinic.remote.exception.ServiceUnavailableException;
import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class RetreiveMessageErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder errorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        FeignException feignException = FeignException.errorStatus(methodKey, response);
        String message = feignException.getMessage();
        return switch (response.status()) {
            case 500 -> new InternalServerErrorException(message != null ? message : "Bad Request");
            case 503 -> new ServiceUnavailableException(message != null ? message : "Not found");
            default -> errorDecoder.decode(methodKey, response);
        };
    }
}
