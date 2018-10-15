package com.github.apuex.protobuf.jms;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.out;

public class ProtoJmsMessageConverter implements MessageConverter {
  private final Map<String, Parser<? extends com.google.protobuf.Message>> msgParsers = new HashMap<>();

  @Override
  public Object fromMessage(Message message) throws JMSException, MessageConversionException {
    if (message instanceof BytesMessage) {
      BytesMessage bm = (BytesMessage) message;
      try {
        Parser<? extends com.google.protobuf.Message> parser = msgParsers.get(message.getJMSType());
        if (parser != null) {
          byte[] bytes = new byte[(int) bm.getBodyLength()];
          bm.readBytes(bytes);
          return parser.parseFrom(bytes);
        } else {
          throw new RuntimeException("No parser available.");
        }
      } catch (InvalidProtocolBufferException e) {
        throw new RuntimeException(e);
      }
    } else {
      throw new RuntimeException("Invalid message type: not a javax.jms.BytesMessage.");
    }
  }

  @Override
  public Message toMessage(Object payload, Session session) throws JMSException, MessageConversionException {
    if (isProtobufMessage(payload)) {
      if (!msgParsers.containsKey(payload.getClass().getName())) {
        throw new RuntimeException(String.format("Not supported message: %s.", payload.getClass().getName()));
      }
      com.google.protobuf.Message message = (com.google.protobuf.Message) payload;
      BytesMessage bm = session.createBytesMessage();
      bm.setJMSType(payload.getClass().getName());
      bm.writeBytes(message.toByteArray());
      return bm;
    } else {
      throw new RuntimeException(String.format("Not a protobuf message: %s.", payload.getClass().getName()));
    }
  }

  public void setProtobufDescriptors(List<String> l) throws Exception {
    StringBuilder classListBuilder = new StringBuilder();
    for (String name : l) {
      InputStream input = getClass().getResourceAsStream(name);
      if (input == null) throw new RuntimeException(String.format("descriptor `%s` not found.", name));
      DescriptorProtos.FileDescriptorSet descriptorSet = DescriptorProtos.FileDescriptorSet.parseFrom(input);
      for (DescriptorProtos.FileDescriptorProto fdp : descriptorSet.getFileList()) {
        String packageName = fdp.getOptions().getJavaPackage();
        for (DescriptorProtos.DescriptorProto md : fdp.getMessageTypeList()) {
          String className = String.format("%s.%s", packageName, md.getName());
          classListBuilder.append(className).append("\n");
          Class<com.google.protobuf.Message> clazz = (Class<com.google.protobuf.Message>) Class.forName(className);
          com.google.protobuf.Message defaultInstance = Internal.getDefaultInstance(clazz);
          msgParsers.put(className, defaultInstance.getParserForType());
        }
      }
    }
    out.println(classListBuilder.toString());
  }

  private boolean isProtobufMessage(Class<?> targetClass) {
    if (targetClass != null) {
      Class<?>[] interfaces = targetClass.getInterfaces();
      Class<?> clazz = targetClass.getSuperclass();
      if (clazz != null) {
        if (isProtobufMessage(clazz)) return true;
      }
      for (Class<?> c : interfaces) {
        if (c.equals(com.google.protobuf.Message.class)) {
          return true;
        } else {
          if (isProtobufMessage(c)) return true;
        }
      }
    }
    return false;
  }

  private boolean isProtobufMessage(Object object) {
    if (object != null) {
      return (object instanceof com.google.protobuf.Message);
    }
    return false;
  }
}
