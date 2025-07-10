package io.hhplus.tdd.time;

public class TestTimeProvider implements TimeProvider{
    private long fixedTime;
    public TestTimeProvider(long time) {
        this.fixedTime = time;
    }
    @Override
    public long now() {
        return this.fixedTime++;
    }
}
