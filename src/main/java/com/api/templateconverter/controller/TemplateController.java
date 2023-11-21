package com.api.templateconverter.controller;

import com.api.templateconverter.service.PdfConverterService;
import com.lowagie.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
@RequestMapping("/template")
public class TemplateController {

    @Autowired
    PdfConverterService pdfConverterService;

    @PostMapping
    public ResponseEntity<String> handlePostRequest(@RequestBody Object obj) throws DocumentException, IOException {


        pdfConverterService.convertPdf();


        return new ResponseEntity<>("Data received successfully", HttpStatus.OK);
    }




}
