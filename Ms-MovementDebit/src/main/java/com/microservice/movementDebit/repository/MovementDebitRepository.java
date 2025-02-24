package com.microservice.movementDebit.repository;

import com.microservice.movementDebit.model.MovementDebit;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovementDebitRepository extends ReactiveCrudRepository<MovementDebit, String>{
}
