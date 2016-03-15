package com.ennovate.config.facebookUtil;


import com.ennovate.util.TimeSource;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.util.Base64Utils;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.regex.Pattern;


public class FacebookSignedRequestVerifier {

    private final static Log log = LogFactory.getLog(FacebookSignedRequestVerifier.class);

    private String faceBookAppSecret;
    private TimeSource timeSource;
    private String userId;

    private ObjectMapper objectMapper = new ObjectMapper();


    public FacebookSignedRequestVerifier(String faceBookAppSecret, TimeSource timeSource) {
        this.faceBookAppSecret = faceBookAppSecret;
        this.timeSource = timeSource;
    }

    public boolean verify(String fbSignedRequest) {
        final String[] signedRequestSegments = fbSignedRequest.split(Pattern.quote("."), 2);
        return isValidSignature(signedRequestSegments[1], signedRequestSegments[0], faceBookAppSecret)
                && !isExpiredSignature(signedRequestSegments[1]);
    }
    private boolean isExpiredSignature(String encodedExpirationData) {

        SignedRequest request;
        try {
            request = objectMapper.readValue(Base64Utils.decodeFromString(encodedExpirationData), SignedRequest.class);
            userId = request.getUser_id();
        } catch (IOException e) {
            log.error("could not read signedRequest data part; ", e);
            return true;
        }

        if (request.getIssuedAt() == 0) {
            log.info("signedRequest is missing issued_at attribute");
            return true;
        }

        Instant oneHourBefore = timeSource.nowWithUTC().toInstant().minus(1, ChronoUnit.HOURS);

        if (!Instant.ofEpochSecond(request.getIssuedAt()).isAfter(oneHourBefore)) {
            log.info("token is older than one hour; " + request);
            return true;
        }
        return false;
    }

    private boolean isValidSignature(String source, String encodedSignature, String secretKey) {
        byte[] signatureBytes = Base64Utils.decodeFromUrlSafeString(encodedSignature);

        byte[] hMac;
        try {
            hMac = CryptoUtils.createHmac(source, secretKey);
        } catch (Exception e) {
            log.warn("could not compute message digest; " + e.getMessage());
            return false;
        }

        if (!Arrays.equals(hMac, signatureBytes)) {
            log.info("signature did not match; expected:" + new String(Hex.encode(hMac)) +
                    " got: " + new String(Hex.encode(signatureBytes)));
            return false;
        }
        return true;
    }

    public String getUserId() {
        return userId;
    }
}
