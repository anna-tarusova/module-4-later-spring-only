package ru.practicum.request;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long>  {
    List<Request> findByUserIdOrderByCreatedDesc(long userId);
    List<Request> findByUserIdNotOrderByCreatedDesc(long userId);
}
