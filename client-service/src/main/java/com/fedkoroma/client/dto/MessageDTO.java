package com.fedkoroma.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDTO {
    private Long id;
    private Long templateId;
    private List<Long> recipientIds;
    private Set<String> deliveryMethods;
    private String status;
    private LocalDateTime sentAt;
}
