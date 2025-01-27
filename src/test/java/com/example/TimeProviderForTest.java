package com.example;

import java.time.LocalDateTime;
import java.time.Month;

public class TimeProviderForTest implements TimeProvider {

    @Override
    public LocalDateTime getCurrentTime() {
        return LocalDateTime.of(2025, Month.JANUARY,27,12, 0);
    }
}
