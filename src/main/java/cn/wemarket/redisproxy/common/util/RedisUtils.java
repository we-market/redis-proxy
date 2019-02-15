package cn.wemarket.redisproxy.common.util;

import cn.wemarket.redisproxy.common.exception.SysException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import java.util.List;

public class RedisUtils {
    private final static Logger LOGGER = LoggerFactory.getLogger(RedisUtils.class);
    private static final String ERROR_LOG_PREFIX = "Redis server proxy jedis.";
    private static final String GENERAL_SYSTEM_ERROR_LOG = "Redis operation has error: ";

    @Value("redis.sentinel.master")
    private static String masterName;

    /**
     * Jedis对象池 所有Jedis对象均通过该池租赁获取
     */
    @Autowired
    @Qualifier("sentinelPool")
    private static JedisSentinelPool sentinelPool;

    /**
     * 缓存数据,数据不过期
     */
    public static String set(String key, String value) throws Exception {
        try(Jedis jedis = sentinelPool.getResource()){
            return jedis.set(key, value);
        }catch (Exception e){
            LOGGER.debug(ERROR_LOG_PREFIX +"set has error: ", e);
            throw new SysException(GENERAL_SYSTEM_ERROR_LOG, e);
        }
    }

    /**
     * 缓存数据并设置超时时间
     */
    public static String set(String key, String value, int timeout) throws Exception{
        try(Jedis jedis = sentinelPool.getResource()) {
            return jedis.setex(key, timeout, value);
        }catch (Exception e){
            LOGGER.debug(ERROR_LOG_PREFIX +"set has error: ", e);
            throw new SysException(GENERAL_SYSTEM_ERROR_LOG, e);
        }
    }

    /**
     * 缓存数据并设置缓存标准与超时时间
     * @param nxxx NX|XX, NX -- Only set the key if it does not already exist. XX -- Only set the key
     *          if it already exist.
     * @param expx EX|PX, expire time units: EX = seconds; PX = milliseconds
     * */
    public static String set(String key, String value, String nxxx, String expx, long time){
        try(Jedis jedis = sentinelPool.getResource()) {
            return jedis.set(key, value, nxxx, expx, time);
        }catch (Exception e){
            LOGGER.debug(ERROR_LOG_PREFIX +"set has error: ", e);
            throw new SysException(GENERAL_SYSTEM_ERROR_LOG, e);
        }
    }

    /**
     * 设置缓存数据过期时间
     * */
    public static void setTtl(String key, int seconds){
        try(Jedis jedis = sentinelPool.getResource()) {
            Long result = jedis.expire(key, seconds);
            if (result != 1L){
                LOGGER.debug(ERROR_LOG_PREFIX +"set has error: 缓存数据已设置过过期时间或数据不存在");
                throw new SysException(GENERAL_SYSTEM_ERROR_LOG+"缓存数据已设置过过期时间或数据不存在");
            }
        }catch (Exception e){
            LOGGER.debug(ERROR_LOG_PREFIX +"set has error: ", e);
            throw new SysException(GENERAL_SYSTEM_ERROR_LOG, e);
        }
    }

    /**
     * 判断数据是否已经存在
     * */
    public static boolean isExist(String key){
        try(Jedis jedis = sentinelPool.getResource()){
            return jedis.exists(key);
        }catch (Exception e){
            LOGGER.debug(ERROR_LOG_PREFIX +"judge exist has error: ", e);
            throw new SysException(GENERAL_SYSTEM_ERROR_LOG, e);
        }
    }

    /**
     * 获取缓存数据
     * */
    public static String get(String key) throws Exception{
        try(Jedis jedis = sentinelPool.getResource()) {
            return jedis.get(key);
        }catch (Exception e){
            LOGGER.debug(ERROR_LOG_PREFIX +"get has error: ", e);
            throw new SysException(GENERAL_SYSTEM_ERROR_LOG, e);
        }
    }

    /**
     * 获取缓存数据过期时间
     * */
    public static long getTtl(String key) throws Exception{
        try(Jedis jedis = sentinelPool.getResource()){
            return jedis.ttl(key);
        }catch (Exception e){
            LOGGER.debug(ERROR_LOG_PREFIX +"get has error: ", e);
            throw new SysException(GENERAL_SYSTEM_ERROR_LOG, e);
        }
    }

    /**
     * 删除单个缓存数据
     * */
    public static Long del(String key) throws Exception{
        try(Jedis jedis = sentinelPool.getResource()){
            return jedis.del(key);
        }catch (Exception e){
            LOGGER.debug(ERROR_LOG_PREFIX +"delete has error: ", e);
            throw new SysException(GENERAL_SYSTEM_ERROR_LOG, e);
        }
    }

    /**
     * 删除多个缓存数据
     * */
    public static Long del(String... keys) throws Exception{
        try(Jedis jedis = sentinelPool.getResource()){
            return jedis.del(keys);
        }catch (Exception e){
            LOGGER.debug(ERROR_LOG_PREFIX +"delete has error: ", e);
            throw new SysException(GENERAL_SYSTEM_ERROR_LOG, e);
        }
    }

    /**
     * 删除多个缓存数据
     * */
    public static void del(List<String> keys) throws Exception{
        try(Jedis jedis = sentinelPool.getResource()){
            for (String key:keys) {
                jedis.del(key);
            }
        }catch (Exception e){
            LOGGER.debug(ERROR_LOG_PREFIX +"delete has error: ", e);
            throw new SysException(GENERAL_SYSTEM_ERROR_LOG, e);
        }
    }
}
