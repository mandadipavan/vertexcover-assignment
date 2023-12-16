package com.vertexcover.assignment.controllers;


import com.vertexcover.assignment.bos.BaseResponse;
import com.vertexcover.assignment.bos.request.CouponApplicationRequest;
import com.vertexcover.assignment.bos.request.CouponRepeatCountRequest;
import com.vertexcover.assignment.bos.response.ApplicationResult;
import com.vertexcover.assignment.bos.response.VerificationResult;
import com.vertexcover.assignment.exceptions.CouponCodeNotFoundException;
import com.vertexcover.assignment.exceptions.InvalidCouponException;
import com.vertexcover.assignment.services.CouponService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("api/coupon")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping("add-repeat-counts")
    public ResponseEntity<BaseResponse<String>> addRepeatCounts(@Valid @RequestBody CouponRepeatCountRequest request) {
        couponService.addRepeatCounts(request.getCouponCode(), request.getRepeatCountConfig());
        return ResponseEntity.ok(BaseResponse.success(null, "Success"));
    }

    @GetMapping("verify/{couponCode}")
    public ResponseEntity<BaseResponse<VerificationResult>> verifyCouponValidity(@PathVariable @NotBlank(message = "Coupon code can not be blank") String couponCode) {
        try {
            VerificationResult response = couponService.verifyCouponValidity(couponCode);
            return ResponseEntity.ok(BaseResponse.success(response, "Success"));
        } catch (CouponCodeNotFoundException | InvalidCouponException e) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.failure(null, e.getMessage()));
        }
    }

    @PostMapping("apply")
    public ResponseEntity<BaseResponse<ApplicationResult>> applyCoupon(@Valid @RequestBody CouponApplicationRequest request) {
        try {
            ApplicationResult response = couponService.applyCoupon(request.getCouponCode());
            return ResponseEntity.ok(BaseResponse.success(response, "Success"));
        } catch (InvalidCouponException | CouponCodeNotFoundException e) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.failure(null, e.getMessage()));
        }
    }
}
