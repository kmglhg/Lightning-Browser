package acr.browser.lightning.async;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by kmglh on 2016-12-01.
 */
public interface  GetAllImagesCallback extends Serializable {
    void callback(ArrayList<String> images);
}