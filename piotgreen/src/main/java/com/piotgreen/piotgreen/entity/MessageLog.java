package com.piotgreen.piotgreen.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "message_log ")
@Data
public class MessageLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime timestamp;

    private String message; // 보낸 메시지 내용
}
