package engine;

import engine.event.EventContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MiddlewareManager {
    private final List<Consumer<EventContext>> middlewareChain = new ArrayList<>();

    public void use(Consumer<EventContext> middleware) {
        middlewareChain.add(middleware);
    }

    public void process(EventContext context, Runnable finalHandler) {
        new MiddlewareExecutor(middlewareChain, finalHandler).execute(context);
    }

    private static class MiddlewareExecutor {
        private final List<Consumer<EventContext>> chain;
        private final Runnable finalHandler;
        private int index = 0;

        public MiddlewareExecutor(List<Consumer<EventContext>> chain, Runnable finalHandler) {
            this.chain = chain;
            this.finalHandler = finalHandler;
        }

        public void execute(EventContext context) {
            if (index < chain.size()) {
                Consumer<EventContext> currentMiddleware = chain.get(index++);
                currentMiddleware.accept(context);
            } else {
                finalHandler.run();
            }
        }
    }
}


