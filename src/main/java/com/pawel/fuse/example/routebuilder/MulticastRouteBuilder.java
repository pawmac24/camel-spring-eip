package com.pawel.fuse.example.routebuilder;

import org.apache.camel.spring.SpringRouteBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by pmackiewicz on 2016-08-24.
 */
public class MulticastRouteBuilder extends SpringRouteBuilder{

    @Override
    public void configure() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(12);

        from("direct:start").routeId("startRouteId")
            .log("[START] Before multicast body = '${body}', thread: ${threadName}")
            .multicast().parallelProcessing().executorService(executor)
            .to("direct:first", "direct:second", "direct:third")
            .end()
            .log("[FINISH] After multicast body = '${body}', thread: ${threadName}")
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
    }
}
