package com.ll.blog.aspect;

import lombok.Data;
import lombok.ToString;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * description
 * @author lilei
 * @date 2022/8/5 17:30
 */
@Aspect
@Component
public class LogAspect {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Pointcut("execution(* com.ll.blog.web.*.*(..))")
    public void log(){}

    @Before("log()")
    public void doBefore(JoinPoint joinPoint){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String url = request.getRequestURL().toString();
        String ip = request.getRemoteAddr();
        String classMethod = joinPoint.getSignature().getDeclaringTypeName()+"."+joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        RequestLog requestLog = new RequestLog(url, ip, classMethod, args);
        logger.info("Request： {}", requestLog);
    }

    @After("log()")
    public void doAfter(){
    }

    @AfterReturning(returning = "result", pointcut = "log()")
    public void doAfterReturn(Object result) throws Exception{
        logger.info("result: {}", result);
    }

    @Data
    @ToString
    private class RequestLog {
        private String url;
        private String ip;
        private String classMethod;
        private Object[] args;

        public RequestLog(String url, String ip, String classMethod, Object[] args) {
            this.url = url;
            this.ip = ip;
            this.classMethod = classMethod;
            this.args = args;
        }
    }
}
