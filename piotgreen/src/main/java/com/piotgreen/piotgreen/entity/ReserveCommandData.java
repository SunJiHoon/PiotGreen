package com.piotgreen.piotgreen.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "reserve_command_data")
@Data
public class ReserveCommandData {
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

    @Column(name = "status")
    private String status; // 수행 여부를 "PENDING", "COMPLETED", "FAILED"와 같은 상태 값으로 저장
}
