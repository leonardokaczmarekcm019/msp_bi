package com.example.msp_bi.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.msp_bi.model.ServicoRede;

public interface ServicoRedeRepository extends JpaRepository<ServicoRede, Long> {
    Optional<ServicoRede> findByNome(String nome);
}
