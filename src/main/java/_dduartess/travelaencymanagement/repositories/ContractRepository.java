package _dduartess.travelaencymanagement.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import _dduartess.travelaencymanagement.entities.contract.Contract;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findByTripId(Long tripId);
}
