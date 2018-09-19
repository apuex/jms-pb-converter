package com.github.apuex.protobuf.jms;

import com.google.protobuf.Message;

import java.net.URI;
import java.security.Principal;

/**
 * Stub interface for handling events published by services.
 */
public interface ProtobufMessageHandler {
  /**
   * Consumes events subscribed from outer system services.
   * @param m event, or message
   * @param p security principal of the subject that issue the request to the service
   * @param service the uri which identifies the service, which is the event source.
   */
  void handleMessage(Message m, Principal p, URI service);
}
