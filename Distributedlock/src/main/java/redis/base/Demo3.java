package redis.base;

import org.junit.Test;
import redis.clients.jedis.JedisCluster;

import javax.annotation.Resource;

public class Demo3 {
    @Resource
    private JedisCluster jedisClients;

    @Test
    public void test1(){

        String result = jedisClients.get("a");
        System.out.println(result);
    }
}
