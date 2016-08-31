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
public class MulticastTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/multicast-context.xml");
    }

    @Override
    public boolean isUseAdviceWith() {
        return true;
    }

    @Test
    public void testMockAllEndpoints() throws Exception {
        //given
        context.getRouteDefinition("startRouteId").adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                weaveAddLast().to("mock:finish");
            }
        });
        context.getRouteDefinition("firstRouteId").adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                weaveAddLast().to("mock:first");
            }
        });
        context.getRouteDefinition("secondRouteId").adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                weaveAddLast().to("mock:second");
            }
        });
        context.getRouteDefinition("thirdRouteId").adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                weaveAddLast().to("mock:third");
            }
        });

        context.start();

        MockEndpoint mockFinish = getMockEndpoint("mock:finish");
        MockEndpoint mockFirst = getMockEndpoint("mock:first");
        MockEndpoint mockSecond = getMockEndpoint("mock:second");
        MockEndpoint mockThird = getMockEndpoint("mock:third");

        mockFinish.expectedMessageCount(3);
        mockFinish.expectedBodiesReceived("First Message - 3", "Second Message - 3", "Third Message - 3");
        mockFirst.expectedMessageCount(3);
        mockFirst.expectedBodiesReceived("First Message - 1", "Second Message - 1", "Third Message - 1");
        mockSecond.expectedMessageCount(3);
        mockSecond.expectedBodiesReceived("First Message - 2", "Second Message - 2", "Third Message - 2");
        mockThird.expectedMessageCount(3);
        mockThird.expectedBodiesReceived("First Message - 3", "Second Message - 3", "Third Message - 3");

        //when
        template.sendBody("direct:start", "First Message");
        template.sendBody("direct:start", "Second Message");
        template.sendBody("direct:start", "Third Message");

        //then
        assertMockEndpointsSatisfied();

        // additional test to ensure correct endpoints in registry
        assertNotNull(context.hasEndpoint("direct:start"));
        assertNotNull(context.hasEndpoint("direct:first"));
        assertNotNull(context.hasEndpoint("direct:second"));
        assertNotNull(context.hasEndpoint("direct:third"));
        assertNotNull(context.hasEndpoint("mock:first"));
        assertNotNull(context.hasEndpoint("mock:second"));
        assertNotNull(context.hasEndpoint("mock:third"));
        assertNotNull(context.hasEndpoint("mock:finish"));

        context.stop();
    }
}

