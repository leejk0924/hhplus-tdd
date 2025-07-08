package io.hhplus.tdd.time;

import org.springframework.stereotype.Component;

@Component
public class SystemTimeProvider implements TimeProvider{
    @Override
    public long now() {
        return System.currentTimeMillis();
    }
}
