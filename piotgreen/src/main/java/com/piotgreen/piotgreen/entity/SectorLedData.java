package com.piotgreen.piotgreen.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "light_data")
@Data
public class SectorLedData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "led_1")
    private String led1;

    @Column(name = "led_2")
    private String led2;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;
}
