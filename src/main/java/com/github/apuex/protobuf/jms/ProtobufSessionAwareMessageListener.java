package com.github.apuex.protobuf.jms;

import com.google.protobuf.Message;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Session;
import java.util.Map;

public class ProtobufSessionAwareMessageListener implements SessionAwareMessageListener<BytesMessage> {
  private MessageConverter messageConverter;
  private Map<Class<? extends Message>, ProtobufMessageHandler> messageHandlerMap;

  @Override
  public void onMessage(BytesMessage message, Session session) throws JMSException {
    Message m = (Message) messageConverter.fromMessage(message);
    messageHandlerMap.get(m.getClass()).handleMessage(m, message.getStringProperty("principalName"));
  }

  public void setMessageConverter(MessageConverter messageConverter) {
    this.messageConverter = messageConverter;
  }

  public void setMessageHandlerMap(Map<Class<? extends Message>, ProtobufMessageHandler> messageHandlerMap) {
    this.messageHandlerMap = messageHandlerMap;
  }
}