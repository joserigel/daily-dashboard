package dailydashboard.Models;

public enum MensaLocation {
    ACADEMICA("academica", "https://www.studierendenwerk-aachen.de/speiseplaene/academica-w-en.html"),
    AHORNSTRASSE("ahornstrasse", "https://www.studierendenwerk-aachen.de/speiseplaene/ahornstrasse-w-en.html");

    private final String label;
    private final String url;
    private MensaLocation(String label, String url) {
        this.url = url;
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    public String getUrl() {
        return this.url;
    }
}
