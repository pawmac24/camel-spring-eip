package com.pawel.fuse.example;

import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by pmackiewicz on 2016-08-25.
 */
public class ContentBasedRouterTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/content-based-router-context.xml");
    }

    @Test
    public void testMockAllEndpoints() throws Exception {
        //given
        context.getRouteDefinitions().get(0).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                // mock all endpoints
                mockEndpoints();
            }
        });

        MockEndpoint mockFinish = getMockEndpoint("mock:finish");
        MockEndpoint mockFirst = getMockEndpoint("mock:first");
        MockEndpoint mockDirectFirst = getMockEndpoint("mock:direct:first");

        mockFinish.expectedMessageCount(3);
        mockFinish.expectedBodiesReceived("Funny World", "First Value", "Second Value");
        mockFirst.expectedMessageCount(1);
        mockFirst.expectedBodiesReceived("First Value");
        mockDirectFirst.expectedBodiesReceived("Hello World");
        mockDirectFirst.expectedMessageCount(1);

        //when
        template.sendBody("direct:start", "Funny World");
        template.sendBodyAndHeader("direct:start", "Hello World", "myCommand", "firstCommand");
        template.sendBodyAndHeader("direct:start", "Hate World", "myCommand", "secondCommand");

        //then
        assertMockEndpointsSatisfied(); //use this for all mock endpoints

        // additional test to ensure correct endpoints in registry
        assertNotNull(context.hasEndpoint("direct:start"));
        assertNotNull(context.hasEndpoint("direct:first"));
        assertNotNull(context.hasEndpoint("direct:second"));
        assertNotNull(context.hasEndpoint("mock:first"));
        assertNotNull(context.hasEndpoint("mock:second"));
        assertNotNull(context.hasEndpoint("mock:finish"));
        // all the endpoints was mocked
        assertNotNull(context.hasEndpoint("mock:direct:start"));
        assertNotNull(context.hasEndpoint("mock:direct:first"));
        assertNotNull(context.hasEndpoint("mock:direct:second"));
    }
}

