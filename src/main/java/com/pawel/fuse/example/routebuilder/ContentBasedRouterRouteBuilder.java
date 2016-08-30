package com.pawel.fuse.example.routebuilder;

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
            .end()
            .log("===")
            .to("mock:finish")
        ;

        from("direct:first").routeId("firstRouteId")
            .transform(constant("First Value"))
            .log("My body in first = '${body}'")
            .to("mock:first")
        ;

        from("direct:second").routeId("secondRouteId")
            .transform(constant("Second Value"))
            .log("My body in second = '${body}'")
            .to("mock:second")
        ;
    }
}
