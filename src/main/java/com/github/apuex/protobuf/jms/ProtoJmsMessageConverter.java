package com.github.apuex.protobuf.jms;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConverter;

public class ProtoJmsMessageConverter implements MessageConverter {
  @Override
  public Object fromMessage(Message<?> message, Class<?> targetClass) {
    return null;
  }

  @Override
  public Message<?> toMessage(Object payload, MessageHeaders headers) {
    return null;
  }
}
