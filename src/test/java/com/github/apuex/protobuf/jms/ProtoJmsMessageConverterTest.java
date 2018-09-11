package com.github.apuex.protobuf.jms;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.*;
import java.util.LinkedList;
import java.util.List;

public class ProtoJmsMessageConverterTest {

  @Test
  public void testToMessage() throws Exception {
    Greetings payload = Greetings.newBuilder()
        .setName("me")
        .build();

    JmsTemplate template = (JmsTemplate) context.getBean(JmsTemplate.class);
    template.send(session -> {
      Message message = template.getMessageConverter().toMessage(payload, session);
      Assert.assertNotNull(message);

      return message;
    });
  }

  @Test
  public void testFromMessage() throws Exception {
    JmsTemplate template = (JmsTemplate) context.getBean(JmsTemplate.class);

    Greetings payload = Greetings.newBuilder()
        .setName("me")
        .build();

    template.send(session -> {
      Message message = template.getMessageConverter().toMessage(payload, session);
      Assert.assertNotNull(message);

      Greetings expected = (Greetings) template.getMessageConverter().fromMessage(message);
      Assert.assertEquals(expected, payload);

      return message;
    });
  }

  ClassPathXmlApplicationContext context;

  @Before
  public void before() {
    context = new ClassPathXmlApplicationContext("app-config.xml");
  }

  @After
  public void after() {
    context.close();
  }
}