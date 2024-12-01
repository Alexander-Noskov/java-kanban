package http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import entity.Epic;
import entity.Subtask;
import exception.NotFoundException;
import manager.TaskManager;

import java.io.IOException;
import java.util.List;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        EpicEndpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_EPICS: {
                handleGetEpics(exchange);
                break;
            }
            case GET_EPIC_BY_ID: {
                handleGetEpicById(exchange);
                break;
            }
            case GET_SUBTASKS_BY_EPIC_ID: {
                handleGetSubtasksByEpicId(exchange);
                break;
            }
            case POST_EPIC: {
                handlePostEpic(exchange);
                break;
            }
            case DELETE_EPIC_BY_ID: {
                handleDeleteEpicById(exchange);
                break;
            }
            default:
                sendNotFound(exchange);
        }
    }

    private void handleGetSubtasksByEpicId(HttpExchange exchange) throws IOException {
        try {
            Epic epic = manager.getEpic(getId(exchange));

            List<Subtask> subtasks = epic.getSubtaskIds().stream()
                    .map(manager::getSubtask)
                    .toList();
            String response = gson.toJson(subtasks);
            sendText(exchange, response, 200);
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        }
    }

    private void handleDeleteEpicById(HttpExchange exchange) throws IOException {
        manager.deleteEpic(getId(exchange));
        sendText(exchange, "", 200);
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        manager.createEpic(parseEpic(exchange));
        sendText(exchange, "", 201);
    }

    private Epic parseEpic(HttpExchange exchange) throws IOException {
        return gson.fromJson(getBody(exchange), Epic.class);
    }

    private void handleGetEpicById(HttpExchange exchange) throws IOException {
        try {
            Epic epic = manager.getEpic(getId(exchange));
            sendText(exchange, gson.toJson(epic), 200);
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        String response = gson.toJson(manager.getEpics());
        sendText(exchange, response, 200);
    }

    private EpicEndpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");
        if (pathParts.length == 2) {
            if (pathParts[1].equals("epics")) {
                if (requestMethod.equals("GET")) {
                    return EpicEndpoint.GET_EPICS;
                } else if (requestMethod.equals("POST")) {
                    return EpicEndpoint.POST_EPIC;
                }
            }
        } else if (pathParts.length == 3) {
            if (pathParts[1].equals("epics")) {
                if (requestMethod.equals("GET")) {
                    return EpicEndpoint.GET_EPIC_BY_ID;
                } else if (requestMethod.equals("DELETE")) {
                    return EpicEndpoint.DELETE_EPIC_BY_ID;
                }
            }
        } else if (pathParts.length == 4) {
            if (pathParts[1].equals("epics")) {
                if (requestMethod.equals("GET")) {
                    if (pathParts[3].equals("subtasks")) {
                        return EpicEndpoint.GET_SUBTASKS_BY_EPIC_ID;
                    }
                }
            }
        }
        return EpicEndpoint.UNKNOWN;
    }

    private enum EpicEndpoint {
        GET_EPICS,
        GET_EPIC_BY_ID,
        GET_SUBTASKS_BY_EPIC_ID,
        POST_EPIC,
        DELETE_EPIC_BY_ID,
        UNKNOWN
    }
}
