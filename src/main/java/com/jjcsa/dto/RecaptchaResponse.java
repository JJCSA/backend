package com.jjcsa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RecaptchaResponse {
    @JsonProperty("success")
    private boolean success;

    @JsonProperty("challenge_ts")
    private String challengeTimestamp;

    @JsonProperty("hostname")
    private String hostname;

    public boolean isSuccess() {
        return success;
    }

    public String getChallengeTimestamp() {
        return challengeTimestamp;
    }

    public String getHostname() {
        return hostname;
    }
}