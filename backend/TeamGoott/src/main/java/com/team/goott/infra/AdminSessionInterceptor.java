package com.team.goott.infra;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.team.goott.admin.domain.AdminDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AdminSessionInterceptor implements HandlerInterceptor{

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		HttpSession session = request.getSession(false);
		log.info("interceptor check for :: {}", session.getId());
        AdminDTO adminSession = (AdminDTO) session.getAttribute("admin");

        if (adminSession == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("권한이 없습니다.");
            return false; // 요청을 중단
        }
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
