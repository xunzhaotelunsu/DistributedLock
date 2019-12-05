package redis.base;

import org.junit.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.Resource;
import java.util.HashSet;

/*
*
 *功能描述 集群
 * @author Liu Xu
 * @date
 * @param
 * @return
 */
public class Demo2 {

    @Resource
    private JedisCluster jedisClients;

    @Test
    public void test1(){
        HashSet<HostAndPort> set = new HashSet<>();
        set.add(new HostAndPort("127.0.0.1",30001));
        set.add(new HostAndPort("127.0.0.1",30002));
        set.add(new HostAndPort("127.0.0.1",30003));
        set.add(new HostAndPort("127.0.0.1",30004));
        set.add(new HostAndPort("127.0.0.1",30005));
        set.add(new HostAndPort("127.0.0.1",30006));
        JedisCluster cluster = new JedisCluster(set);
        String result = cluster.get("a");
        System.out.println(result);
    }
}
