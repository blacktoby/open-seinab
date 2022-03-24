package de.seinab.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class BackendController {
    private static final Logger log = LoggerFactory.getLogger(BackendController.class);

    @RequestMapping("/backend")
    public String backendIndex() {
        return "backend/index";
    }

    @RequestMapping("/backend/login")
    public String backendLogin() {
        return "backend/login";
    }


}
