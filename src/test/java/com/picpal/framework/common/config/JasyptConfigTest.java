package com.picpal.framework.common.config;

import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class JasyptConfigTest {
    @Test
    void stringEncryptor() {
        String username = "admin";
        String password = "admin123";

        log.info("####### jasypt ENC username : {} ",jasyptEncoding(username));
        log.info("####### jasypt ENC password : {} ",jasyptEncoding(password));
    }

    public String jasyptEncoding(String value) {
        String key = "00001111"; // 암호화 키
        StandardPBEStringEncryptor pbeEnc = new StandardPBEStringEncryptor();
        pbeEnc.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        pbeEnc.setPassword(key);
        pbeEnc.setIvGenerator(new RandomIvGenerator());
        return pbeEnc.encrypt(value);
    }
}