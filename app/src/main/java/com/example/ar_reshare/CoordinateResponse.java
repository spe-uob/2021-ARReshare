package com.example.ar_reshare;

import com.example.ar_reshare.PostcodeDetails;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CoordinateResponse {
        @SerializedName("success")
        private String success;
        @SerializedName("result")
        private List<PostcodeDetails> result;

        public String getSuccess() {
            return success;
        }

        public void setSuccess(String success) {
            this.success = success;
        }

        public List<PostcodeDetails> getResult() {
            return result;
        }

        public void setResult(List<PostcodeDetails> result) {
            this.result = result;
        }
}
