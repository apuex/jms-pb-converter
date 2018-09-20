package com.github.apuex.protobuf.jms;

import com.google.protobuf.Message;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Session;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static com.github.apuex.protobuf.jms.JmsConverterConfig.DEFAULT_PRINCIPAL_NAME_FIELD;
import static com.github.apuex.protobuf.jms.JmsConverterConfig.DEFAULT_SERVICE_URI_FIELD;

public class ProtobufSessionAwareMessageListener implements SessionAwareMessageListener<BytesMessage> {
  private String principalNameField = DEFAULT_PRINCIPAL_NAME_FIELD;
  private String serviceUriField = DEFAULT_SERVICE_URI_FIELD;
  private MessageConverter messageConverter;
  private ProtobufMessageHandler defaultMessageHandler = new DefaultProtobufMessageHandler();
  private Map<Class<? extends Message>, ProtobufMessageHandler> messageHandlerMap = new HashMap<>();

  @Override
  public void onMessage(BytesMessage message, Session session) throws JMSException {
    Message m = (Message) messageConverter.fromMessage(message);
    try {
      String p = message.getStringProperty(principalNameField);
      String u = message.getStringProperty(serviceUriField);
      message.getJMSTimestamp();
      getMessageHandler(m.getClass()).handleMessage(m,
          message.getJMSTimestamp(),
          (p != null ? new ProtobufJmsPrincipal(p): null),
          (u != null ? new URI(u): null)
      );
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  protected ProtobufMessageHandler getMessageHandler(Class<? extends Message> clazz) {
    return messageHandlerMap.getOrDefault(clazz, defaultMessageHandler);
  }

  public void setMessageConverter(MessageConverter messageConverter) {
    this.messageConverter = messageConverter;
  }

  public void setPrincipalNameField(String principalNameField) {
    this.principalNameField = principalNameField;
  }

  public void setServiceUriField(String serviceUriField) {
    this.serviceUriField = serviceUriField;
  }

  public void setDefaultMessageHandler(ProtobufMessageHandler messageHandler) {
    this.defaultMessageHandler = messageHandler;
  }

  public void setMessageHandlerMap(Map<Class<? extends Message>, ProtobufMessageHandler> messageHandlerMap) {
    this.messageHandlerMap = messageHandlerMap;
  }
}
