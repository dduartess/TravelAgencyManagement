package _dduartess.travelaencymanagement.entities.customers;

import java.time.LocalDate;

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
@Table(name = "customers")
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
    @Pattern(regexp = "^[a-zA-Z\\u00C0-\\u00FF\\s]+$", message = "O nome não pode conter números ou caracteres especiais")
    private String name;

    @NotBlank(message = "O documento é obrigatório")
    @Pattern(regexp = "\\d{7,20}", message = "O documento deve conter apenas números e ter entre 7 e 20 dígitos")
    private String documentNumber;

    @NotNull(message = "A data de nascimento é obrigatória")
    @PastOrPresent(message = "A data de nascimento não pode ser maior que a data atual")
    private LocalDate birthDate;

    @NotBlank(message = "O telefone é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "O telefone deve conter exatamente 11 dígitos")
    private String phoneNumber;
}
