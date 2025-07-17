package ru.practicum.request;

import java.util.List;

public interface RequestService {
    Request addRequest(Request request);
    Request get(long userId, long requestId);
    List<Request> getMyRequests(long userId);
    List<Request> getOtherRequests(long userId);
}
