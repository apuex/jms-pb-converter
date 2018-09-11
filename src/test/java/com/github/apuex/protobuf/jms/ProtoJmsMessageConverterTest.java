package com.github.apuex.protobuf.jms;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import java.util.LinkedList;
import java.util.List;

public class ProtoJmsMessageConverterTest {
  @Test
  public void testToMessage() throws Exception {
    ProtoJmsMessageConverter converter = getProtoJmsMessageConverter();

    Greetings payload = Greetings.newBuilder()
        .setName("me")
        .build();

    Message message = converter.toMessage(payload, new MessageHeaders(null));

    Assert.assertNotNull(message);
  }

  @Test
  public void testFromMessage() throws Exception {
    ProtoJmsMessageConverter converter = getProtoJmsMessageConverter();

    Greetings payload = Greetings.newBuilder()
        .setName("me")
        .build();

    Message message = converter.toMessage(payload, new MessageHeaders(null));

    Greetings expected = (Greetings) converter.fromMessage(message, Greetings.class);
    Assert.assertEquals(expected, payload);
  }

  private ProtoJmsMessageConverter getProtoJmsMessageConverter() throws Exception {
    ProtoJmsMessageConverter converter = new ProtoJmsMessageConverter();
    List<String> descriptors = new LinkedList<>();
    descriptors.add("/protobuf/descriptor-sets/jms-pb-converter-1.0.0.protobin");
    converter.setProtobufDescriptors(descriptors);
    return converter;
  }
}