package com.example.localvault.credential.dto;

import com.example.localvault.credential.domain.CredentialType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CredentialRequest {

    @NotBlank(message = "서비스명은 필수입니다")
    @Size(max = 100)
    private String serviceName;

    @NotNull(message = "유형은 필수입니다")
    private CredentialType credentialType;

    private String url;

    @NotBlank(message = "계정은 필수입니다")
    @Size(max = 100)
    private String username;

    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;

    private Integer port;

    @Size(max = 500)
    private String description;
}