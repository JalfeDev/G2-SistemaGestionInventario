package com.g2.demo.config;

import com.g2.demo.entity.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };

    private final ObjectMapper objectMapper;
    private final String secret;
    private final long expirationMs;

    public JwtUtil(
            ObjectMapper objectMapper,
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms:86400000}") long expirationMs
    ) {
        this.objectMapper = objectMapper;
        this.secret = secret;
        this.expirationMs = expirationMs;
    }

    public String generateToken(Usuario usuario) {
        long now = Instant.now().getEpochSecond();
        long expiration = now + expirationMs / 1000;

        Map<String, Object> header = new LinkedHashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", usuario.getUsername());
        payload.put("id", usuario.getId());
        payload.put("nombre", usuario.getNombre());
        payload.put("rol", usuario.getRol() != null ? usuario.getRol().getNombre() : null);
        payload.put("iat", now);
        payload.put("exp", expiration);

        String unsignedToken = encodeJson(header) + "." + encodeJson(payload);
        return unsignedToken + "." + sign(unsignedToken);
    }

    public boolean isValid(String token) {
        try {
            Map<String, Object> claims = getClaims(token);
            return getExpiration(claims) > Instant.now().getEpochSecond();
        } catch (RuntimeException ex) {
            return false;
        }
    }

    public String getUsername(String token) {
        Object subject = getClaims(token).get("sub");
        return subject != null ? subject.toString() : null;
    }

    public String getRol(String token) {
        Object rol = getClaims(token).get("rol");
        return rol != null ? rol.toString() : null;
    }

    public Map<String, Object> getClaims(String token) {
        String[] parts = splitToken(token);
        verifySignature(parts[0] + "." + parts[1], parts[2]);
        return readJson(decode(parts[1]));
    }

    private String encodeJson(Map<String, Object> data) {
        try {
            return encode(objectMapper.writeValueAsBytes(data));
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo construir el JWT", ex);
        }
    }

    private Map<String, Object> readJson(byte[] bytes) {
        try {
            return objectMapper.readValue(bytes, MAP_TYPE);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Payload JWT invalido", ex);
        }
    }

    private String[] splitToken(String token) {
        if (token == null) {
            throw new IllegalArgumentException("Token ausente");
        }
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Formato JWT invalido");
        }
        return parts;
    }

    private void verifySignature(String unsignedToken, String receivedSignature) {
        String expectedSignature = sign(unsignedToken);
        if (!MessageDigest.isEqual(expectedSignature.getBytes(StandardCharsets.UTF_8), receivedSignature.getBytes(StandardCharsets.UTF_8))) {
            throw new IllegalArgumentException("Firma JWT invalida");
        }
    }

    private String sign(String unsignedToken) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            mac.init(key);
            return encode(mac.doFinal(unsignedToken.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo firmar el JWT", ex);
        }
    }

    private long getExpiration(Map<String, Object> claims) {
        Object exp = claims.get("exp");
        if (exp instanceof Number number) {
            return number.longValue();
        }
        throw new IllegalArgumentException("JWT sin expiracion");
    }

    private String encode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private byte[] decode(String value) {
        return Base64.getUrlDecoder().decode(value);
    }
}
