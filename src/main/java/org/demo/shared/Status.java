package org.demo.shared;

import lombok.Value;

@Value
public class Status {
    boolean ready;
    long progress;
    long timeBehindInMillis;
}
