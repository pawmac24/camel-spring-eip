package com.pawel.fuse.example;

import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pmackiewicz on 2016-08-25.
 */
public class RecipientListTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/recipient-list-context.xml");
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

        mockFinish.expectedMessageCount(4);
        mockFinish.expectedBodiesReceived("First Message - 1", "Second Message - 2", "Third Message - 3", "Super Message - *");
        mockFirst.expectedMessageCount(4);
        mockFirst.expectedBodiesReceived("First Message - 1", "Second Message - 1", "Third Message - 1", "Super Message - 1");
        mockSecond.expectedMessageCount(2);
        mockSecond.expectedBodiesReceived("Second Message - 2", "Third Message - 2");
        mockThird.expectedMessageCount(1);
        mockThird.expectedBodiesReceived("Third Message - 3");

        //when
        template.sendBodyAndHeader("direct:start", "First Message", "recipients", "direct:first");
        template.sendBodyAndHeader("direct:start", "Second Message", "recipients", "direct:first, direct:second");
        template.sendBodyAndHeader("direct:start", "Third Message", "recipients", "direct:first, direct:second, direct:third");
        Map<String, Object> myHeaders = new HashMap<>();
        myHeaders.put("recipients", "direct:first");
        myHeaders.put("super-recipient", "super");
        template.sendBodyAndHeaders("direct:start", "Super Message", myHeaders);

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

