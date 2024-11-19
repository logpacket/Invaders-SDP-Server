package engine.event;

public interface EventHandler {
    /**
     * Handle on event
     *
     * @param eventContext
     *      EventContext for handle
     */
    void handle(EventContext eventContext);
}
