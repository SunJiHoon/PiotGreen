package com.piotgreen.piotgreen.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "led_data")
@Data
public class LedData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "led_1")
    private String led1; //on 혹은 off 값을 갖는다.

    @Column(name = "led_2")
    private String led2;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;
}
