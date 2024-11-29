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
    private String dangerLevel;//0-safe, 1-danger 값을 갖는다. //1이면 위험, 0이면 안전인 것 같다.

    @Column(name = "timestamp")
    private LocalDateTime timestamp;
}
