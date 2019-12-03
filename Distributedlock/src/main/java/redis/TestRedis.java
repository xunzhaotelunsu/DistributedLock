package redis;

public class TestRedis {
    public static void main(String[] args) {
        RedisService service = new RedisService();
        for (int i=0;i<50;i++){
            ThreadA threadA = new ThreadA(service);
            threadA.start();
        }
    }
}
