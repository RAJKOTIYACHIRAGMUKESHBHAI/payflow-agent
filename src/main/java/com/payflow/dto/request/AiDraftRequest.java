package com.payflow.dto.request;

import jakarta.validation.constraints.NotBlank;

public class AiDraftRequest {

    @NotBlank(message = "Prompt cannot be empty")
    private String prompt;

    public AiDraftRequest() {}

    public AiDraftRequest(String prompt) {
        this.prompt = prompt;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
