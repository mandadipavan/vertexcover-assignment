package com.vertexcover.assignment.bos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class CouponApplicationRequest {
    @NotBlank(message = "Coupon code can not be blank")
    private String couponCode;
}