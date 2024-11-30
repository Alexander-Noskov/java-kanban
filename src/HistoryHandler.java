import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestURI().getPath().equals("/history")) {
            if (exchange.getRequestMethod().equals("GET")) {
                String response = gson.toJson(manager.getHistory());
                sendText(exchange, response, 200);
            }
        }
    }
}
