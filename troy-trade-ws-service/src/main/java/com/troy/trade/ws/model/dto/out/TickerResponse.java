package com.troy.trade.ws.model.dto.out;

import com.troy.commons.dto.out.ResData;

/**
 * ticker 订阅返回实体
 */
public class TickerResponse extends ResData {

    /**
     * 交易对名称 - symbol
     */
    private String s;

    /**
     * 收盘价 - close
     */
    private String c;

    /**
     * 涨跌幅 - percentChange
     */
    private String pc;

    /**
     * 24h最高价 - high
     */
    private String h;

    /**
     * 24h最低价 - low
     */
    private String l;

    /**
     * 24小时成交量，以基础币种计量--24小时成交量 - amount
     */
    private String a;

    /**
     * 24小时成交量，以报价币种计量--24小时成交额 - vol
     */
    private String v;

    /**
     * 当前价格转人民币的数值 - cny
     */
    private String cy;

    public TickerResponse(String s, String c, String pc, String h, String l, String a, String v, String cy) {
        if(null == s){
            s = "";
        }
        if(null == c){
            c = "0";
        }
        if(null == pc){
            pc = "0";
        }
        if(null == h){
            h = "0";
        }
        if(null == l){
            l = "0";
        }
        if(null == a){
            a = "0";
        }
        if(null == v){
            v = "0";
        }
        if(null == cy){
            cy = "0";
        }

        this.s = s;
        this.c = c;
        this.pc = pc;
        this.h = h;
        this.l = l;
        this.a = a;
        this.v = v;
        this.cy = cy;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getPc() {
        return pc;
    }

    public void setPc(String pc) {
        this.pc = pc;
    }

    public String getH() {
        return h;
    }

    public void setH(String h) {
        this.h = h;
    }

    public String getL() {
        return l;
    }

    public void setL(String l) {
        this.l = l;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public String getCy() {
        return cy;
    }

    public void setCy(String cy) {
        this.cy = cy;
    }
}