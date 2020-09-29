package com.leyou.sms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * SMS阿里短信接口的配置读取类
 *
 * @author zzx
 * @date 2020-09-29 11:05:01
 */
@ConfigurationProperties(prefix = "leyou.sms") // 读取leyou.sms为前缀的
public class SmsProperties {
    // 属性名，与application.yml中的配置名保持一致
    String accessKeyId;
    String accessKeySecret;
    String signName;
    String verifyCodeTemplate;

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getSignName() {
        return signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    public String getVerifyCodeTemplate() {
        return verifyCodeTemplate;
    }

    public void setVerifyCodeTemplate(String verifyCodeTemplate) {
        this.verifyCodeTemplate = verifyCodeTemplate;
    }
}
