package com.pawel.fuse.example.routebuilder;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.spring.SpringRouteBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by pmackiewicz on 2016-08-24.
 */
public class RecipientListRouteBuilder extends SpringRouteBuilder{

    @Override
    public void configure() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(12);

        from("direct:start").routeId("startRouteId")
            .log("[START] Before recipientList body = '${body}', thread: ${threadName}")
            .process(new Processor() {
                public void process(Exchange exchange) throws Exception {
                    String superRecipient = exchange.getIn().getHeader("super-recipient", String.class);
                    String recipients = exchange.getIn().getHeader("recipients", String.class);
                    if(superRecipient != null && superRecipient.equals("super")){
                        recipients += ", direct:super";
                    }
                    exchange.getIn().setHeader("recipients", recipients);
                }
            })
            .recipientList(header("recipients")).parallelProcessing().executorService(executor)
            .log("[FINISH] After recipientList body = '${body}', thread: ${threadName}")
        ;

        from("direct:first").routeId("firstRouteId")
            .transform(body().append(" - 1"))
            .log("[1] My body in first = '${body}', thread: ${threadName}")
        ;

        from("direct:second").routeId("secondRouteId")
            .transform(body().append(" - 2"))
            .log("[2] My body in second = '${body}', thread: ${threadName}")
        ;

        from("direct:third").routeId("thirdRouteId")
            .transform(body().append(" - 3"))
            .log("[3] My body in third = '${body}', thread: ${threadName}")
        ;

        from("direct:super").routeId("superRouteId")
            .transform(body().append(" - *"))
            .log("[*] My body in super = '${body}', thread: ${threadName}")
        ;
    }
}
