package com.github.apuex.protobuf.jms;

import org.junit.Assert;
import org.junit.Test;

import javax.jms.*;
import java.util.LinkedList;
import java.util.List;

public class ProtoJmsMessageConverterTest {
  @Test
  public void testToMessage() throws Exception {
    ProtoJmsMessageConverter converter = getProtoJmsMessageConverter();

    Greetings payload = Greetings.newBuilder()
        .setName("me")
        .build();

    Session session = getJmsSession();
    Message message = converter.toMessage(payload, session);

    Assert.assertNotNull(message);
    session.close();
  }

  @Test
  public void testFromMessage() throws Exception {
    ProtoJmsMessageConverter converter = getProtoJmsMessageConverter();

    Greetings payload = Greetings.newBuilder()
        .setName("me")
        .build();

    Session session = getJmsSession();
    Message message = converter.toMessage(payload, session);

    Greetings expected = (Greetings) converter.fromMessage(message);
    Assert.assertEquals(expected, payload);
    session.close();
  }

  private ProtoJmsMessageConverter getProtoJmsMessageConverter() throws Exception {
    ProtoJmsMessageConverter converter = new ProtoJmsMessageConverter();
    List<String> descriptors = new LinkedList<>();
    descriptors.add("/protobuf/descriptor-sets/jms-pb-converter-1.0.0.protobin");
    converter.setProtobufDescriptors(descriptors);
    return converter;
  }

  private Session getJmsSession() {
    return null;
  }
}