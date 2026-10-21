package com.example.msp_bi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.msp_bi.model.Integracao;

public interface IntegracaoRepository extends JpaRepository<Integracao, Long> {

    List<Integracao> findByFonteDadosId(Long fonteDadosId);

    List<Integracao> findByFonteDadosIdAndAtivoTrue(Long fonteDadosId);

    Optional<Integracao> findByWebhookToken(String webhookToken);
}
