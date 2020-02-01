package com.xuecheng.auth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Administrator
 * @version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRedis {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    //创建jwt令牌
    @Test
    public void testRedis(){
        //定义key
        String key = "user_token:e4bfb87d-2385-43cb-ab2b-114f6aab9235";
        //定义value
        Map<String,String> value = new HashMap<>();
        value.put("jwt","eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOm51bGwsInVzZXJwaWMiOm51bGwsInVzZXJfbmFtZSI6Iml0Y2FzdCIsInNjb3BlIjpbImFwcCJdLCJuYW1lIjpudWxsLCJ1dHlwZSI6bnVsbCwiaWQiOm51bGwsImV4cCI6MTU4MDEzNzE0NSwianRpIjoiZTRiZmI4N2QtMjM4NS00M2NiLWFiMmItMTE0ZjZhYWI5MjM1IiwiY2xpZW50X2lkIjoiWGNXZWJBcHAifQ.Vwtyow8S18LYcQdMFIunA9C5mgEsv2FWscILdNT0rlfQrvOAlkjGLRsTRtJrLrVlo_ya9N9omxTXV3PN6XqyYTEcCKLlItyIlBdXHcPyg_qnH7Yld9ZmsDoDorE7Hz4b9tQCuL1qVuj5oyhnSiClXeFzyUCi5w8RruSQVoa995ZDJ3tzB3X__RvBlu3w8h6X5-_R8snpqVF9FJhkJCjAGJVD7DMFSYBD1G-P04Y2MpTxS-osCu1UnBdRGhyoa8TggmB6JT3oM2-fCDrbj9RSCOqSoys3vnSMwQeQcLUWv8edi5grabNofcgv-KhKApq1QHsekvCGrEv_WJ59LMYVSg");
        value.put("refresh_token","eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOm51bGwsInVzZXJwaWMiOm51bGwsInVzZXJfbmFtZSI6Iml0Y2FzdCIsInNjb3BlIjpbImFwcCJdLCJhdGkiOiJlNGJmYjg3ZC0yMzg1LTQzY2ItYWIyYi0xMTRmNmFhYjkyMzUiLCJuYW1lIjpudWxsLCJ1dHlwZSI6bnVsbCwiaWQiOm51bGwsImV4cCI6MTU4MDEzNzE0NSwianRpIjoiNDYzNzZjMDItOWJlOS00YzYyLWFmNzEtZWE5ZGYyYjMyNTE4IiwiY2xpZW50X2lkIjoiWGNXZWJBcHAifQ.egJ-GcNSkRjBHa0m-Sh5rZYRxzEaVTyTxbGbhnB_iudSRvp98IXpzzaNiEZqnTXnoCfWIIxa_jr6xlSlEY2l0lYIkrsL_zY63BxqXrbSpxDdhlhzzkLmlkOULHhzNjZp7qH9jhmxNC_II3f7Yosl_gwDd88WEm8EntlvEDQuK4cXXVwpNsdbUT6j88qzNUguuZpanPOM3C51pdf5aNCxGBFHV9FEaHBd1H0vogtsopouuqcjBanRovPdmyCiS4rYf6OsLcZNvVrL57nbWh4NJ-XuZ6-dmkY86ebDKu9cHYKdkUC9ie99_4SRzNoAqGEwFaiRK4oESFCR-ClFwjmPdg");
        String jsonString = JSON.toJSONString(value);
        //校验key是否存在，如果不存在则返回-2
        Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        System.out.println(expire);
        //存储数据
        stringRedisTemplate.boundValueOps(key).set(jsonString,30, TimeUnit.SECONDS);
        //获取数据
        String string = stringRedisTemplate.opsForValue().get(key);
        System.out.println(string);


    }


}
