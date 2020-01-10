package com.troy.trade.ws.model.dto.out;

import com.troy.commons.dto.out.ResData;


/**
 * ResponseDto
 *
 * @author yanping
 * @date 2019/8/06
 */
public class ResponseDto extends ResData {
    /**
     * 返回code码
     */
    private String code="";

    /**
     * code码描述信息
     */
    private String msg="";

    /**
     * 时间戳
     */
    private Long ts=System.currentTimeMillis();

    /**
     * 推送消息类型
     */
    private String method="";

    /**
     * 返回结果
     */
    private Object result;

    public ResponseDto() {
        super();
    }

    public ResponseDto(String code, Object result, String method) {
        this.ts = System.currentTimeMillis();
        this.code = code;
        this.result = result;
        this.method = method;
    }

    public ResponseDto(String code, String msg, String method, Object result) {
        this.ts = System.currentTimeMillis();
        this.code = code;
        this.msg = msg;
        this.result = result;
        this.method = method;
    }

    public ResponseDto(String code, String msg, Long ts, String method, Object result) {
        super();
        if(null == code){
            code = "201";
        }
        if(null == msg){
            msg = "";
        }
        if(null == ts){
            ts = System.currentTimeMillis();
        }

        if(null == method){
            method = "";
        }

        this.code = code;
        this.msg = msg;
        this.ts = ts;
        this.method = method;
        this.result = result;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
