package com.banda.chatbot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "lab_results")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String referenceNumber;

    @Column(nullable = false)
    private String patientName;

    @Column
    private String phoneNumber;

    @Column(nullable = false)
    private String testName;

    @Column(columnDefinition = "TEXT")
    private String result;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDate dateCollected;

    @Column
    private LocalDate dateCompleted;
}
