package com.D2D.personal_financier.service;

import com.D2D.personal_financier.dto.transactionDTO.TransactionRequestDto;
import com.D2D.personal_financier.dto.transactionDTO.TransactionResponseDto;
import com.D2D.personal_financier.entity.Transaction;
import com.D2D.personal_financier.mapper.TransactionMapper;
import com.D2D.personal_financier.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public TransactionResponseDto createTransaction(TransactionRequestDto dto) {
        Transaction transaction = transactionMapper.toEntity(dto);
        transactionRepository.save(transaction);
        return transactionMapper.toDto(transaction);
    }

    public List<TransactionResponseDto> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());
    }

    public TransactionResponseDto getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        return transactionMapper.toDto(transaction);
    }

    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }
}


