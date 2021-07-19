package demo.jlee.demo2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lijia
 * @since 2021/7/15
 */
@RestController("test2")
public class Index2Controller {

    @GetMapping("index")
    public String index() {
        return ("ahahah");
    }
}
