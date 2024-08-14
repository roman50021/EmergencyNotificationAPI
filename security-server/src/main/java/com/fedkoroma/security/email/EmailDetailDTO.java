package com.fedkoroma.security.email;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class EmailDetailDTO {
    private String to;
    private String subject;
    private Map<String, Object> dynamicValue;
    private String templateName;
}
