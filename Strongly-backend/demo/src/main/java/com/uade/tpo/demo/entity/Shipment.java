// src/main/java/com/uade/tpo/demo/entity/Shipment.java
package com.uade.tpo.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "shipments")
@Data
public class Shipment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "order_id", unique = true)
    private Order order;

    @Column(length = 60)
    private String carrier;

    @Column(length = 100)
    private String trackingNumber;

    private Instant shippedAt;
    private Instant deliveredAt;

    @Column(precision = 12, scale = 2)
    private BigDecimal shippingCost = BigDecimal.ZERO;
}
