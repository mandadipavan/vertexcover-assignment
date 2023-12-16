package com.vertexcover.assignment.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class Coupon {
    @Id
    private String couponCode;
    private LocalDate lastUsageDate;
    private LocalDate weekStartDate;
    private int globalTotalRepeatCount;
    private int globalTotalRemaining;
    private int userTotalRepeatCount;
    private int userTotalRemaining;
    private int userDailyRepeatCount;
    private int userDailyRemaining;
    private int userWeeklyRepeatCount;
    private int userWeeklyRemaining;
}