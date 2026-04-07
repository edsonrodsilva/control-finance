package com.controlfinance.modules.user.domain.services;

import com.controlfinance.common.exceptions.BadRequestException;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.Locale;

/**
 * Implementação simples de TOTP (RFC 6238) compatível com Google Authenticator.
 * - usa HmacSHA1
 * - 30s time step
 * - 6 dígitos
 */
@Service
public class TotpService {

  private static final int TIME_STEP_SECONDS = 30;
  private static final int DIGITS = 6;
  private static final SecureRandom RNG = new SecureRandom();
  private static final char[] BASE32_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".toCharArray();
  private static final int[] BASE32_LOOKUP = new int[256];

  static {
    Arrays.fill(BASE32_LOOKUP, -1);
    for (int i = 0; i < BASE32_ALPHABET.length; i++) {
      BASE32_LOOKUP[BASE32_ALPHABET[i]] = i;
    }
  }

  /** Gera segredo em Base32 compatível com Google Authenticator. */
  public String generateSecret() {
    byte[] buf = new byte[20];
    RNG.nextBytes(buf);
    return encodeBase32(buf);
  }

  public boolean verifyCode(String secret, String code) {
    if (code == null || code.length() != DIGITS) return false;
    long t = Instant.now().getEpochSecond() / TIME_STEP_SECONDS;
    // tolerância de -1, 0, +1 janelas
    for (long i = -1; i <= 1; i++) {
      String expected = totp(secret, t + i);
      if (expected.equals(code)) return true;
    }
    return false;
  }

  private String totp(String secret, long counter) {
    try {
      byte[] key = decodeSecret(secret);
      byte[] data = ByteBuffer.allocate(8).putLong(counter).array();

      Mac mac = Mac.getInstance("HmacSHA1");
      mac.init(new SecretKeySpec(key, "HmacSHA1"));
      byte[] hash = mac.doFinal(data);

      int offset = hash[hash.length - 1] & 0x0F;
      int binary = ((hash[offset] & 0x7f) << 24)
          | ((hash[offset + 1] & 0xff) << 16)
          | ((hash[offset + 2] & 0xff) << 8)
          | (hash[offset + 3] & 0xff);

      int otp = binary % (int) Math.pow(10, DIGITS);
      return String.format("%0" + DIGITS + "d", otp);
    } catch (Exception e) {
      throw new BadRequestException("Invalid 2FA secret");
    }
  }

  private byte[] decodeSecret(String secret) {
    if (secret == null || secret.isBlank()) {
      throw new BadRequestException("Invalid 2FA secret");
    }

    String normalized = secret.replace(" ", "").replace("=", "").toUpperCase(Locale.ROOT);
    try {
      return decodeBase32(normalized);
    } catch (Exception ignored) {
      // Backward compatibility: previously persisted secrets used Base64 URL-safe.
      try {
        return Base64.getUrlDecoder().decode(secret);
      } catch (Exception e) {
        throw new BadRequestException("Invalid 2FA secret");
      }
    }
  }

  private String encodeBase32(byte[] data) {
    StringBuilder out = new StringBuilder((data.length * 8 + 4) / 5);
    int buffer = 0;
    int bitsLeft = 0;

    for (byte b : data) {
      buffer = (buffer << 8) | (b & 0xFF);
      bitsLeft += 8;
      while (bitsLeft >= 5) {
        out.append(BASE32_ALPHABET[(buffer >> (bitsLeft - 5)) & 0x1F]);
        bitsLeft -= 5;
      }
    }

    if (bitsLeft > 0) {
      out.append(BASE32_ALPHABET[(buffer << (5 - bitsLeft)) & 0x1F]);
    }

    return out.toString();
  }

  private byte[] decodeBase32(String value) {
    if (value.isBlank()) {
      throw new IllegalArgumentException("Empty Base32");
    }

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    int buffer = 0;
    int bitsLeft = 0;

    for (char c : value.toCharArray()) {
      if (c >= BASE32_LOOKUP.length || BASE32_LOOKUP[c] < 0) {
        throw new IllegalArgumentException("Invalid Base32 character");
      }
      buffer = (buffer << 5) | BASE32_LOOKUP[c];
      bitsLeft += 5;

      if (bitsLeft >= 8) {
        out.write((buffer >> (bitsLeft - 8)) & 0xFF);
        bitsLeft -= 8;
      }
    }

    return out.toByteArray();
  }
}
