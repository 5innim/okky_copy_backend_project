package com.innim.okkycopy;

import com.innim.okkycopy.global.commons.YamlLoadFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource(value = "classpath:/config/jwt.yml", factory = YamlLoadFactory.class)
public class OkkyCopyApplication {
	public static void main(String[] args) {
		SpringApplication.run(OkkyCopyApplication.class, args);
	}

}
