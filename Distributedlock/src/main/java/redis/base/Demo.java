package redis.base;

import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;

public class Demo {

    private Jedis jedis=null;

    @Before
    public void before(){
        jedis =  new Jedis("127.0.0.1",6379);
    }
    public void after(){
        jedis.close();
    }

    @Test
    public void testInsert(){
        jedis.set("test","liuxu");//设置值
        System.out.println(jedis.get("test"));
        jedis.set("test","yangfang");
        System.out.println(jedis.get("test"));
        jedis.setnx("test","huoyanshu");//如果不存在set值，存在没有操作
        System.out.println(jedis.get("test"));
        jedis.setex("test",5,"huoyanshu");//set值，设置一个超时删除时间
        System.out.println(jedis.get("test"));

    }
}
