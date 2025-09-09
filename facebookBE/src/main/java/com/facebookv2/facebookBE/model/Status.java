package com.facebookv2.facebookBE.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    private String picture;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdTime;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
