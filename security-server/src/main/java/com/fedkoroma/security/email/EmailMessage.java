package com.fedkoroma.security.email;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EmailMessage {
    private String to;
    private String name;
    private String link;
}
