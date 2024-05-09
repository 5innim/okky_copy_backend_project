package com.innim.okkycopy.global.runner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OkkyCopyApplicationRunner implements ApplicationRunner {

    @Value("#{environment['spring.profiles.active']}")
    private String activeProfile;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("active_profile: " + activeProfile);

    }
}
