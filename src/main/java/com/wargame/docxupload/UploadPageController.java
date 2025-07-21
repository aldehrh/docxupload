package com.wargame.docxupload;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UploadPageController {

    @GetMapping("/")
    public String showUploadPage() {
        return "upload";
    }
}
