package cn.mulanbay.face.api.web;

import cn.mulanbay.face.api.web.listener.StartListener;
import cn.mulanbay.face.api.web.listener.StopListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 系统启动主类
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@SpringBootApplication
@EnableCaching
@EnableTransactionManagement
@ComponentScan(basePackages = "cn.mulanbay.face.api")
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication sa = new SpringApplication(Application.class);
        sa.addListeners(new StartListener());
        sa.addListeners(new StopListener());
        sa.run(args);
        logger.debug("程序启动成功");
    }

}
