package com.example.greenlens.repository;

import com.example.greenlens.model.Coupon;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserCouponRepository {
    private final String userId;
    private List<Coupon> userCoupons;

    public UserCouponRepository(String userId) {
        this.userId = userId;
        this.userCoupons = new ArrayList<>();
        loadUserCoupons();
    }

    // 사용자의 미사용 쿠폰 조회
    public List<Coupon> getUnusedCoupons() {
        return userCoupons.stream()
                .filter(coupon -> !coupon.isUsed())
                .collect(Collectors.toList());
    }

    // 사용자의 사용완료 쿠폰 조회
    public List<Coupon> getUsedCoupons() {
        return userCoupons.stream()
                .filter(Coupon::isUsed)
                .collect(Collectors.toList());
    }

    // 쿠폰 사용 처리
    public void useCoupon(String couponId) {
        userCoupons.stream()
                .filter(coupon -> coupon.getCouponId().equals(couponId))
                .findFirst()
                .ifPresent(Coupon::useCoupon);
        saveUserCoupons(); // 변경사항 저장
    }

    // 새 쿠폰 구매/획득
    public void addCoupon(Coupon coupon) {
        userCoupons.add(coupon);
        saveUserCoupons();
    }

    // 데이터 로드/저장 메소드 (실제로는 DB나 서버와 연동)
    private void loadUserCoupons() {
        // TODO: DB나 서버에서 사용자의 쿠폰 목록 로드
    }

    private void saveUserCoupons() {
        // TODO: DB나 서버에 사용자의 쿠폰 목록 저장
    }
}