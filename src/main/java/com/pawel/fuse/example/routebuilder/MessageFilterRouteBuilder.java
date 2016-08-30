package com.pawel.fuse.example.routebuilder;

import org.apache.camel.spring.SpringRouteBuilder;

/**
 * Created by pmackiewicz on 2016-08-24.
 */
public class MessageFilterRouteBuilder extends SpringRouteBuilder{

    @Override
    public void configure() throws Exception {

        from("direct:start").routeId("startRouteId")
            .log("Command name = '${header.myCommand}'").id("givenCommandId")
            .filter(header("myCommand").endsWith("Command"))
            .log("Passed command name = '${header.myCommand}'")
            .log("===").id("finishRouteId")
        ;
    }
}
