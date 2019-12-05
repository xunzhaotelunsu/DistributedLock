package redis.lock;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/*
*
 *功能描述
 * @author Liu Xu
 * @date
 * @param
 * @return
 */
public class RedisService {
    private static JedisPool pool = null;

    static {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(200);//设置最大连接数
        jedisPoolConfig.setMaxIdle(8);//设置最大空闲数
        jedisPoolConfig.setMaxWaitMillis(1000* 100);//最大等待时间 100秒
        jedisPoolConfig.setTestOnBorrow(true);//在borrow 一个jedis实例时，是否需要验证，若为true ，则所有jedis实例均是可用的
        pool = new JedisPool(jedisPoolConfig, "127.0.0.1", 6379, 3000);
    }
    DistributedLock lock = new DistributedLock(pool);
    int n = 500;
    public void seckill(){
        String identifier = lock.lockWithTimeout("resource", 5000, 1000);
        System.out.println(Thread.currentThread().getName()+"获取锁");
        System.out.println(--n);
        lock.releaseLock("resource",identifier);
    }
}
