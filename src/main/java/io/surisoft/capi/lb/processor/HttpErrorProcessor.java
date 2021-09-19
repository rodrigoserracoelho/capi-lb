package io.surisoft.capi.lb.processor;

import io.surisoft.capi.lb.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HttpErrorProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        exchange.getIn().setHeader(Constants.CAPI_URI_IN_ERROR, exchange.getIn().getHeader(Exchange.HTTP_URI).toString());
        exchange.getIn().setHeader(Constants.CAPI_URL_IN_ERROR, exchange.getIn().getHeader(Exchange.HTTP_URL).toString());
    }
}
