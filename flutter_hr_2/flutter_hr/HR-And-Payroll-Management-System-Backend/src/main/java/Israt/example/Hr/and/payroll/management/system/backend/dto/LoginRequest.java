package Israt.example.Hr.and.payroll.management.system.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @Schema(description = "User email", example = "user@example.com")
    private String email;

    @Schema(description = "User password", example = "secret", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;
}


