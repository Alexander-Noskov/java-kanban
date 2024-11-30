import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        TaskEndpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASKS: {
                handleGetTasks(exchange);
                break;
            }
            case GET_TASK_BY_ID: {
                handleGetTaskById(exchange);
                break;
            }
            case POST_TASK: {
                handlePostTask(exchange);
                break;
            }
            case DELETE_TASK_BY_ID: {
                handleDeleteTaskById(exchange);
                break;
            }
            default:
                sendNotFound(exchange);
        }
    }

    private void handleDeleteTaskById(HttpExchange exchange) throws IOException {
        manager.deleteTask(getId(exchange));
        sendText(exchange, "", 200);
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        try {
            Task task = parseTask(exchange);
            try {
                manager.getTask(task.getId());
                manager.updateTask(task);
                sendText(exchange, "", 201);
            } catch (NotFoundException e) {
                manager.createTask(task);
                sendText(exchange, "", 201);
            }
        } catch (IllegalArgumentException e) {
            sendHasInteractions(exchange);
        }
    }

    private Task parseTask(HttpExchange exchange) throws IOException {
        return gson.fromJson(getBody(exchange), Task.class);
    }

    private void handleGetTaskById(HttpExchange exchange) throws IOException {
        try {
            Task task = manager.getTask(getId(exchange));
            sendText(exchange, gson.toJson(task), 200);
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(manager.getTasks());
        sendText(exchange, response, 200);
    }

    private TaskEndpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");
        if (pathParts.length == 2) {
            if (pathParts[1].equals("tasks")) {
                if (requestMethod.equals("GET")) {
                    return TaskEndpoint.GET_TASKS;
                } else if (requestMethod.equals("POST")) {
                    return TaskEndpoint.POST_TASK;
                }
            }
        } else if (pathParts.length == 3) {
            if (pathParts[1].equals("tasks")) {
                if (requestMethod.equals("GET")) {
                    return TaskEndpoint.GET_TASK_BY_ID;
                } else if (requestMethod.equals("DELETE")) {
                    return TaskEndpoint.DELETE_TASK_BY_ID;
                }
            }
        }
        return TaskEndpoint.UNKNOWN;
    }

    private enum TaskEndpoint {
        GET_TASKS, GET_TASK_BY_ID, POST_TASK, DELETE_TASK_BY_ID, UNKNOWN
    }
}