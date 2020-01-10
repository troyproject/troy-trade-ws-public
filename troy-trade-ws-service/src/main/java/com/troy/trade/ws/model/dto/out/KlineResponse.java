package com.troy.trade.ws.model.dto.out;

import com.troy.commons.dto.out.ResData;

/**
 * Kline 订阅返回实体
 */
public class KlineResponse extends ResData {

    /**
     * 交易对名称 - symbol
     */
    private String s;

    /**
     * k线时间戳 - time
     */
    private Long t;

    /**
     * 成交量 - amount
     */
    private String a;

    /**
     * 开盘价 - open
     */
    private String o;

    /**
     * 收盘价 - close
     */
    private String c;

    /**
     * 最低价 - low
     */
    private String l;

    /**
     * 最高价 - high
     */
    private String h;

    /**
     * 成交额 - vol
     */
    private String v;

    /**
     * 成交笔数 - count
     */
    private Integer co;

    public KlineResponse(String s, Long t, String a, String o, String c, String l, String h, String v, Integer co) {
        if(null == s){
            s = "";
        }
        if(null == t){
            t=0L;
        }
        if(null == a){
            a="0";
        }
        if(null == o){
            o="0";
        }
        if(null == c){
            c="0";
        }
        if(null == l){
            l="0";
        }
        if(null == h){
            h="0";
        }
        if(null == v){
            v="0";
        }
        if(null == co){
            co=0;
        }

        this.s = s;
        this.t = t;
        this.a = a;
        this.o = o;
        this.c = c;
        this.l = l;
        this.h = h;
        this.v = v;
        this.co = co;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public Long getT() {
        return t;
    }

    public void setT(Long t) {
        this.t = t;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getO() {
        return o;
    }

    public void setO(String o) {
        this.o = o;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getL() {
        return l;
    }

    public void setL(String l) {
        this.l = l;
    }

    public String getH() {
        return h;
    }

    public void setH(String h) {
        this.h = h;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public Integer getCo() {
        return co;
    }

    public void setCo(Integer co) {
        this.co = co;
    }
}
