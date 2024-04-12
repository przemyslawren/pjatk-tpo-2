import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class WebBrowser extends Region {
    private final WebView browser = new WebView();
    private final WebEngine webEngine = browser.getEngine();

    public WebBrowser() {
        getChildren().add(browser);
    }

    public void loadPage(String location) {
        webEngine.load("https://en.wikipedia.org/wiki/" + location);
    }

    @Override
    protected void layoutChildren() {
        double width = getWidth(), height = getHeight();
        layoutInArea(browser,0,0,width,height,0, HPos.CENTER, VPos.CENTER);
    }
}
