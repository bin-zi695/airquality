package com.example.airquality.aop;

import com.example.airquality.entity.OperationLog;
import com.example.airquality.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
public class OperationLogAspect {

    private final OperationLogService operationLogService;

    public OperationLogAspect(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    @Pointcut("@annotation(com.example.airquality.aop.OperLog)")
    public void operationLogPointcut() {}

    @Around("operationLogPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        OperationLog log = new OperationLog();
        if (request != null) {
            log.setIpAddress(getClientIp(request));
            log.setRequestMethod(request.getMethod());
            log.setRequestUrl(request.getRequestURI());
        }
        String params = Arrays.toString(joinPoint.getArgs());
        if (params.length() > 400) {
            params = params.substring(0, 400);
        }
        log.setRequestParams(params);
        log.setOperationTime(LocalDateTime.now());

        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() - start;
            log.setStatus(1);
            log.setExecutionTime(elapsed);
            return result;
        } catch (Throwable e) {
            long elapsed = System.currentTimeMillis() - start;
            log.setStatus(0);
            log.setExecutionTime(elapsed);
            log.setErrorMsg(e.getMessage() != null && e.getMessage().length() > 400
                    ? e.getMessage().substring(0, 400) : e.getMessage());
            throw e;
        } finally {
            try {
                operationLogService.save(log);
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
