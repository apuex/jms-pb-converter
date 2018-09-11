package com.github.apuex.protobuf.jms;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.util.MimeTypeUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProtoJmsMessageConverter implements MessageConverter {
  public static final String MSG_TYPE = "msgType";
  private final Map<String, Parser<? extends com.google.protobuf.Message>> msgParsers = new HashMap<>();

  @Override
  public Object fromMessage(Message<?> message, Class<?> targetClass) {
    if (isProtobufMessage(targetClass)
        && MimeTypeUtils.APPLICATION_OCTET_STREAM.equals(message.getHeaders().get(MessageHeaders.CONTENT_TYPE))) {
      try {
        Parser<? extends com.google.protobuf.Message> parser = msgParsers.get(message.getHeaders().get(MSG_TYPE));
        if (parser != null) {
          return parser.parseFrom((byte[]) message.getPayload());
        } else {
          return null;
        }
      } catch (InvalidProtocolBufferException e) {
        throw new RuntimeException(e);
      }
    } else {
      return null;
    }
  }

  @Override
  public Message<?> toMessage(Object payload, MessageHeaders headers) {
    if (isProtobufMessage(payload)) {
      Parser<? extends com.google.protobuf.Message> parser = msgParsers.get(payload.getClass().getName());
      if (parser == null) {
        return null;
      }
      com.google.protobuf.Message message = (com.google.protobuf.Message) payload;
      return new Message<Object>() {
        @Override
        public Object getPayload() {
          return message.toByteArray();
        }

        @Override
        public MessageHeaders getHeaders() {
          final Map<String, Object> extra = new HashMap<>();
          extra.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_OCTET_STREAM);
          extra.put(MSG_TYPE, payload.getClass().getName());
          headers.entrySet().forEach(e -> extra.put(e.getKey(), e.getValue()));
          return new MessageHeaders(extra);
        }
      };
    } else {
      return null;
    }
  }

  public void setProtobufDescriptors(List<String> l) throws Exception {
    for (String name : l) {
      InputStream input = Class.class.getResourceAsStream(name);
      DescriptorProtos.FileDescriptorSet descriptorSet = DescriptorProtos.FileDescriptorSet.parseFrom(input);
      for (DescriptorProtos.FileDescriptorProto fdp : descriptorSet.getFileList()) {
        String packageName = fdp.getOptions().getJavaPackage();
        for (DescriptorProtos.DescriptorProto md : fdp.getMessageTypeList()) {
          String className = String.format("%s.%s", packageName, md.getName());
          Class<com.google.protobuf.Message> clazz = (Class<com.google.protobuf.Message>) Class.forName(className);
          com.google.protobuf.Message defaultInstance = Internal.getDefaultInstance(clazz);
          msgParsers.put(className, defaultInstance.getParserForType());
        }
      }
    }
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
