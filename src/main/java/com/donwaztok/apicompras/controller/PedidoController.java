package com.donwaztok.apicompras.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
import com.donwaztok.apicompras.entity.PedidoItem;
import com.donwaztok.apicompras.entity.Produto;
import com.donwaztok.apicompras.model.ErrorMessage;
import com.donwaztok.apicompras.model.VendasProduto;
import com.donwaztok.apicompras.repository.PedidoItemRepository;
import com.donwaztok.apicompras.repository.PedidoRepository;
import com.donwaztok.apicompras.repository.ProdutoRepository;

@RestController
class PedidoController {

	@Autowired
	private PedidoRepository _pedidoRepository;

	@Autowired
	private PedidoItemRepository _pedidoItemRepository;

	@Autowired
	private ProdutoRepository _produtoRepository;

	@PostMapping(value = "/pedido")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> create(@RequestBody Pedido $pedido) {
		BigDecimal preco = new BigDecimal(0);

		if ($pedido.getPedido_item().size() == 0)
			return getErrorBody(HttpStatus.INTERNAL_SERVER_ERROR, "Pedido precisa conter pelo menos um produto");
		{
			List<PedidoItem> pedidoitem = $pedido.getPedido_item();
			$pedido.setPedido_item(null);
			_pedidoRepository.save($pedido);
			$pedido.setPedido_item(pedidoitem);
		}

		for (PedidoItem pedidoitem : $pedido.getPedido_item()) {
			Optional<Produto> prodOpt = _produtoRepository.findById(pedidoitem.getProduto().getId());
			if (prodOpt.isPresent()) {
				Produto produto = prodOpt.get();
				if (pedidoitem.getQuantidade() == 0) {
					return getErrorBody(HttpStatus.NOT_FOUND, "Pedido precisa conter pelo menos 1 item do produto");
				}

				if (produto.getQuantidade() >= pedidoitem.getQuantidade()) {
					produto.setQuantidade(produto.getQuantidade() - pedidoitem.getQuantidade());
					pedidoitem.setProduto(prodOpt.get());
					pedidoitem.setTotal(produto.getPreco().multiply(new BigDecimal(pedidoitem.getQuantidade())));
					pedidoitem.setPedido($pedido);
					preco = preco.add(pedidoitem.getTotal());
					_produtoRepository.save(produto);
					_pedidoItemRepository.save(pedidoitem);
				} else {
					return getErrorBody(HttpStatus.NOT_FOUND, "Produto não contém estoque");
				}
			} else {
				return getErrorBody(HttpStatus.NOT_FOUND, "Produto não encontrado");
			}
		}
		$pedido.setTotal(preco);
		_pedidoRepository.save($pedido);
		return new ResponseEntity<Pedido>($pedido, HttpStatus.OK);
	}

	@GetMapping(value = "/pedidos")
	public List<Pedido> findAll() {
		return _pedidoRepository.findAll();
	}

	@GetMapping(value = "/pedido/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> GetById(@PathVariable(value = "id") long $id) {
		Optional<Pedido> pedido = _pedidoRepository.findById($id);
		if (pedido.isPresent())
			return new ResponseEntity<Pedido>(pedido.get(), HttpStatus.OK);
		else
			return getErrorBody(HttpStatus.NOT_FOUND, "Pedido não encontrado");
	}

	@GetMapping(value = "/mais_vendidos")
	public ResponseEntity<?> maisVendidos() {
		HashMap<Produto, Integer> maisvendidos = new HashMap<Produto, Integer>();

		try {
			for (Pedido pedido : _pedidoRepository.findAll()) {
				for (PedidoItem pedidoItem : pedido.getPedido_item()) {

					Integer count = maisvendidos.containsKey(pedidoItem.getProduto())
							? maisvendidos.get(pedidoItem.getProduto())
							: 0;
					maisvendidos.put(pedidoItem.getProduto(), count + pedidoItem.getQuantidade());
				}
			}

			maisvendidos = maisvendidos.entrySet().stream()
					.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> {
						throw new AssertionError();
					}, LinkedHashMap::new));

			List<VendasProduto> vendas = new ArrayList<>();
			for (Produto produto : maisvendidos.keySet()) {
				VendasProduto vendasProduto = new VendasProduto();
				vendasProduto.setProduto(produto);
				vendasProduto.setQuantidade(maisvendidos.get(produto));
				vendas.add(vendasProduto);
			}
			return new ResponseEntity<List<VendasProduto>>(vendas, HttpStatus.OK);
		} catch (Exception e) {
			return getErrorBody(HttpStatus.NOT_FOUND, e.getLocalizedMessage());
		}

	}

	private ResponseEntity<?> getErrorBody(HttpStatus $status, String $msg) {
		ErrorMessage error = new ErrorMessage();
		error.setError($status.toString());
		error.setMessage($msg);
		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);

	}
}