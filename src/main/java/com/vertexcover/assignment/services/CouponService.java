package com.vertexcover.assignment.services;

import com.vertexcover.assignment.bos.request.RepeatCountConfig;
import com.vertexcover.assignment.bos.response.ApplicationResult;
import com.vertexcover.assignment.bos.response.VerificationResult;
import com.vertexcover.assignment.exceptions.CouponCodeNotFoundException;
import com.vertexcover.assignment.exceptions.InvalidCouponException;

public interface CouponService {
    void addRepeatCounts(String couponCode, RepeatCountConfig repeatCountConfig);

    VerificationResult verifyCouponValidity(String couponCode) throws CouponCodeNotFoundException, InvalidCouponException;

    ApplicationResult applyCoupon(String couponCode) throws InvalidCouponException, CouponCodeNotFoundException;
}

