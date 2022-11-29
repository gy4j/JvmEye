package com.gy4j.jvm.eye.demo.spring;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/16-17:19
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@SpringBootApplication
@MapperScan("com.gy4j.jvm.eye.demo.spring.mapper")
public class DemoApplication {
    private static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);
    private static Random random = new Random();
    private int illegalArgumentCount = 0;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
        DemoApplication demo = new DemoApplication();
        while (true) {
            try {
                demo.run();
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void run() throws InterruptedException {
        try {
            int number = random.nextInt() / 10000;
            List<Integer> primeFactors = primeFactors(number);
            print(number, primeFactors);
        } catch (Exception e) {
            logger.info(String.format("illegalArgumentCount:%3d, ", illegalArgumentCount) + e.getMessage());
        }
    }

    public static void print(int number, List<Integer> primeFactors) {
        StringBuffer sb = new StringBuffer(number + "=");
        for (int factor : primeFactors) {
            sb.append(factor).append('*');
        }
        if (sb.charAt(sb.length() - 1) == '*') {
            sb.deleteCharAt(sb.length() - 1);
        }
        logger.info(sb.toString());
    }

    public List<Integer> primeFactors(int number) {
        if (number < 2) {
            illegalArgumentCount++;
            throw new IllegalArgumentException("number is: " + number + ", need >= 2");
        }
        List<Integer> result = new ArrayList<Integer>();
        int i = 2;
        while (i <= number) {
            if (number % i == 0) {
                result.add(i);
                number = number / i;
                i = 2;
            } else {
                i++;
            }
        }

        return result;
    }
}
