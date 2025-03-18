package com.example.apiIntegracionIa.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.example.apiIntegracionIa.services.DeepSeekService;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;



@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    @Autowired
    private DeepSeekService deepSeekService;
    
    @CrossOrigin(origins = "*")
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFiles(@RequestParam("files") List<MultipartFile> files) throws IOException {
        List<String> summaries = new ArrayList<>();
        for (MultipartFile file : files) {
            String extractedText = extractTextFromPdf(file.getInputStream());
            summaries.add(deepSeekService.getSummary(extractedText));
        }
        
        return new ResponseEntity<>(summaries,HttpStatus.OK);
    }

    private String extractTextFromPdf(InputStream pdfStream) throws IOException {
        try (PDDocument document = PDDocument.load(pdfStream)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String extractedText = pdfStripper.getText(document);

            if (extractedText == null || extractedText.trim().isEmpty()) {
                System.out.println("No se pudo extraer texto del PDF.");
            } else {
                System.out.println("Texto extraído correctamente.");
            }

            return extractedText;
        }
    }

}
