package com.api.templateconverter.controller;

import com.api.templateconverter.dto.DadosArquivo;
import com.api.templateconverter.service.PdfConverterService;
import com.api.templateconverter.util.CompactacaoUtil;
import com.api.templateconverter.util.UnzipUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.io.*;
import java.nio.file.Files;
import java.util.Base64;


@RestController
@RequestMapping("/templates")
public class TemplateController {

    @Autowired
    PdfConverterService pdfConverterService;


    @PostMapping("/{templateName}/{type}")
    public ResponseEntity<?> handlePostRequest(
            @PathVariable String templateName,
            @PathVariable String type,
            @RequestBody DadosArquivo dadosArquivo
    ) throws IOException {

        if ("base64".equals(type)) {

            String resultado = pdfConverterService.convertPdf(dadosArquivo, templateName);

            // Return Base64 string in the response body
            return ResponseEntity.ok().body("\nBase64: " + resultado);
        } else if ("pdf".equals(type)) {

            String base64ZipedResult = pdfConverterService.convertPdf(dadosArquivo, templateName);

            //String unzipedBase64 = CompactacaoUtil.reverterBase64Descompactar(base64ZipedResult);
            //byte[] encodedBytes = Base64.getEncoder().encode(unzipedBase64.getBytes());
            //String result = new String(encodedBytes);

            //decodeAndUnzip(base64ZipedResult);


            // Load PDF file from the classpath
            Resource resource = new FileSystemResource(templateName + ".pdf");

            //Resource resource = new ClassPathResource("example.pdf");

            // Read PDF content into a byte array
            byte[] pdfBytes = Files.readAllBytes(resource.getFile().toPath());

            // Return PDF as an attachment
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "example.pdf");

            //return ResponseEntity.ok().body(base64ZipedResult);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } else {
            // Handle other types or throw an exception if necessary
            return new ResponseEntity<>("Unsupported type", HttpStatus.BAD_REQUEST);
        }
    }



    private void decodeAndUnzip(String base64Pdf) throws IOException {
        byte[] decodedBytes = Base64.getDecoder().decode(base64Pdf.getBytes());

        // Step 2: Save the decoded content to a zip file
        File decodedFile = new File("decoded_file.zip");
        try (FileOutputStream fos = new FileOutputStream(decodedFile)) {
            fos.write(decodedBytes);
        }

       unzipAndUse(decodedFile.getAbsolutePath());
        //unzipAndUse("src/main/resources/top-image.zip");

    }

    //    @PostMapping
//    public ResponseEntity<String> handlePostRequest(@RequestBody DadosArquivo adosArquivo) throws IOException {
//
//       String resultado =  pdfConverterService.convertPdf(dadosArquivo);
//
//        return new ResponseEntity<>(resultado, HttpStatus.OK);
//    }

    private void unzipAndUse(String zipFilePath) {
        //String zipFilePath = "decode_file.zip";
        String destDirectory = "extracted_content";
        try {

            UnzipUtility utility = new UnzipUtility();

            utility.unzip(zipFilePath, destDirectory);
            System.out.println("File successfully unzipped.");
        } catch (IOException e) {
            System.out.println("Error unzipping the file: " + e.getMessage());
        }
    }
    }

