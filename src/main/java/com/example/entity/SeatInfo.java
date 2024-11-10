package com.example.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Table(name = "seat_info")
public class SeatInfo  implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "position")
    private String position;

    @Column(name = "goodsId")
    private Long goodsId;


}
