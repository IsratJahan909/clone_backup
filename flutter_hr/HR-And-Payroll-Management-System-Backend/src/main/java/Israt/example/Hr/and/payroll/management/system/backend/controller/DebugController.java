package Israt.example.Hr.and.payroll.management.system.backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/debug")
@CrossOrigin(origins = "*")
public class DebugController {

    @GetMapping("/whoami")
    public ResponseEntity<?> whoami() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> res = new HashMap<>();
        if (auth == null) {
            res.put("authenticated", false);
            res.put("principal", null);
            res.put("authorities", null);
            return ResponseEntity.ok(res);
        }
        res.put("authenticated", auth.isAuthenticated());
        res.put("principal", auth.getName());
        res.put("authorities", auth.getAuthorities());
        return ResponseEntity.ok(res);
    }
}
