package com.color.mall.product.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Test {
    public static void main(String[] args) throws ParseException {
        String s = "202311160000".substring(0,8);
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");

        Date date1Date = sdf1.parse(s);
        String date2Date = sdf2.format(date1Date);
        System.out.println(date2Date);
    }
}
