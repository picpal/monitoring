package com.picpal.framework.common.config;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableEncryptableProperties
public class JasyptConfig {
    
    private String getKey() {
        String pre = System.getProperty("jasypt.enc.pre");
        String post = System.getProperty("jasypt.enc.post");
        
        // Fallback to hardcoded key if system properties are not set
        if (pre == null || post == null) {
            log.warn("Jasypt system properties not found, using default key");
            return "00001111"; // ""로 변경해서 사용필요.
        }
        
        return pre + post;
    }

    @Bean("jasyptEncryptorAES")
    public StringEncryptor stringEncryptor(){
        String key = getKey();
        log.info("Using Jasypt key: {}", key);

        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(key); //암호화할 때 사용하는 키
        config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256"); // 암호화 알고리즘
        config.setKeyObtentionIterations("1000"); // 반복할 해싱 횟수
        config.setPoolSize("1"); // 인스턴스 Pool
        config.setProviderName("SunJCE"); // 프로바이더
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator"); // salt 생성 클래스 지정
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64"); // 인코딩
        encryptor.setConfig(config); // 설정 정보 Set

        return encryptor;
    }
}