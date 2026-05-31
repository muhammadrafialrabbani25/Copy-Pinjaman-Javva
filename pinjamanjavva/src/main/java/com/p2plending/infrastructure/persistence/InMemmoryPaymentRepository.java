package com.p2plending.infrastructure.persistence;

import com.p2plending.domain.borrower.entity.Payment;
import com.p2plending.domain.borrower.repository.PaymentRepository;
import com.p2plending.domain.shared.PaymentStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class InMemmoryPaymentRepository implements PaymentRepository {

    private final Map<String, Payment> store = SharedStorage.getInstance().getPayments();

    @Override
    public void save(Payment payment) {
        store.put(payment.getId(), payment);
    }

    @Override
    public Optional<Payment> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Payment> findByLoanId(String loanId) {
        return store.values().stream()
                .filter(payment -> payment.getLoanId().equals(loanId))
                .collect(Collectors.toList());
    }

    public void updateStatus(String paymentId, PaymentStatus status) {
        Payment payment = store.get(paymentId);
        if (payment == null) {
            throw new IllegalArgumentException("Payment tidak ditemukan: " + paymentId);
        }
        if (status == PaymentStatus.PAID) {
            payment.pay();
        } else if (status == PaymentStatus.OVERDUE) {
            payment.calculateDenda();
        }
        store.put(paymentId, payment);
    }
}
