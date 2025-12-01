package io.github.kacper.weglarz.realtimecollaboration.controller;

import io.github.kacper.weglarz.realtimecollaboration.dto.request.DocumentRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class DocumentWebSocketController {

    @Autowired
    public SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Sends document edits to all WebSocket subscribers
     * @param id of document
     * @param documentRequestDTO DTO -> title, content
     */
    @MessageMapping("/document/{id}/edit")
    public void handleEdit(@DestinationVariable Long id, @Payload DocumentRequestDTO documentRequestDTO) {
        simpMessagingTemplate.convertAndSend("/topic/document/" + id, documentRequestDTO);
    }
}
