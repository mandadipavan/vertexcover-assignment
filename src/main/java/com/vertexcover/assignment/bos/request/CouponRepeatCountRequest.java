package com.vertexcover.assignment.bos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class CouponRepeatCountRequest {
    @NotBlank(message = "Coupon code can not be blank")
    private String couponCode;
    @Valid
    private RepeatCountConfig repeatCountConfig;
}