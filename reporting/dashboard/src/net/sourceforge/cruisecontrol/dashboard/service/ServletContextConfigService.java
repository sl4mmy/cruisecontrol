package net.sourceforge.cruisecontrol.dashboard.service;

import javax.servlet.ServletContext;

import net.sourceforge.cruisecontrol.dashboard.exception.ConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.context.ServletContextAware;

public class ServletContextConfigService implements ServletContextAware, DashboardConfigService {
    private static final String WARNING_MESSAGE =
            "Configure dashboard via web.xml is deprecated. Use system properties or dashboard-config.xml instead.";

    private static final Logger LOGGER = Logger.getLogger(ServletContextConfigService.class);

    public static final String CONTEXT_CC_CONFIG_FILE = "cruisecontrol.config.file";

    public static final String CONTEXT_CC_CONFIG_EDITABLE = "cruisecontrol.config.editable";

    public static final String CONTEXT_CC_CONFIG_JMX_PORT = "cruisecontrol.jmxport";

    public static final String CONTEXT_CC_CONFIG_RMI_PORT = "cruisecontrol.rmiport";

    public static final String CONTEXT_CC_CONFIG_FORCEBUILD_ENABLED = "cruisecontrol.config.forcebuild";

    public static final String CONTEXT_CC_CONFIG_LOG_DIR = "cruisecontrol.logdir";

    public static final String CONTEXT_CC_CONFIG_ARTIFACTS_DIR = "cruisecontrol.artifacts";

    public static final String CONTEXT_CC_CONFIG_PROJECTS_DIR = "cruisecontrol.projects";

    private ServletContext servletContext;

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public String getArtifactsDir() throws ConfigurationException {
        LOGGER.warn(WARNING_MESSAGE);
        return StringUtils.defaultString(servletContext.getInitParameter(CONTEXT_CC_CONFIG_ARTIFACTS_DIR));
    }

    public String getConfigXml() {
        LOGGER.warn(WARNING_MESSAGE);
        return StringUtils.defaultString(servletContext.getInitParameter(CONTEXT_CC_CONFIG_FILE));
    }

    public String getJMXPort() {
        LOGGER.warn(WARNING_MESSAGE);
        return StringUtils.defaultString(servletContext.getInitParameter(CONTEXT_CC_CONFIG_JMX_PORT));
    }

    public String getLogsDir() throws ConfigurationException {
        LOGGER.warn(WARNING_MESSAGE);
        return StringUtils.defaultString(servletContext.getInitParameter(CONTEXT_CC_CONFIG_LOG_DIR));
    }

    public String getProjectsDir() throws ConfigurationException {
        LOGGER.warn(WARNING_MESSAGE);
        return StringUtils.defaultString(servletContext.getInitParameter(CONTEXT_CC_CONFIG_PROJECTS_DIR));
    }

    public String getRMIPort() {
        LOGGER.warn(WARNING_MESSAGE);
        return StringUtils.defaultString(servletContext.getInitParameter(CONTEXT_CC_CONFIG_RMI_PORT));
    }

    public String isConfigFileEditable() {
        LOGGER.warn(WARNING_MESSAGE);
        return StringUtils.defaultString(servletContext.getInitParameter(CONTEXT_CC_CONFIG_EDITABLE));
    }

    public String isForceBuildEnabled() {
        LOGGER.warn(WARNING_MESSAGE);
        return StringUtils.defaultString(servletContext
                .getInitParameter(CONTEXT_CC_CONFIG_FORCEBUILD_ENABLED));
    }

    public String getCCHome() {
        LOGGER.warn(WARNING_MESSAGE);
        return "";
    }
}
