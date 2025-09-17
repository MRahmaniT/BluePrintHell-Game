package Client.Security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class HmacSigner {
    private HmacSigner(){}

    public static String signBase64(String keyBase64, String op, String entity, String playerId, String body, long ts, String nonce) {
        try {
            byte[] key = Base64.getDecoder().decode(keyBase64);
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            String canonical = op + "\n" + entity + "\n" + playerId + "\n" + (body == null ? "" : body) + "\n" + ts + "\n" + nonce;
            byte[] sig = mac.doFinal(canonical.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(sig);
        } catch (Exception e) {
            throw new RuntimeException("HMAC sign error", e);
        }
    }

    public static String randomNonce() {
        return java.util.UUID.randomUUID().toString();
    }
}
