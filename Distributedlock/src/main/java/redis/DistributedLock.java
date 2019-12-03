package redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.UUID;

/*
*
 *功能描述 使用的是jedis来连接Redis。
 * 实现思想
 *获取锁的时候，使用setnx加锁，并使用expire命令为锁添加一个超时时间，超过该时间则自动释放锁，锁的value值为一个随机生成的UUID，通过此在释放锁的时候进行判断。
 *获取锁的时候还设置一个获取的超时时间，若超过这个时间则放弃获取锁。
 *释放锁的时候，通过UUID判断是不是该锁，若是该锁，则执行delete进行锁释放。
 * @author Liu Xu
 * @date
 * @param
 * @return
 */
public class DistributedLock {
  private final JedisPool jedisPool;


    public DistributedLock(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }
/*
*
 *功能描述 加锁
 * @author Liu Xu
 * @date
 * @param localName 锁的key
 * @param acquireTimeout 获取超时时间
 * @param timeout 锁的超时时间
 * @return 锁标识
 */
    public String lockWithTimeout(String localName,long acquireTimeout,long timeout){
        Jedis conn = null;
        String retIdentifier = null;
        try {
            /**获取连接**/
            conn = jedisPool.getResource();
            /**随机生成一个value**/
            String identifier = UUID.randomUUID().toString();
            /**锁名，集key值**/
            String lockKey = "lock:"+localName;
            /**超时时间，上锁后超过此时间则自动释放**/
            int lockExpire = (int)(timeout/1000);
            // 获取锁的超时时间，超过这个时间则放弃获取锁
            long end = System.currentTimeMillis()+acquireTimeout;
            while (System.currentTimeMillis()<end){
                if(conn.setnx(lockKey,identifier)==1){
                    conn.expire(lockKey,lockExpire);
                    //返回value ，用户释放锁时间确认
                    retIdentifier = identifier;
                    return retIdentifier;
                }
                //返回-1 代表key没有设置超时时间，为key设置一个超时时间
                if(conn.ttl(lockKey)==-1){
                    conn.expire(lockKey,lockExpire);
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();//中断线程
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(conn!=null){
                conn.close();
            }

        }

        return retIdentifier;
    }
/*
*
 *功能描述 释放锁
 * @author Liu Xu
 * @date
 * @param  lockName 锁的key
 * @param  identifier 释放锁的标识
 * @return
 */
    public boolean releaseLock(String lockName, String identifier) {
        Jedis conn = null;
        String lockKey = "lock:" + lockName;
        boolean retFlag = false;
        try {
            conn = jedisPool.getResource();

            while (true) {
                /**监视lock ，准备开始事务**/
                conn.watch(lockKey);
                if (identifier.equals(conn.get(lockKey))) {
                    Transaction transaction = conn.multi();
                    transaction.del(lockKey);
                    List<Object> results = transaction.exec();
                    if (results == null) {
                        continue;
                    }
                    retFlag = true;
                }
                conn.unwatch();
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return retFlag;
    }
}
