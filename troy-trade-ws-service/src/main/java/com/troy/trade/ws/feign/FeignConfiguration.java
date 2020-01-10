package com.troy.trade.ws.feign;

import com.alibaba.fastjson.JSONObject;
import feign.Request;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

@Slf4j
@Configuration
public class FeignConfiguration implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if(attributes == null){
                return;
            }
            HttpServletRequest request = attributes.getRequest();
            if(request == null){
                return;
            }
            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                    String name = headerNames.nextElement();
                    String values = request.getHeader(name);
                    requestTemplate.header(name, values);
                }
                log.info("feign interceptor header:{}", requestTemplate);
            }
            Request request1 = requestTemplate.request();
            byte[] body = request1.body();
            if (null != body) {
                String bodyStr = new String(body, "UTF-8");
                log.info("请求参数bodyStr=" + bodyStr);
                JSONObject bodyJSON = JSONObject.parseObject(bodyStr);
                if (bodyJSON.containsKey("data")) {
                    JSONObject dataJSON = bodyJSON.getJSONObject("data");
                    if (null != dataJSON && dataJSON.containsKey("token")) {
                        String token = dataJSON.getString("token");
                        requestTemplate.header("Authorization", token);
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}