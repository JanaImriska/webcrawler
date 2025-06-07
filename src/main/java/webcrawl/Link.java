package webcrawl;

public record Link (String title, String url) {

    public Link (String title, String url) {
        this.title = title.trim();
        this.url = url;
    }
}
