package acr.browser.lightning.model;

/**
 * Created by kmglh on 2017-03-07.
 */
public class GridModel {
    private boolean selected;

    private String url;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
