package com.example.ar_reshare;

import com.google.gson.annotations.SerializedName;

public class PostcodeResponse {
    @SerializedName("success")
    private String success;
    @SerializedName("result")
    private PostcodeDetails result;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public PostcodeDetails getResult() {
        return result;
    }

    public void setResult(PostcodeDetails result) {
        this.result = result;
    }
}
