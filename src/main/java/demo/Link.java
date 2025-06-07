package demo;

import javax.swing.*;
import java.util.Objects;

public class Link {
    private String url;
    private String title;

    public Link(String url, String title) {
        this.url = url;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return this.title + ": " + this.url;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.url);
    }

}
