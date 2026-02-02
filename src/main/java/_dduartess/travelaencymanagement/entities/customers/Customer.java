package _dduartess.travelaencymanagement.entities.customers;

import java.time.LocalDate;

import _dduartess.travelaencymanagement.dtos.customer.CustomerDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_customers")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Pattern(regexp = "^[\\p{L}\\s'-]+$", message = "O nome não pode conter números ou caracteres especiais")
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @NotBlank(message = "O documento é obrigatório")
    @Pattern(regexp = "\\d{7,20}", message = "O documento deve conter apenas números e ter entre 7 e 20 dígitos")
    @Column(name = "document_number", nullable = false, length = 20)
    private String documentNumber;

    @NotNull(message = "A data de nascimento é obrigatória")
    @PastOrPresent(message = "A data de nascimento não pode ser maior que a data atual")
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @NotBlank(message = "O telefone é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "O telefone deve conter exatamente 11 dígitos")
    @Column(name = "phone_number", nullable = false, length = 11)
    private String phoneNumber;

    public Customer updateFromDto(CustomerDto dto) {
    this.setName(dto.name());
    this.setDocumentNumber(dto.documentNumber());
    this.setBirthDate(dto.birthDate());
    this.setPhoneNumber(dto.phoneNumber());
    return this;
}
}
