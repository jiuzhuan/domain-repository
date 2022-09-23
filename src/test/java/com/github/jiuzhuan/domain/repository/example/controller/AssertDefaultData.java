package com.github.jiuzhuan.domain.repository.example.controller;

import com.github.jiuzhuan.domain.repository.example.domain.entity.*;

import java.util.Objects;

/**
 * @author pengfwang@trip.com
 * @date 2022/9/23 17:40
 */
public class AssertDefaultData {

    public static void assertOrder1(MasterOrderInfo info) {
        assert Objects.equals(info.userName, "老王");
    }

    public static void assertOrder2(MasterOrderInfo info) {
        assert Objects.equals(info.userName, "小李");
    }

    public static void assertSlave1(SlaveOrderInfo info) {
        assert Objects.equals(info.storeName, "麦当劳");
    }

    public static void assertSlave2(SlaveOrderInfo info) {
        assert Objects.equals(info.storeName, "肯德基");
    }

    public static void assertSlave3(SlaveOrderInfo info) {
        assert Objects.equals(info.storeName, "老乡鸡");
    }

    public static void assertGood1(OrderGoodInfo info) {
        assert Objects.equals(info.goodName, "香辣鸡腿堡");
    }

    public static void assertGood2(OrderGoodInfo info) {
        assert Objects.equals(info.goodName, "可乐");
    }

    public static void assertGood3(OrderGoodInfo info) {
        assert Objects.equals(info.goodName, "蛋挞");
    }

    public static void assertGood4(OrderGoodInfo info) {
        assert Objects.equals(info.goodName, "鸡汤");
    }

    public static void assertDiscount1(OrderGoodDiscountInfo info) {
        assert Objects.equals(info.discount.toString(), "0.80");
    }

    public static void assertDiscount2(OrderGoodDiscountInfo info) {
        assert Objects.equals(info.discount.toString(), "0.90");
    }

    public static void assertDiscount3(OrderGoodDiscountInfo info) {
        assert Objects.equals(info.discount.toString(), "0.70");
    }

    public static void assertAddress1(OrderAddressInfo info) {
        assert Objects.equals(info.address, "上海");
    }

    public static void assertRemark1(OrderGoodRemarkInfo info) {
        assert Objects.equals(info.remark, "加冰");
    }

    public static void assertService1(OrderServiceInfo info) {
        assert Objects.equals(info.serviceName, "保价险");
    }

    public static void assertServicePrice1(OrderServicePriceInfo info) {
        assert Objects.equals(info.price.toString(), "99.00");
    }
}
