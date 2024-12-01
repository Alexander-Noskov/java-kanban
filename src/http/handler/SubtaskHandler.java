package http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import entity.Subtask;
import exception.NotFoundException;
import manager.TaskManager;

import java.io.IOException;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        SubtaskEndpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_SUBTASKS: {
                handleGetSubtasks(exchange);
                break;
            }
            case GET_SUBTASK_BY_ID: {
                handleGetSubtaskById(exchange);
                break;
            }
            case POST_SUBTASK: {
                handlePostSubtask(exchange);
                break;
            }
            case DELETE_SUBTASK_BY_ID: {
                handleDeleteSubtaskById(exchange);
                break;
            }
            default:
                sendNotFound(exchange);
        }
    }

    private void handleDeleteSubtaskById(HttpExchange exchange) throws IOException {
        manager.deleteSubtask(getId(exchange));
        sendText(exchange, "", 200);
    }

    private void handlePostSubtask(HttpExchange exchange) throws IOException {
        try {
            Subtask subtask = parseSubtask(exchange);
            try {
                manager.getSubtask(subtask.getId());
                manager.updateSubtask(subtask);
                sendText(exchange, "", 201);
            } catch (NotFoundException e) {
                manager.createSubtask(subtask);
                sendText(exchange, "", 201);
            }
        } catch (IllegalArgumentException e) {
            sendHasInteractions(exchange);
        }
    }

    private Subtask parseSubtask(HttpExchange exchange) throws IOException {
        return gson.fromJson(getBody(exchange), Subtask.class);
    }

    private void handleGetSubtaskById(HttpExchange exchange) throws IOException {
        try {
            Subtask subtask = manager.getSubtask(getId(exchange));
            sendText(exchange, gson.toJson(subtask), 200);
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        }
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(manager.getSubtasks());
        sendText(exchange, response, 200);
    }

    private SubtaskEndpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");
        if (pathParts.length == 2) {
            if (pathParts[1].equals("subtasks")) {
                if (requestMethod.equals("GET")) {
                    return SubtaskEndpoint.GET_SUBTASKS;
                } else if (requestMethod.equals("POST")) {
                    return SubtaskEndpoint.POST_SUBTASK;
                }
            }
        } else if (pathParts.length == 3) {
            if (pathParts[1].equals("subtasks")) {
                if (requestMethod.equals("GET")) {
                    return SubtaskEndpoint.GET_SUBTASK_BY_ID;
                } else if (requestMethod.equals("DELETE")) {
                    return SubtaskEndpoint.DELETE_SUBTASK_BY_ID;
                }
            }
        }
        return SubtaskEndpoint.UNKNOWN;
    }

    private enum SubtaskEndpoint {
        GET_SUBTASKS,
        GET_SUBTASK_BY_ID,
        POST_SUBTASK,
        DELETE_SUBTASK_BY_ID,
        UNKNOWN
    }
}