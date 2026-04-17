package com.example.localvault.global.util;

import com.example.localvault.global.exception.BusinessException;
import com.example.localvault.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class CryptoUtil {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int IV_LENGTH = 12;

    private final SecretKeySpec secretKey;

    public CryptoUtil(@Value("${vault.encryption-key}") String key) {
        // 키를 32바이트로 맞춤
        byte[] keyBytes = new byte[32];
        byte[] originalBytes = key.getBytes();
        System.arraycopy(originalBytes, 0, keyBytes, 0, Math.min(originalBytes.length, 32));
        this.secretKey = new SecretKeySpec(keyBytes, "AES");
    }

    // 암호화
    public EncryptedData encrypt(String plainText) {
        try {
            byte[] iv = generateIV();
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());

            return new EncryptedData(
                    Base64.getEncoder().encodeToString(encryptedBytes),
                    Base64.getEncoder().encodeToString(iv)
            );
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.CRYPT_ERROR);
        }
    }

    // 복호화
    public String decrypt(String cipherText, String ivString) {
        try {
            byte[] iv = Base64.getDecoder().decode(ivString);
            byte[] encryptedBytes = Base64.getDecoder().decode(cipherText);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.CRYPT_ERROR);
        }
    }

    // IV 생성
    private byte[] generateIV() {
        byte[] iv = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    // 암호화 결과 클래스
    public record EncryptedData(String cipherText, String iv) {}
}