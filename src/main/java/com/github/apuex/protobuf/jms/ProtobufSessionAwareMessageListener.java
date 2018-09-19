package com.github.apuex.protobuf.jms;

import com.google.protobuf.Message;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Session;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import static com.github.apuex.protobuf.jms.JmsConverterConfig.DEFAULT_PRINCIPAL_NAME_FIELD;
import static com.github.apuex.protobuf.jms.JmsConverterConfig.DEFAULT_SERVICE_URI_FIELD;

public class ProtobufSessionAwareMessageListener implements SessionAwareMessageListener<BytesMessage> {
  private String principalNameField = DEFAULT_PRINCIPAL_NAME_FIELD;
  private String serviceUriField = DEFAULT_SERVICE_URI_FIELD;
  private MessageConverter messageConverter;
  private Map<Class<? extends Message>, ProtobufMessageHandler> messageHandlerMap;

  @Override
  public void onMessage(BytesMessage message, Session session) throws JMSException {
    Message m = (Message) messageConverter.fromMessage(message);
    try {
      messageHandlerMap.get(m.getClass()).handleMessage(m,
          new ProtobufJmsPrincipal(message.getStringProperty(principalNameField)),
          new URI(message.getStringProperty(serviceUriField))
      );
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
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

  public void setMessageHandlerMap(Map<Class<? extends Message>, ProtobufMessageHandler> messageHandlerMap) {
    this.messageHandlerMap = messageHandlerMap;
  }
}
