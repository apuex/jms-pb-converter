package com.github.apuex.protobuf.jms;

import com.google.protobuf.Message;

import java.security.Principal;

public interface ProtobufMessageHandler {
  void handleMessage(Message m, String principalName);
}
