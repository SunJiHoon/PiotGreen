package com.piotgreen.piotgreen.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;


@Entity
@Table(name = "irrigation_data")
@Data
public class IrrigationData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "moisture_level")
    private int moistureLevel;//0~100사이의 값을 갖는다.

    @Column(name = "timestamp")
    private LocalDateTime timestamp;
}
