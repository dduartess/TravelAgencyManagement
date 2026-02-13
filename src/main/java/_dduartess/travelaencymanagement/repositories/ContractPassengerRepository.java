package _dduartess.travelaencymanagement.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import _dduartess.travelaencymanagement.entities.contract.ChargeType;
import _dduartess.travelaencymanagement.entities.contract.ContractPassenger;

public interface ContractPassengerRepository extends JpaRepository<ContractPassenger, Long> {

    long countByContractIdAndChargeType(Long contractId, ChargeType chargeType);

    boolean existsByContractIdAndCustomerId(Long contractId, Long customerId);

    boolean existsByContractTripIdAndCustomerId(Long tripId, Long customerId);

    Optional<ContractPassenger> findByContractIdAndCustomerId(Long contractId, Long customerId);

    List<ContractPassenger> findByContractId(Long contractId);

    long countByContractTripIdAndCustomerId(Long tripId, Long customerId);
}
