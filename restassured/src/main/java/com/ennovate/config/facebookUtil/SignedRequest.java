package com.ennovate.config.facebookUtil;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SignedRequest {

    private long issuedAt;
    private String user_id;

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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}