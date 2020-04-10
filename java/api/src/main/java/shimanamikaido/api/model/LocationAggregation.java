package shimanamikaido.api.model;

public class LocationAggregation {
    private int functional;
    private int failed;

    public LocationAggregation() {
        functional = 0;
        failed = 0;
    }

    public int getFunctional() {
        return functional;
    }

    public void setFunctional(int functional) {
        this.functional = functional;
    }

    public int getFailed() {
        return failed;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }
}