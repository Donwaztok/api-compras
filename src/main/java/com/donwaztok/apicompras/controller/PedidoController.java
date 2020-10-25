package com.donwaztok.apicompras.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.donwaztok.apicompras.entity.Pedido;
import com.donwaztok.apicompras.repository.PedidoRepository;

@RestController
class PedidoController {

	@Autowired
	private PedidoRepository _pedidoRepository;

	@PostMapping(value = "/pedido")
	@ResponseStatus(HttpStatus.CREATED)
	public Pedido create(@RequestBody Pedido pedido) {
		return _pedidoRepository.save(pedido);
	}

	@GetMapping(value = "/pedidos")
	public List<Pedido> findAll() {
		return _pedidoRepository.findAll();
	}

	@GetMapping(value = "/pedido/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Pedido> GetById(@PathVariable(value = "id") long id) {
		Optional<Pedido> pedido = _pedidoRepository.findById(id);
		if (pedido.isPresent())
			return new ResponseEntity<Pedido>(pedido.get(), HttpStatus.OK);
		else
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

//	POST /pedido para, criar um novo pedido ***
//	GET /pedidos para, listar todos os pedidos
//	GET /pedido/<id> para listar um pedido específico
//	GET /mais_vendidos para listar os produtos mais vendidos em ordem decrescente
}