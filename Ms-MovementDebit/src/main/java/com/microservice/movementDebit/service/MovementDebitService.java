package com.microservice.movementDebit.service;

import com.microservice.movementDebit.model.Account;
import com.microservice.movementDebit.model.MovementDebit;
import com.microservice.movementDebit.repository.AccountRepository;
import com.microservice.movementDebit.repository.MovementDebitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MovementDebitService {

    private  final MovementDebitRepository movementDebitRepository;
    private final AccountRepository accountRepository;

    public Flux<MovementDebit> getAllCreditMovement(){
        return movementDebitRepository.findAll();
    }
    public Mono<MovementDebit> getCreditMovementById(String id){
        return  movementDebitRepository.findById(id);
    }
    public Mono<MovementDebit> createCreditMovement(MovementDebit movementDebit){
        return accountRepository.findById(movementDebit.getIdAccount())
                .flatMap(account -> {
                    if (movementDebit.getAmount() > 0) {
                        return processDebitMovement(account, movementDebit);
                    } else {
                        return Mono.error(new IllegalArgumentException("El monto del movimiento debe ser mayor a 0."));
                    }
                });
    }

    public Mono<MovementDebit> updateCreditMovement(String id, MovementDebit creditMovement){
        return movementDebitRepository.findById(id)
                .flatMap(bean -> {
                    bean.setAmount(creditMovement.getAmount());
                    bean.setDateStart(creditMovement.getDateStart());
                    bean.setDateLimit(creditMovement.getDateLimit());
                    bean.setCommission(creditMovement.getCommission());
                    bean.setDescription(creditMovement.getDescription());
                    bean.setIdAccount(creditMovement.getIdAccount());
                    return movementDebitRepository.save(bean);
                });
    }
    public Mono<MovementDebit> deleteCreditMovement(String id){
        return movementDebitRepository.findById(id)
                .flatMap(existsCreditMovement -> movementDebitRepository.delete(existsCreditMovement)
                        .then(Mono.just(existsCreditMovement)));
    }


    private Mono<MovementDebit> processDebitMovement(Account account, MovementDebit movementDebit) {
        if (account.getAvailableBalance() < movementDebit.getAmount()) {
            return Mono.error(new IllegalArgumentException("Saldo insuficiente para realizar la transacción."));
        }

        account.setAvailableBalance(account.getAvailableBalance() - movementDebit.getAmount());

        return accountRepository.save(account)
                .then(movementDebitRepository.save(movementDebit));
    }

}
