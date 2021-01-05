package revolver.headead.core.display;

public class Header implements ListItem {

    private final String label;

    public Header(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
