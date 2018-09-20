package com.github.apuex.protobuf.jms;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;

import java.net.URI;
import java.security.Principal;
import java.util.Calendar;
import java.util.TimeZone;

import static java.lang.System.out;

public class DefaultProtobufMessageHandler implements ProtobufMessageHandler {
  private final JsonFormat.Printer printer = JsonFormat.printer();

  @Override
  public void handleMessage(Message m, long t, Principal p, URI u) {
    try {
      final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
      calendar.setTimeInMillis(t);
      out.printf("[%s] [%s] [%s] [%s]:\n%s\n",
          calendar.getTime(),
          (p != null ? p.getName() : "-"),
          m.getClass().getName(),
          (u != null ? u.toString() : "-"),
          printer.print(m)
      );
    } catch (InvalidProtocolBufferException e) {
      throw new RuntimeException(e);
    }
  }
}
