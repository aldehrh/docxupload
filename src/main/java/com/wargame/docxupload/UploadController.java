package com.wargame.docxupload;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/upload")
public class UploadController {

	private final DocxParserService docxParserService;

	public UploadController(DocxParserService docxParserService) {
		this.docxParserService = docxParserService;
	}

	@PostMapping
	public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
		try {
			if (!file.getOriginalFilename().endsWith(".docx")) {
				model.addAttribute("message", "Only .docx files are allowed.");
				return "error";
			}

			String result = docxParserService.parseDocx(file);

			if (result != null && result.length() > 0) {
				model.addAttribute("message", result);
			} else {
				model.addAttribute("message", "업로드 성공");
			}

			return "result";

		} catch (Exception e) {
			model.addAttribute("message", "Parsing error: " + e.getMessage());
			return "error";
		}
	}

}

