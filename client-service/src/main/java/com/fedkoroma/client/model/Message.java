package com.fedkoroma.client.model;

import com.fedkoroma.client.model.contract.AbstractEntity;
import com.fedkoroma.client.model.enums.DeliveryMethod;
import com.fedkoroma.client.model.enums.DeliveryStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "messages")
public class Message extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "template_id", nullable = false)
    private MessageTemplate template;

    @ManyToMany
    @JoinTable(
            name = "message_recipients",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "contact_id")
    )
    private List<Contact> recipients;

    @ElementCollection(targetClass = DeliveryMethod.class)
    @CollectionTable(name = "message_delivery_methods",
            joinColumns = @JoinColumn(name = "message_id"))
    @Column(name = "delivery_method")
    @Enumerated(EnumType.STRING)
    private Set<DeliveryMethod> deliveryMethods;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;
}
