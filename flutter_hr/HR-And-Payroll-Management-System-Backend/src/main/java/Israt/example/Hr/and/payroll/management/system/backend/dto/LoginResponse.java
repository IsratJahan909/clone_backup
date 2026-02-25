package Israt.example.Hr.and.payroll.management.system.backend.dto;

import Israt.example.Hr.and.payroll.management.system.backend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private boolean success;
    private String message;
  private String token;
    private User user;
}
