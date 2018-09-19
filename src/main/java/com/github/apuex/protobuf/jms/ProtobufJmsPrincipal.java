package com.github.apuex.protobuf.jms;

import java.security.Principal;
import java.util.Objects;

public final class ProtobufJmsPrincipal implements Principal {
  private final String name;

  ProtobufJmsPrincipal(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ProtobufJmsPrincipal that = (ProtobufJmsPrincipal) o;
    return Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public String toString() {
    return "ProtobufJmsPrincipal{" +
        "name='" + name + '\'' +
        '}';
  }
}
