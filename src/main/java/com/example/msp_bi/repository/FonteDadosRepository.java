package com.example.msp_bi.repository;

import com.example.msp_bi.model.FonteDados;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FonteDadosRepository extends JpaRepository<FonteDados, Long> {

    List<FonteDados> findByAtivoTrue();

}
