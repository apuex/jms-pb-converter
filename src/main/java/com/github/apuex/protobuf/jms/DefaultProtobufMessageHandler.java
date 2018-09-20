package com.github.apuex.protobuf.jms;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;

import java.net.URI;
import java.security.Principal;

import static java.lang.System.out;

public class DefaultProtobufMessageHandler implements ProtobufMessageHandler {
  private final JsonFormat.Printer printer = JsonFormat.printer();

  @Override
  public void handleMessage(Message m, Principal p, URI service) {
    try {
      out.printf("%s %s:\n%s\n",
          m.getClass().getName(),
          (p != null ? p.getName() : ""),
          printer.print(m)
      );
    } catch (InvalidProtocolBufferException e) {
      throw new RuntimeException(e);
    }
  }
}
