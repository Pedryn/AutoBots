package com.autobots.automanager.entitades;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(exclude = { "cliente", "funcionario", "veiculo" })
@Entity
@JsonIgnoreProperties
public class Venda extends RepresentationModel<Venda> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Date cadastro;

    @Column(nullable = false, unique = true)
    private String identificacao;

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    @JsonBackReference
    private Usuario cliente;

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    @JsonBackReference
    private Usuario funcionario;

    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    @JsonManagedReference
    private Set<Mercadoria> mercadorias = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    @JsonManagedReference
    private Set<Servico> servicos = new HashSet<>();

    @OneToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE })
    @JsonBackReference
    private Veiculo veiculo;

    // Validação Personalizada inline
    public static class ServicoUnicoValidator implements ConstraintValidator<ServicoUnico, Venda> {

        @Override
        public boolean isValid(Venda venda, ConstraintValidatorContext context) {
            // Verifica se a venda já está associada a outro serviço com o mesmo id
            for (Servico servico : venda.getServicos()) {
                if (servico.getId() == 1) { // Substitua 1 pelo id do serviço que você quer validar
                    return false; 
                }
            }
            return true;
        }
    }

    // Anotação de Validação inline
    @Target({ ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = ServicoUnicoValidator.class)
    public @interface ServicoUnico {
        String message() default "Serviço já está associado a outra venda.";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }
}