package com.donwaztok.apicompras.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.donwaztok.apicompras.entity.Produto;
import com.donwaztok.apicompras.repository.ProdutoRepository;

@RestController
public class ProdutoController {

	@Autowired
	private ProdutoRepository _produtoRepository;

	@PostMapping(value = "/produto")
	@ResponseStatus(HttpStatus.CREATED)
	public Produto create(@RequestBody Produto produto) {
		return _produtoRepository.save(produto);
	}

	@GetMapping(value = "/produtos")
	public List<Produto> findAll() {
		return _produtoRepository.findAll();
	}

	@GetMapping(value = "/produto/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Produto> GetById(@PathVariable(value = "id") long id) {
		Optional<Produto> produto = _produtoRepository.findById(id);
		if (produto.isPresent())
			return new ResponseEntity<Produto>(produto.get(), HttpStatus.OK);
		else
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@DeleteMapping(value = "/produto/{id}")
	public ResponseEntity<Object> Delete(@PathVariable(value = "id") long id) {
		Optional<Produto> pessoa = _produtoRepository.findById(id);
		if (pessoa.isPresent()) {
			_produtoRepository.delete(pessoa.get());
			return new ResponseEntity<>(HttpStatus.OK);
		} else
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
}
