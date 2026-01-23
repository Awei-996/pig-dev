package cn.com.k12code.pigseckill;

import com.pig4cloud.pig.common.security.annotation.EnablePigResourceServer;
import com.pig4cloud.pig.common.swagger.annotation.EnablePigDoc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 秒杀服务应用启动类
 *
 * @author carl
 * @date 2025/01/23
 */
@EnablePigDoc("seckill")
@EnablePigResourceServer
@EnableDiscoveryClient
@SpringBootApplication
public class PigSeckillApplication {

	public static void main(String[] args) {
		SpringApplication.run(PigSeckillApplication.class, args);
	}

}
