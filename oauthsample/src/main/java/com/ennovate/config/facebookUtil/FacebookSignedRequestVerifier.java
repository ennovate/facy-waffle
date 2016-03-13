package com.ennovate.config.facebookUtil;


import com.ennovate.util.TimeSource;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.crypto.codec.Hex;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.regex.Pattern;

public class FacebookSignedRequestVerifier {

    private final static Log log = LogFactory.getLog(FacebookSignedRequestVerifier.class);

    private String faceBookAppSecret;
    private TimeSource timeSource;


    private Base64 base64 = new Base64();

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
            request = objectMapper.readValue(Base64.decodeBase64(encodedExpirationData), SignedRequest.class);
        } catch (IOException e) {
            System.out.println("could not read signedRequest data part; " + e.getMessage());
            return true;
        }

        if (request == null || request.getIssuedAt() == 0) {
            log.info("signedRequest is missing issued_at attribute");
            return true;
        }

        Instant oneHourBefore = timeSource.nowWithUTC().toInstant().minus(1, ChronoUnit.HOURS);

        if (!Instant.ofEpochSecond(request.getIssuedAt()).isAfter(oneHourBefore)) {
            log.info("token is older than one hour; "+ request);
            return true;
        }
        return false;
    }

    private boolean isValidSignature(String source, String encodedSignature, String secretKey) {
        byte[] signatureBytes;
        try {
            signatureBytes = base64.decode(encodedSignature.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            return false;
        }
        byte[] hMac;
        try {
            hMac = CryptoUtils.createHmac(source, secretKey);
        } catch (Exception e) {
            log.warn("could not compute message digest; " + e.getMessage());
            return false;
        }

        if (!Arrays.equals(hMac, signatureBytes)) {
            log.info("signature did not match; expected:"+new String(Hex.encode(hMac))+
                    " got: "+ new String(Hex.encode(signatureBytes)));
            return false;
        }
        return true;
    }
}
