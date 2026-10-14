package com.example.govservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 多语言配置类
 */
@Configuration
@PropertySource("classpath:languages/zh-CN.properties")
@PropertySource("classpath:languages/mn.properties")
@PropertySource("classpath:languages/bo-CN.properties")
@PropertySource("classpath:languages/ug-CN.properties")
@PropertySource("classpath:languages/kk.properties")
@PropertySource("classpath:languages/ko.properties")
@PropertySource("classpath:languages/yi.properties")
@PropertySource("classpath:languages/za.properties")
@PropertySource("classpath:languages/bgc.properties")
@PropertySource("classpath:languages/hmn.properties")
@PropertySource("classpath:languages/kmc.properties")
@PropertySource("classpath:languages/yao.properties")
@PropertySource("classpath:languages/bai.properties")
@PropertySource("classpath:languages/hni.properties")
@PropertySource("classpath:languages/tai.properties")
@PropertySource("classpath:languages/lis.properties")
@PropertySource("classpath:languages/lah.properties")
@PropertySource("classpath:languages/wa.properties")
@PropertySource("classpath:languages/nxi.properties")
public class LanguageConfig {
    
    /**
     * 支持的语言类型
     */
    public enum LanguageType {
        ZH_CN("zh-CN", "中文"),
        MN("mn", "蒙古语"),
        BO("bo", "藏语"),
        UG("ug", "维吾尔语"),
        KK("kk", "哈萨克语"),
        KO("ko", "朝鲜语"),
        YI("yi", "彝语"),
        ZA("za", "壮语"),
        BGC("bgc", "布依语"),
        HMN("hmn", "苗语"),
        KMC("kmc", "侗语"),
        YAO("yao", "瑶语"),
        BAI("bai", "白语"),
        HNI("hni", "哈尼语"),
        TAI("tai", "傣语"),
        LIS("lis", "傈僳语"),
        LAH("lah", "拉祜语"),
        WA("wa", "佤语"),
        NXI("nxi", "纳西语");
        
        private final String code;
        private final String name;
        
        LanguageType(String code, String name) {
            this.code = code;
            this.name = name;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getName() {
            return name;
        }
        
        /**
         * 根据代码获取语言类型
         */
        public static LanguageType fromCode(String code) {
            for (LanguageType type : values()) {
                if (type.code.equals(code)) {
                    return type;
                }
            }
            return null;
        }
        
        /**
         * 检查是否支持该语言
         */
        public static boolean isSupported(String code) {
            return fromCode(code) != null;
        }
    }
}
