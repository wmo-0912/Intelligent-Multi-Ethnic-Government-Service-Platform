package com.example.govservice.service.impl;

import com.example.govservice.entity.User;
import com.example.govservice.service.RealNameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RealNameServiceImpl implements RealNameService {
    @Override
    public boolean verifyUser(User user) {
        // 占位：此处可对接公安/民政等外部认证系统
        log.info("[实名认证占位] 调用外部系统核验用户：{}-{}", user.getUserId(), user.getName());
        return true; // 默认通过
    }
}


