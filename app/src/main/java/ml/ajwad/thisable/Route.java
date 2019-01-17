package ml.ajwad.thisable;

public class Route {
    private Integer routeID;
    private String source;
    private String destination;

    public Route(Integer routeID, String source, String destination) {
        this.routeID = routeID;
        this.source = source;
        this.destination = destination;
    }

    public Integer getRouteID() {
        return this.routeID;
    }

    public String getSource() {
        return this.source;
    }

    public String getDestination() {
        return this.destination;
    }
}
