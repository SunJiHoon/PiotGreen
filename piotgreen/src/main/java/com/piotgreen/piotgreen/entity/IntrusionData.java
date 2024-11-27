package com.piotgreen.piotgreen.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;


@Entity
@Table(name = "intrusion_data")
@Data
public class IntrusionData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "danger_level")
    private int dangerLevel;//0~100사이의 값을 갖는다.

    @Column(name = "timestamp")
    private LocalDateTime timestamp;
}
