package com.donwaztok.apicompras.model;

import com.donwaztok.apicompras.entity.Produto;

public class VendasProduto {

	private int quantidade;
	private Produto produto;

	public int getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}
}
