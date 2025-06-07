package demo;

import org.apache.logging.log4j.util.Strings;

import javax.swing.*;
import java.util.Objects;

public record Link (String title, String url) {

    public Link (String title, String url) {
        this.title = title.trim();
        this.url = url;
    }
}
