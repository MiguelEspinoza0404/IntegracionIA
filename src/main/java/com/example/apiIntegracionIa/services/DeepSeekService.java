package com.example.apiIntegracionIa.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;


@Service
public class DeepSeekService {
    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String API_KEY = "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"; 

    public String getSummary(String text) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "deepseek/deepseek-r1:free");
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", """
                                        Eres un asistente especializado en reclutamiento.Tu tarea es analizar estos CV recibidos en formato TEXTO, Debes:
                                        1. Analizar el CV para que cumpla con los requisitos solicitados
                                        2. Generar una respuesta Clara y concisa en espanol
                                        Si no encuentras un candidato Apto simplemente indica que no es apto mostrando su nombre completo. Limita tu respuesta a una oracion maximo.
                                        Pregunta: 'Necesito personas para cubrir el puesto de desarrollador FRON-END, experiencia minima de 3 meses'
                                    """ + text);
        messages.add(userMessage);
        
        requestBody.put("messages", messages);
        requestBody.put("max_tokens", 1500);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(API_KEY); 

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(API_URL, HttpMethod.POST, requestEntity, Map.class);

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
        if (choices != null && !choices.isEmpty()) {
            Map<String, Object> firstChoice = choices.get(0);

            Map<String, String> message = (Map<String, String>) firstChoice.get("message");

            String content = message != null ? message.get("content") : null;
            if (content == null) {
                content = message != null ? message.get("reasoning") : null; 
            }

            return content;
        }

        return "No se pudo obtener un resumen.";
    }
}
