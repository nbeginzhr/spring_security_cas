package cn.haoyu.casServer.controller;

import cn.haoyu.casServer.configuration.Constans;
import org.apache.http.HttpStatus;
import org.jasig.cas.support.oauth.OAuthConstants;
import org.jasig.cas.support.oauth.OAuthUtils;
import org.jasig.cas.support.oauth.web.BaseOAuthWrapperController;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by haoyu on 2018/6/28.
 */
@Component("haoyuOAuth20WrapperController")
public class HaoyuOAuth20WrapperController extends BaseOAuthWrapperController {


    @Resource(name = "haoyuOAuth20AccessTokenController")
    private Controller accessTokenController;


    @Override
    protected ModelAndView internalHandleRequest(final String method, final HttpServletRequest request,
                                                 final HttpServletResponse response) throws Exception {

        //get access token
        if (Constans.ACCESS_TOKEN_URL.equals(method)) {
            return accessTokenController.handleRequest(request, response);
        }

        // else error
        logger.error("Unknown method : {}", method);
        OAuthUtils.writeTextError(response, Constans.INVALID_REQUEST, HttpStatus.SC_OK);
        return null;
    }

    public Controller getAccessTokenController() {
        return accessTokenController;
    }

}
