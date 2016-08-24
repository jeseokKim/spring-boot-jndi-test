package com.mybatis.sample.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mybatis.sample.repository.SampleMapper;

@Controller
public class SampleController {

    @Autowired
    private SampleMapper sampleMapper;

    @RequestMapping("/")
    public String init(Model model) {

        model.addAttribute("sample", sampleMapper.getData());

        return "home";
    }
}
