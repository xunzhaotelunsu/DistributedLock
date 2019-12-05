package redis.lock;

public class ThreadA extends Thread {
    private RedisService service;

    @Override
    public void run() {
        service.seckill();
    }
    public ThreadA(RedisService service){
        this.service = service;
    }

}
