package com.vertexcover.assignment.services;

import com.vertexcover.assignment.bos.request.RepeatCountConfig;
import com.vertexcover.assignment.bos.response.*;
import com.vertexcover.assignment.entities.Coupon;
import com.vertexcover.assignment.exceptions.CouponCodeNotFoundException;
import com.vertexcover.assignment.exceptions.InvalidCouponException;
import com.vertexcover.assignment.repositories.CouponRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Optional;

@Service
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    public CouponServiceImpl(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Override
    public void addRepeatCounts(String couponCode, RepeatCountConfig repeatCountConfig) {
        Optional<Coupon> existingCouponOpt = couponRepository.findById(couponCode);
        if (existingCouponOpt.isEmpty()) {
            // If the coupon doesn't exist, create a new one
            Coupon newCoupon = new Coupon();
            newCoupon.setCouponCode(couponCode);
            updateRepeatCounts(newCoupon, repeatCountConfig);
            couponRepository.save(newCoupon);
        } else {
            // If the coupon already exists, update the repeat counts
            updateRepeatCounts(existingCouponOpt.get(), repeatCountConfig);
            couponRepository.save(existingCouponOpt.get());
        }
    }

    @Override
    public VerificationResult verifyCouponValidity(String couponCode) throws CouponCodeNotFoundException, InvalidCouponException {
        Coupon coupon = getCouponInfo(couponCode);
        //reset the limits
        updateCouponDailyAndWeekly(coupon);
        // Check if the user has exceeded the total repeat count
        validateCoupon(coupon);
        // If all checks pass, the coupon is valid
        return VerificationResult.builder()
                .valid(true)
                .message("Coupon code is valid.")
                .remainingUses(coupon.getUserTotalRemaining())
                .timeLimits(TimeLimits.builder()
                        .daily(TimeLimitsInfo.builder()
                                .remaining(coupon.getUserDailyRemaining())
                                .limit(coupon.getUserDailyRepeatCount())
                                .build())
                        .weekly(TimeLimitsInfo.builder()
                                .remaining(coupon.getUserWeeklyRemaining())
                                .limit(coupon.getUserWeeklyRepeatCount())
                                .build())
                        .build())
                .build();
    }

    @Override
    public ApplicationResult applyCoupon(String couponCode) throws InvalidCouponException, CouponCodeNotFoundException {
        Coupon coupon = getCouponInfo(couponCode);
        //reset the limits
        updateCouponDailyAndWeekly(coupon);
        //validate the coupon
        validateCoupon(coupon);
        updateRepeatCountsAfterApplication(coupon);
        couponRepository.save(coupon);
        return ApplicationResult.builder()
                .message("Coupon applied successfully.")
                .status("success")
                .updatedRepeatCounts(UpdatedRepeatCounts.builder()
                        .globalTotalRemaining(coupon.getGlobalTotalRemaining())
                        .userTotalRemaining(coupon.getUserTotalRemaining())
                        .userDailyRemaining(coupon.getUserDailyRemaining())
                        .userWeeklyRemaining(coupon.getUserWeeklyRemaining())
                        .build())
                .build();
    }

    // Helper method to update repeat counts in the Coupon entity
    private void updateRepeatCounts(Coupon coupon, RepeatCountConfig repeatCountConfig) {
        coupon.setWeekStartDate(LocalDate.now());
        coupon.setLastUsageDate(LocalDate.now());
        coupon.setGlobalTotalRepeatCount(repeatCountConfig.getGlobalTotalRepeatCount());
        coupon.setGlobalTotalRemaining(repeatCountConfig.getGlobalTotalRepeatCount());
        coupon.setUserTotalRepeatCount(repeatCountConfig.getUserTotalRepeatCount());
        coupon.setUserTotalRemaining(repeatCountConfig.getUserTotalRepeatCount());
        coupon.setUserDailyRepeatCount(repeatCountConfig.getUserDailyRepeatCount());
        coupon.setUserDailyRemaining(repeatCountConfig.getUserDailyRepeatCount());
        coupon.setUserWeeklyRepeatCount(repeatCountConfig.getUserWeeklyRepeatCount());
        coupon.setUserWeeklyRemaining(repeatCountConfig.getUserWeeklyRepeatCount());
    }

    private void updateRepeatCountsAfterApplication(Coupon coupon) {
        // Update user-specific counts
        coupon.setUserTotalRemaining(coupon.getUserTotalRemaining() - 1);
        coupon.setUserDailyRemaining(coupon.getUserDailyRemaining() - 1);
        coupon.setUserWeeklyRemaining(coupon.getUserWeeklyRemaining() - 1);
        // Update global counts
        coupon.setGlobalTotalRemaining(coupon.getGlobalTotalRemaining() - 1);
        coupon.setLastUsageDate(LocalDate.now());
    }

    private void validateCoupon(Coupon coupon) throws InvalidCouponException {
        updateCouponDailyAndWeekly(coupon);
        if (coupon.getUserTotalRepeatCount() > 0 && coupon.getUserTotalRemaining() <= 0) {
            throw new InvalidCouponException("User has exceeded the total repeat count.");
        }

        // Check if the user has exceeded the weekly repeat count
        if (coupon.getUserWeeklyRepeatCount() > 0 && coupon.getUserWeeklyRemaining() <= 0) {
            throw new InvalidCouponException("User has exceeded the weekly repeat count.");
        }
        // Check if the user has exceeded the daily repeat count
        if (coupon.getUserDailyRepeatCount() > 0 && coupon.getUserDailyRemaining() <= 0) {
            throw new InvalidCouponException("User has exceeded the daily repeat count.");
        }


        // Check global limits
        if (coupon.getGlobalTotalRepeatCount() > 0 && coupon.getGlobalTotalRemaining() <= 0) {
            throw new InvalidCouponException("Coupon has exceeded the global total repeat count.");
        }
    }

    private Coupon getCouponInfo(String couponCode) throws CouponCodeNotFoundException {
        Optional<Coupon> couponOptional = couponRepository.findById(couponCode);
        if (couponOptional.isEmpty()) {
            throw new CouponCodeNotFoundException("Coupon code not found.");
        }
        return couponOptional.get();
    }

    private void updateCouponDailyAndWeekly(Coupon coupon) {
        LocalDate currentDate = LocalDate.now();
        if (currentDate.isAfter(coupon.getLastUsageDate())) {
            coupon.setLastUsageDate(currentDate);
            coupon.setUserDailyRemaining(coupon.getUserDailyRepeatCount());
        }
        if (isNewWeekStarted(coupon.getWeekStartDate(), currentDate)) {
            coupon.setUserWeeklyRemaining(coupon.getUserWeeklyRepeatCount());
            coupon.setWeekStartDate(currentDate);
        }
        couponRepository.save(coupon);
    }

    private boolean isNewWeekStarted(LocalDate startingWeekDate, LocalDate currentDate) {
        int startingWeekOfYear = startingWeekDate.get(WeekFields.ISO.weekOfWeekBasedYear());
        int currentWeekOfYear = currentDate.get(WeekFields.ISO.weekOfWeekBasedYear());
        return startingWeekOfYear != currentWeekOfYear;
    }
}
