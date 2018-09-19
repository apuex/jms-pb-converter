package com.github.apuex.protobuf.jms;

import com.google.protobuf.Message;

public interface ProtobufMessageHandler {
  void handleMessage(Message m, String principalName);
}
