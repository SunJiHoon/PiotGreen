package com.piotgreen.piotgreen.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "light_data")
@Data
public class LightData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "light_level_1")
    private int lightLevel1;//0~100사이의 값을 갖는다.

    @Column(name = "light_level_2")
    private int lightLevel2;


    @Column(name = "timestamp")
    private LocalDateTime timestamp;
}
