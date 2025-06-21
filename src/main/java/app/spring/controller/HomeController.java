package app.spring.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<?> home(Principal principal) {
        Map<String, String> map = new HashMap<>();
        map.put("username", principal.getName());
        return ResponseEntity.ok(map);
    }
}
