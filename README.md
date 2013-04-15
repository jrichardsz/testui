```
mvn -Dintegration.base.url=http://www.google.ie -Pintegration verify
```

# Introduction

The dependencies page lists all the jars that you will need to have in your classpath.

The class com.gargoylesoftware.htmlunit.WebClient is the main starting point. This simulates a web browser and will be used to execute all of the tests.

Most unit testing will be done within a framework like JUnit so all the examples here will assume that we are using that.

In the first sample, we create the web client and have it load the homepage from the HtmlUnit website. We then verify that this page has the correct title. Note that getPage() can return different types of pages based on the content type of the returned data. In this case we are expecting a content type of text/html so we cast the result to an com.gargoylesoftware.htmlunit.html.HtmlPage.

```
@Test
public void homePage() throws Exception {
    final WebClient webClient = new WebClient();
    final HtmlPage page = webClient.getPage("http://htmlunit.sourceforge.net");
    Assert.assertEquals("HtmlUnit - Welcome to HtmlUnit", page.getTitleText());

    final String pageAsXml = page.asXml();
    Assert.assertTrue(pageAsXml.contains("<body class=\"composite\">"));

    final String pageAsText = page.asText();
    Assert.assertTrue(pageAsText.contains("Support for the HTTP and HTTPS protocols"));

    webClient.closeAllWindows();
}
```

# Imitating a specific browser

Often you will want to simulate a specific browser. This is done by passing a com.gargoylesoftware.htmlunit.BrowserVersion into the WebClient constructor. Constants have been provided for some common browsers but you can create your own specific version by instantiating a BrowserVersion.

```
@Test
public void homePage_Firefox() throws Exception {
    final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_17);
    final HtmlPage page = webClient.getPage("http://htmlunit.sourceforge.net");
    Assert.assertEquals("HtmlUnit - Welcome to HtmlUnit", page.getTitleText());

    webClient.closeAllWindows();
}
```

Specifying this BrowserVersion will change the user agent header that is sent up to the server and will change the behavior of some of the JavaScript.

# Finding a specific element

Once you have a reference to an HtmlPage, you can search for a specific HtmlElement by one of 'get' methods, or by using XPath.

Below is an example of finding a 'div' by an ID, and getting an anchor by name:

```
@Test
public void getElements() throws Exception {
    final WebClient webClient = new WebClient();
    final HtmlPage page = webClient.getPage("http://some_url");
    final HtmlDivision div = page.getHtmlElementById("some_div_id");
    final HtmlAnchor anchor = page.getAnchorByName("anchor_name");

    webClient.closeAllWindows();
}
```

XPath is the suggested way for more complex searches, a brief tutorial can be found in W3Schools

```
@Test
public void xpath() throws Exception {
    final WebClient webClient = new WebClient();
    final HtmlPage page = webClient.getPage("http://htmlunit.sourceforge.net");

    //get list of all divs
    final List<?> divs = page.getByXPath("//div");

    //get div which has a 'name' attribute of 'John'
    final HtmlDivision div = (HtmlDivision) page.getByXPath("//div[@name='John']").get(0);

    webClient.closeAllWindows();
}
```

# Using a proxy server

The last WebClient constructor allows you to specify proxy server information in those cases where you need to connect through one.

```
@Test
public void homePage_proxy() throws Exception {
    final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_10, "http://myproxyserver", myProxyPort);

    //set proxy username and password 
    final DefaultCredentialsProvider credentialsProvider = (DefaultCredentialsProvider) webClient.getCredentialsProvider();
    credentialsProvider.addCredentials("username", "password");

    final HtmlPage page = webClient.getPage("http://htmlunit.sourceforge.net");
    Assert.assertEquals("HtmlUnit - Welcome to HtmlUnit", page.getTitleText());

    webClient.closeAllWindows();
}
```

Specifying this BrowserVersion will change the user agent header that is sent up to the server and will change the behavior of some of the JavaScript.

# Submitting a form

Frequently we want to change values in a form and submit the form back to the server. The following example shows how you might do this.

```
@Test
public void submittingForm() throws Exception {
    final WebClient webClient = new WebClient();

    // Get the first page
    final HtmlPage page1 = webClient.getPage("http://some_url");

    // Get the form that we are dealing with and within that form, 
    // find the submit button and the field that we want to change.
    final HtmlForm form = page1.getFormByName("myform");

    final HtmlSubmitInput button = form.getInputByName("submitbutton");
    final HtmlTextInput textField = form.getInputByName("userid");

    // Change the value of the text field
    textField.setValueAttribute("root");

    // Now submit the form by clicking the button and get back the second page.
    final HtmlPage page2 = button.click();

    webClient.closeAllWindows();
}
```

# Tables

The first set of examples will use this simple html.

```
<html><head><title>Table sample</title></head><body>
    <table id="table1">
        <tr>
            <th>Number</th>
            <th>Description</th>
        </tr>
        <tr>
            <td>5</td>
            <td>Bicycle</td>
        </tr>
    </table>
</body></html>
```

This example shows how to iterate over all the rows and cells

```
final HtmlTable table = page.getHtmlElementById("table1");
for (final HtmlTableRow row : table.getRows()) {
    System.out.println("Found row");
    for (final HtmlTableCell cell : row.getCells()) {
        System.out.println("   Found cell: " + cell.asText());
    }
}
```

The following sample shows how to access specific cells by row and column

final WebClient webClient = new WebClient();
final HtmlPage page = webClient.getPage("http://foo.com");

final HtmlTable table = page.getHtmlElementById("table1");
System.out.println("Cell (1,2)=" + table.getCellAt(1,2));
More complex table

The next examples will use a more complicated table that includes table header, footer and body sections as well as a caption

```
<html><head><title>Table sample</title></head><body>
    <table id="table1">
        <caption>My complex table</caption>
        <thead>
            <tr>
                <th>Number</th>
                <th>Description</th>
            </tr>
        </thead>
        <tfoot>
            <tr>
                <td>7</td>
                <td></td>
            </tr>
        </tfoot>
        <tbody>
            <tr>
                <td>5</td>
                <td>Bicycle</td>
            </tr>
        </tbody>
        <tbody>
            <tr>
                <td>2</td>
                <td>Tricycle</td>
            </tr>
        </tbody>
    </table>
</body></html>
```

HtmlTableHeader, HtmlTableFooter and HtmlTableBody sections are groupings of rows. There can be at most one header and one footer but there may be more than one body. Each one of these contains rows which can be accessed via getRows()

```
final HtmlTableHeader header = table.getHeader();
final List<HtmlTableRow> headerRows = header.getRows();

final HtmlTableFooter footer = table.getFooter();
final List<HtmlTableRow> footerRows = footer.getRows();

for (final HtmlTableBody body : table.getBodies()) {
    final List rows = body.getRows();
    ...
}
```

Every table may optionally have a caption element which describes it.

```
final String caption = table.getCaptionText()
```
