package web.filter;

import entity.Employee;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import util.enumeration.AccessRightEnum;

/**
 *
 * @author tjle2
 */
@WebFilter(filterName = "SecurityFilter", urlPatterns = {"/*"})

public class SecurityFilter implements Filter {

    FilterConfig filterConfig;

    private static final String CONTEXT_ROOT = "/tellybuddy-war";

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        HttpSession httpSession = httpServletRequest.getSession(true);
        String requestServletPath = httpServletRequest.getServletPath();

        if (httpSession.getAttribute("isLogin") == null) {
            httpSession.setAttribute("isLogin", false);
        }

        Boolean isLogin = (Boolean) httpSession.getAttribute("isLogin");

        if (!excludeLoginCheck(requestServletPath)) {
            if (isLogin == true) {
                Employee currentEmployee = (Employee) httpSession.getAttribute("currentEmployee");

                if (checkAccessRight(requestServletPath, currentEmployee.getAccessRightEnum())) {
                    chain.doFilter(request, response);
                } else {
                    httpServletResponse.sendRedirect(CONTEXT_ROOT + "/accessRightError.xhtml");
                }
            } else {
                httpServletResponse.sendRedirect(CONTEXT_ROOT + "/accessRightError.xhtml");
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    public void destroy() {

    }

    private Boolean checkAccessRight(String path, AccessRightEnum accessRight) {
        if (accessRight.equals(AccessRightEnum.EMPLOYEE)) {
            if (path.equals("/management/home.xhtml")
                    || path.equals("/management/account/updateParticulars.html")
                    || path.equals("/management/plans/main.xhtml")
                    || path.equals("/management/products/main.xhtml")
                    || path.equals("/management/customers/main.xhtml")
                    || path.equals("/management/announcements/main.xhtml")
                    || path.equals("/management/promotions/main.xhtml")
                    || path.equals("/management/announcements/pastAnnouncement.xhtml")) {
                return true;
            } else {
                return false;
            }
        } else if (accessRight.equals(AccessRightEnum.MANAGER)) {
            if (path.contains("/management")) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    private Boolean excludeLoginCheck(String path) {
        if (path.equals("/index.xhtml")
                || path.equals("/accessRightError.xhtml")
                || path.startsWith("/javax.faces.resource")
                || path.equals("/resources/images/background.jpg")) {
            return true;
        } else {
            return false;
        }
    }
}
