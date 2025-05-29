package com.isai.demowebregistrationsystem.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/admin/dashboard")
public class AdminDashboardController {

    @GetMapping(path = "")
    public String mostrarPersonas() {
        return "admin/dashboard-admin";
    }
}
