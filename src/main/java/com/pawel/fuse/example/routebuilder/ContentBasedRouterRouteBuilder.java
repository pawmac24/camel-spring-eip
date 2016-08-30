package com.pawel.fuse.example.routebuilder;

import org.apache.camel.Exchange;
import org.apache.camel.spring.SpringRouteBuilder;

/**
 * Created by pmackiewicz on 2016-08-24.
 */
public class ContentBasedRouterRouteBuilder extends SpringRouteBuilder{

    @Override
    public void configure() throws Exception {

        from("direct:start").routeId("startRouteId")
            .log("Command name = '${header.myCommand}'")
            .choice()
                .when(header("myCommand").isEqualTo("firstCommand"))
                    .log("I am firstCommand")
                    .to("direct:first")
                .when(header("myCommand").isEqualTo("secondCommand"))
                    .log("I am secondCommand")
                    .to("direct:second")
                .otherwise()
                    .log("Other or no command")
            .end()
            .log("===")
            .to("mock:finish")
        ;

        from("direct:first").routeId("firstRouteId")
            .transform(constant("First Value"))
            .log("My body in first = '${body}'")
            .setHeader(Exchange.FILE_NAME, constant("first-file.txt"))
            .to("file://target/first")
            .to("mock:first")
        ;

        from("direct:second").routeId("secondRouteId")
            .transform(constant("Second Value"))
            .log("My body in second = '${body}'")
            .setHeader(Exchange.FILE_NAME, constant("second-file.txt"))
            .to("file:target/second")
            .to("mock:second")
        ;
    }
}
