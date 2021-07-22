package de.bonndan.nivio.input.kubernetes.details;

public abstract class DetailDecorator implements Details {
    protected Details detail;

    protected DetailDecorator(Details detail) {
        this.detail = detail;
    }
}
