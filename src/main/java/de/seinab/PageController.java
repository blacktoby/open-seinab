package de.seinab;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {
    private static final Logger log = LoggerFactory.getLogger(PageController.class);


    @RequestMapping("/")
    public String indexPage(Model model) {
        log.debug("Hello 123!");
        return "index";
    }
}
