package com.piotgreen.piotgreen.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;


@Entity
@Table(name = "command_data")
@Data
public class CommandData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "category")
    private String category;

    @Column(name = "command")
    private String command;

    @Column(name = "value")
    private String value;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;
}
