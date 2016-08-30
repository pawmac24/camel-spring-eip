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
public class MessageFilterRouterTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/message-filter-router-context.xml");
    }

    @Override
    public boolean isUseAdviceWith() {
        return true;
    }

    @Test
    public void testMockAllEndpoints() throws Exception {
        //given
        context.getRouteDefinitions().get(0).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                // mock all endpoints
                mockEndpoints();
                weaveById("givenCommandId").after().to("mock:before");
                weaveById("finishRouteId").after().to("mock:finish");
            }
        });

        context.start();

        MockEndpoint mockBefore = getMockEndpoint("mock:before");
        MockEndpoint mockFinish = getMockEndpoint("mock:finish");

        mockBefore.expectedMessageCount(5);
        mockBefore.expectedBodiesReceived("Bad World", "Hello World", "Wonderful World", "Funny World", "Bad World");

        mockFinish.expectedMessageCount(3);
        mockFinish.expectedBodiesReceived("Hello World", "Wonderful World", "Funny World");

        //when
        template.sendBody("direct:start", "Bad World");
        template.sendBodyAndHeader("direct:start", "Hello World", "myCommand", "firstCommand");
        template.sendBodyAndHeader("direct:start", "Wonderful World", "myCommand", "secondCommand");
        template.sendBodyAndHeader("direct:start", "Funny World", "myCommand", "secondCommand");
        template.sendBodyAndHeader("direct:start", "Bad World", "myCommand", "customOperation");

        //then
        assertMockEndpointsSatisfied(); //use this for all mock endpoints

        // additional test to ensure correct endpoints in registry
        assertNotNull(context.hasEndpoint("direct:start"));
        // all the endpoints was mocked
        assertNotNull(context.hasEndpoint("mock:direct:start"));

        context.stop();
    }
}

