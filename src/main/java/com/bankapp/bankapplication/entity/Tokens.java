package com.bankapp.bankapplication.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="tokens")
public class Tokens {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String token;
    private Date createDate;
    private Date expiryDate;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
