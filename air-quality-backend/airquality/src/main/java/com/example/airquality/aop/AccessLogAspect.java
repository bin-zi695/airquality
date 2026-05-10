package com.example.airquality.aop;

import com.example.airquality.entity.AccessLog;
import com.example.airquality.service.AccessLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Aspect
@Component
public class AccessLogAspect {

    private final AccessLogService accessLogService;

    public AccessLogAspect(AccessLogService accessLogService) {
        this.accessLogService = accessLogService;
    }

    @Pointcut("execution(* com.example.airquality.controller..*(..))")
    public void controllerPointcut() {}

    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        AccessLog log = new AccessLog();
        if (request != null) {
            log.setIpAddress(getClientIp(request));
            log.setRequestMethod(request.getMethod());
            log.setRequestUrl(request.getRequestURI());
            log.setUserAgent(request.getHeader("User-Agent"));
        }
        log.setCreatedAt(LocalDateTime.now());

        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() - start;
            log.setExecutionTime(elapsed);
            log.setResponseStatus(200);
            log.setRequestParams("success");
            return result;
        } catch (Throwable e) {
            long elapsed = System.currentTimeMillis() - start;
            log.setExecutionTime(elapsed);
            log.setResponseStatus(500);
            log.setRequestParams(e.getMessage());
            throw e;
        } finally {
            try {
                accessLogService.save(log);
            } catch (Exception ignored) {}
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
