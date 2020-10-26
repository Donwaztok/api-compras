package com.donwaztok.apicompras.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.donwaztok.apicompras.entity.Produto;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}