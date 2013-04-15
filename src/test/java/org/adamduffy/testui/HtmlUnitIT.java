package org.adamduffy.testui;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class HtmlUnitIT {

    private String webUrl;

    private WebClient webClient;

    @Before
    public void setUp() {
        webUrl = System.getProperty("integration.base.url");
        webClient = new WebClient();
    }

    @After
    public void tearDown() {
        webClient.closeAllWindows();
    }

    /**
     * Test /index.html
     */
    @Test
    public void testIndexHtml() throws Exception {
        System.out.println("Connecting to: " + webUrl);
        HtmlPage page = webClient.getPage(webUrl);
        assertTrue(page.getBody().asText().indexOf("Google") != -1);
    }
}