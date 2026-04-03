package com.controlfinance.infrastructure.security;

import com.controlfinance.shared.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class CryptoService {

  private static final String AES_GCM = "AES/GCM/NoPadding";
  private static final int IV_LEN = 12;
  private static final int TAG_BITS = 128;

  private final EncryptionProperties props;
  private final SecureRandom random = new SecureRandom();

  private SecretKey key() {
    try {
      byte[] raw = Base64.getDecoder().decode(props.getKeyBase64());
      return new SecretKeySpec(raw, "AES");
    } catch (Exception e) {
      throw new BadRequestException("Invalid APP_ENCRYPTION_KEY (Base64) configuration");
    }
  }

  public String encrypt(String plain) {
    try {
      byte[] iv = new byte[IV_LEN];
      random.nextBytes(iv);
      Cipher c = Cipher.getInstance(AES_GCM);
      c.init(Cipher.ENCRYPT_MODE, key(), new GCMParameterSpec(TAG_BITS, iv));
      byte[] out = c.doFinal(plain.getBytes(StandardCharsets.UTF_8));
      byte[] combined = new byte[iv.length + out.length];
      System.arraycopy(iv, 0, combined, 0, iv.length);
      System.arraycopy(out, 0, combined, iv.length, out.length);
      return Base64.getEncoder().encodeToString(combined);
    } catch (Exception e) {
      throw new RuntimeException("Encryption failure", e);
    }
  }

  public String decrypt(String cipherTextBase64) {
    try {
      byte[] combined = Base64.getDecoder().decode(cipherTextBase64);
      byte[] iv = new byte[IV_LEN];
      byte[] ct = new byte[combined.length - IV_LEN];
      System.arraycopy(combined, 0, iv, 0, IV_LEN);
      System.arraycopy(combined, IV_LEN, ct, 0, ct.length);
      Cipher c = Cipher.getInstance(AES_GCM);
      c.init(Cipher.DECRYPT_MODE, key(), new GCMParameterSpec(TAG_BITS, iv));
      byte[] out = c.doFinal(ct);
      return new String(out, StandardCharsets.UTF_8);
    } catch (Exception e) {
      throw new RuntimeException("Decryption failure", e);
    }
  }
}
