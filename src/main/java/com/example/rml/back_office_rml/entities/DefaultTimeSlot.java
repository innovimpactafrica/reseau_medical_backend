package com.example.rml.back_office_rml.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Table(name = "default_time_slots")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DefaultTimeSlot {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long defaultTimeSlotId;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;


}
