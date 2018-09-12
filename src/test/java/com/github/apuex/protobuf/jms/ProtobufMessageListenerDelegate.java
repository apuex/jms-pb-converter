package com.github.apuex.protobuf.jms;

import java.util.LinkedList;
import java.util.Queue;

public class ProtobufMessageListenerDelegate {
  private final Queue<Greetings> messages = new LinkedList<>();

  public void handleMessage(Greetings m) {
    synchronized (this) {
      messages.add(m);
      this.notify();
    }
  }

  public Greetings pop() throws InterruptedException {
    synchronized (this) {
      if (messages.isEmpty()) {
        this.wait(5000);
      }
      return messages.remove();
    }
  }
}
