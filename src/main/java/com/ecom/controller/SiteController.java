package com.ecom.controller;



import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SiteController {

  // เสิร์ฟไฟล์จาก /static/login.html
	// @GetMapping("/login")
	//public String login() {
	 // return "forward:/login.html";
	 // }

  // หน้า root ให้ไป swagger (กันลูปถ้าหน้าแรกต้อง auth)
  @GetMapping("/swagger")
  public String home() {
    return "redirect:/swagger-ui/index.html";
  }
}

