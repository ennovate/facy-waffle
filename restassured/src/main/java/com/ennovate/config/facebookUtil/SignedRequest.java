package com.ennovate.config.facebookUtil;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SignedRequest {

    private long issuedAt;

    public SignedRequest() {
    }

    public long getIssuedAt() {
        return issuedAt;
    }

    @JsonProperty(value = "issued_at")
    public void setIssuedAt(long issuedAt) {
        this.issuedAt = issuedAt;
    }

    @Override
    public String toString() {
        return "SignedRequest{" +
                "issuedAt=" + issuedAt +
                '}';
    }
}