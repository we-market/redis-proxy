package cn.wemarket.redisproxy;

import cn.wemarket.redisproxy.biz.server.RedisProxyApplication;
import cn.wemarket.redisproxy.common.util.RedisUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RedisProxyApplication.class})
public class RedisProxyApplicationTests {

    @Test
    public void RedisUtilsTest() {
        try {
            RedisUtils.set("testKey1", "testValue1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

