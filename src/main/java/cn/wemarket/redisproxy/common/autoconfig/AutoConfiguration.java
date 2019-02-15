package cn.wemarket.redisproxy.common.autoconfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import java.util.*;


@Configuration
@ComponentScan
@EnableScheduling
@EnableAsync
@EnableCaching
public class AutoConfiguration {
    private final static Logger LOGGER = LoggerFactory.getLogger(AutoConfiguration.class);

    @Value("${redis.maxTotal:200}")
    private int maxTotal;

    @Value("${redis.maxIdle:100}")
    private int maxIdle;

    @Value("${redis.minIdle:8}")
    private int minIdle;

    @Value("${redis.maxActive:300}")
    private int maxActive;

    @Value("${redis.maxWaitMillis:100000}")
    private long maxWaitMillis;

    @Value("${redis.testOnBorrow:true}")
    private boolean testOnBorrow;

    @Value("${redis.testOnReturn:true}")
    private boolean testOnReturn;

    @Value("${redis.testWhileIdle:true}")
    private boolean testWhileIdle;

    @Value("${redis.timeBetweenEvictionRunsMillis:30000}")
    private long timeBetweenEvictionRunsMillis;

    @Value("${redis.numTestsPerEvictionRun:10}")
    private int numTestsPerEvictionRun;

    @Value("${redis.minEvictableIdleTimeMillis:60000}")
    private long minEvictableIdleTimeMillis;

    @Value("redis.timeout:100000")
    private int timeout;

    @Value("${redis.sentinel.nodes}")
    private String nodes;

    @Value("${redis.sentinel.master}")
    private String masterName;

    @Value("${redis.password}")
    private String password;

    @Bean("jedisPoolConfig")
    public JedisPoolConfig initJedisPoolConfig(){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxWaitMillis(maxWaitMillis);
        poolConfig.setTestOnBorrow(testOnBorrow);
        poolConfig.setTestOnReturn(testOnReturn);
        poolConfig.setTestWhileIdle(testWhileIdle);
        poolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        poolConfig.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
        poolConfig.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);

        return poolConfig;
    }

    @Bean(value = "sentinelPool")
    public JedisSentinelPool initJedisPool(
            @Qualifier(value = "jedisPoolConfig") JedisPoolConfig jedisPoolConfig
    ){

        Set<String> nodeSet = new HashSet<>();
        //判断字符串是否为空
        if(nodes == null || "".equals(nodes)){
            LOGGER.error("RedisSentinelConfiguration initialize error nodeString is null");
            throw new RuntimeException("RedisSentinelConfiguration initialize error nodeString is null");
        }
        String[] nodeArray = nodes.split(",");
        //判断是否为空
        if(nodeArray == null || nodeArray.length == 0){
            LOGGER.error("RedisSentinelConfiguration initialize error nodeArray is null");
            throw new RuntimeException("RedisSentinelConfiguration initialize error nodeArray is null");
        }
        //循环注入至Set中
        for(String node : nodeArray){
            nodeSet.add(node);
        }
        //创建连接池对象
        JedisSentinelPool jedisPool =
                new JedisSentinelPool(masterName ,nodeSet ,jedisPoolConfig ,timeout , password);

        return jedisPool;
    }

    /*@Bean("redisSentinelConfiguration")
    public RedisSentinelConfiguration initRedisSentinelConfiguration(){
        Set<String> sentinels = new HashSet<>();
        //判断字符串是否为空
        if(nodes == null || "".equals(nodes)){
            LOGGER.error("RedisSentinelConfiguration initialize error nodeString is null");
            throw new RuntimeException("RedisSentinelConfiguration initialize error nodeString is null");
        }
        String[] nodeArray = nodes.split(",");
        //判断是否为空
        if(nodeArray == null || nodeArray.length == 0){
            LOGGER.error("RedisSentinelConfiguration initialize error nodeArray is null");
            throw new RuntimeException("RedisSentinelConfiguration initialize error nodeArray is null");
        }
        //循环注入至Set中
        for(String node : nodeArray){
            sentinels.add(node);
        }
        RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration(masterName, sentinels);

        return redisSentinelConfiguration;
    }

    @Bean("jedisConnectionFactory")
    public JedisConnectionFactory initJedisConnectionFactory(
            @Qualifier("redisSentinelConfiguration") RedisSentinelConfiguration redisSentinelConfiguration,
            @Qualifier("jedisPoolConfig") JedisPoolConfig jedisPoolConfig
    ){
        JedisConnectionFactory jedisConnectionFactory =
                new JedisConnectionFactory(redisSentinelConfiguration, jedisPoolConfig);

        return jedisConnectionFactory;
    }

    @Bean("redisTemplate")
    public RedisTemplate initRedisTemplate(
            @Qualifier("jedisConnectionFactory") JedisConnectionFactory jedisConnectionFactory
    ){
        RedisTemplate redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);

        return redisTemplate;
    }*/
}
