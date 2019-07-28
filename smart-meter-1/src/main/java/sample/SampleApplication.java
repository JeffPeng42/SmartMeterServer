package sample;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.extern.slf4j.Slf4j;

@EnableScheduling
@SpringBootApplication
//@EnableConfigServer
@MapperScan("sample.mapper")
@Slf4j
public class SampleApplication {
	
	public static void main(String... args) {
		SpringApplication.run(SampleApplication.class, args);
		
		log.info("Init Sample server success!!!");
	}
}
