package de.seinab.form.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class FormUrlParser {

    private PathPattern pathPattern;
    private PathPattern.PathMatchInfo pathInfo;
    private PathContainer pathContainer;

    public FormUrlParser(HttpServletRequest request, String pattern) {
        PathPatternParser pathParser = new PathPatternParser();
        pathParser.setMatchOptionalTrailingSeparator(true);
        this.pathContainer = PathContainer.parsePath(request.getRequestURI());
        this.pathPattern = pathParser.parse(pattern);
        this.pathInfo = this.pathPattern.matchAndExtract(PathContainer.parsePath(request.getRequestURI()));
    }

    public String getFormName(){
        if(pathInfo == null) {
            return "";
        }
        Map<String, String> patternMap = pathInfo.getUriVariables();
        return patternMap.getOrDefault("formname", "");
    }

    public String getEventGroupName(){
        if(pathInfo == null) {
            return "";
        }
        Map<String, String> patternMap = pathInfo.getUriVariables();
        return patternMap.getOrDefault("eventGroup", "");
    }

    public boolean pathMatchesPattern() {
        return this.pathPattern.matches(this.pathContainer);
    }

    public String buildNewUrl(String urlPattern) {
        String formName = getFormName();
        String eventGroupName = getEventGroupName();
        String url = StringUtils.replace(urlPattern, "{formname}", formName);
        url = StringUtils.replace(url, "{eventGroup}", eventGroupName);
        return StringUtils.remove(url, "*");
    }
}
