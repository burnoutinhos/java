package com.burnoutinhos.burnoutinhos_api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = "userId")
@EqualsAndHashCode(exclude = "userId")
@Entity
@Table(name = "t_gp_mottu_token_push")
public class PushToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_token_push")
    private Long id;

    private String token;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private AppUser userId;
}
