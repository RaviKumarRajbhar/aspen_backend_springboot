package com.example.aspen.Dto.Mapper;

import com.example.aspen.Dto.NotificationResponse;
import com.example.aspen.Entities.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification notification){

        NotificationResponse response = new NotificationResponse();

        response.setSenderId(notification.getReceiverId());
        response.setCreatedAt(notification.getCreatedAt());
        response.setReferenceId(notification.getReferenceId());
        response.setType(notification.getType());

        return response;
    }
}
