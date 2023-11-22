package com.api.templateconverter.controller;

import com.api.templateconverter.dto.DadosArquivo;
import com.api.templateconverter.service.PdfConverterService;
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
    public ResponseEntity<String> handlePostRequest(@RequestBody DadosArquivo adosArquivo) throws IOException {


       String resultado =  pdfConverterService.convertPdf(adosArquivo);

        return new ResponseEntity<>(resultado, HttpStatus.OK);
    }

}
