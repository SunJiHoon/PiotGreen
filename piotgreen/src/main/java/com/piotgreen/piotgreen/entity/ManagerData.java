package com.piotgreen.piotgreen.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "manager_data")
@Data
public class ManagerData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;//0~100사이의 값을 갖는다.

    @Column(name = "phone_number")
    private String phoneNumber;
}
