package app.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    String home(HttpServletRequest request) {
        return "Session: " + request.getSession().getId() + "<br/>";
    }
}
