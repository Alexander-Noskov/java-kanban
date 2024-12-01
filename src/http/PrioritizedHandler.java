package http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestURI().getPath().equals("/prioritized")) {
            if (exchange.getRequestMethod().equals("GET")) {
                String response = gson.toJson(manager.getPrioritizedTasks());
                sendText(exchange, response, 200);
            }
        }
    }
}
