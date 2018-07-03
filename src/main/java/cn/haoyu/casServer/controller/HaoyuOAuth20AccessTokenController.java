package cn.haoyu.casServer.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.jasig.cas.support.oauth.OAuthConstants;
import org.jasig.cas.support.oauth.OAuthUtils;
import org.jasig.cas.support.oauth.OAuthWebApplicationService;
import org.jasig.cas.support.oauth.services.OAuthRegisteredService;
import org.jasig.cas.support.oauth.web.AccessTokenGenerator;
import org.jasig.cas.support.oauth.web.BaseOAuthWrapperController;
import org.jasig.cas.ticket.ServiceTicket;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * Created by haoyu on 2018/6/28.
 */
@Component("haoyuOAuth20AccessTokenController")
public class HaoyuOAuth20AccessTokenController extends BaseOAuthWrapperController {

    @Autowired
    @Qualifier("defaultAccessTokenGenerator")
    private AccessTokenGenerator accessTokenGenerator;

    @Override
    protected ModelAndView internalHandleRequest(String method, HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws Exception {
        final String redirectUri = request.getParameter(OAuthConstants.REDIRECT_URI);
        logger.debug("{} : {}", OAuthConstants.REDIRECT_URI, redirectUri);

        final String clientId = request.getParameter(OAuthConstants.CLIENT_ID);
        logger.debug("{} : {}", OAuthConstants.CLIENT_ID, clientId);

        final String clientSecret = request.getParameter(OAuthConstants.CLIENT_SECRET);

        final String code = request.getParameter(OAuthConstants.CODE);
        logger.debug("{} : {}", OAuthConstants.CODE, code);

        final boolean isVerified = verifyAccessTokenRequest(response, redirectUri, clientId, clientSecret, code);
        if (!isVerified) {
            return OAuthUtils.writeTextError(response, OAuthConstants.INVALID_REQUEST, HttpStatus.SC_BAD_REQUEST);
        }

        final ServiceTicket serviceTicket = (ServiceTicket) ticketRegistry.getTicket(code);
        // service ticket should be valid
        if (serviceTicket == null || serviceTicket.isExpired()) {
            logger.error("Code expired : {}", code);
            return OAuthUtils.writeTextError(response, OAuthConstants.INVALID_GRANT, HttpStatus.SC_BAD_REQUEST);
        }
        final TicketGrantingTicket ticketGrantingTicket = serviceTicket.getGrantingTicket();
        // remove service ticket
        ticketRegistry.deleteTicket(serviceTicket.getId());

        final OAuthRegisteredService registeredService = OAuthUtils.getRegisteredOAuthService(this.servicesManager, clientId);
        final OAuthWebApplicationService service = new OAuthWebApplicationService(registeredService.getId());
        final String accessTokenEncoded = this.accessTokenGenerator.generate(service, ticketGrantingTicket);
        final int expires = (int) (this.timeout - TimeUnit.MILLISECONDS
                .toSeconds(System.currentTimeMillis() - ticketGrantingTicket.getCreationTime()));
        final String text = String.format("%s=%s&%s=%s", OAuthConstants.ACCESS_TOKEN, accessTokenEncoded, OAuthConstants.EXPIRES, expires);
        logger.debug("OAuth access token response: {}", text);
        response.setContentType("text/plain");
        return OAuthUtils.writeText(response, text, HttpStatus.SC_OK);
    }

    private boolean verifyAccessTokenRequest(final HttpServletResponse response, final String redirectUri,
                                             final String clientId, final String clientSecret, final String code) {

        // clientId is required
        if (StringUtils.isBlank(clientId)) {
            logger.error("Missing {}", OAuthConstants.CLIENT_ID);
            return false;
        }
        // redirectUri is required
        if (StringUtils.isBlank(redirectUri)) {
            logger.error("Missing {}", OAuthConstants.REDIRECT_URI);
            return false;
        }
        // clientSecret is required
        if (StringUtils.isBlank(clientSecret)) {
            logger.error("Missing {}", OAuthConstants.CLIENT_SECRET);
            return false;
        }
        // code is required
        if (StringUtils.isBlank(code)) {
            logger.error("Missing {}", OAuthConstants.CODE);
            return false;
        }

        final OAuthRegisteredService service = OAuthUtils.getRegisteredOAuthService(this.servicesManager, clientId);
        if (service == null) {
            logger.error("Unknown {} : {}", OAuthConstants.CLIENT_ID, clientId);
            return false;
        }

        final String serviceId = service.getServiceId();
        if (!redirectUri.matches(serviceId)) {
            logger.error("Unsupported {} : {} for serviceId : {}", OAuthConstants.REDIRECT_URI, redirectUri, serviceId);
            return false;
        }

        if (!StringUtils.equals(service.getClientSecret(), clientSecret)) {
            logger.error("Wrong client secret for service {}", service);
            return false;
        }
        return true;
    }

}
