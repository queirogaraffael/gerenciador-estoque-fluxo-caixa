package com.gerenciadorDeEstoqueEFluxoDeCaixa.controllers;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

import com.gerenciadorDeEstoqueEFluxoDeCaixa.constantes.ConstantesMenuFluxoCaixa;
import com.gerenciadorDeEstoqueEFluxoDeCaixa.entities.Produto;
import com.gerenciadorDeEstoqueEFluxoDeCaixa.entities.Venda;
import com.gerenciadorDeEstoqueEFluxoDeCaixa.services.FluxoDeCaixaService;
import com.gerenciadorDeEstoqueEFluxoDeCaixa.services.ComumProdutoVendaService;
import com.gerenciadorDeEstoqueEFluxoDeCaixa.services.ProdutoService;
import com.gerenciadorDeEstoqueEFluxoDeCaixa.utils.AutenticadorDeSenha;
import com.gerenciadorDeEstoqueEFluxoDeCaixa.views.FluxoDeCaixaView;

public class CaixaController {

	public void fluxoDeCaixa(Set<Produto> produtos, Set<Venda> vendas, Set<Integer> codigosVendas,
			Boolean statusNotaFiscal, String caminhoNotaFiscal) {
		int opcaoMenuFluxoDeCaixa = 0;
		Set<Produto> listaCompras = new HashSet<>();

		do {

			opcaoMenuFluxoDeCaixa = Integer
					.parseInt(JOptionPane.showInputDialog(FluxoDeCaixaView.exibirMenuFluxoDeCaixa()));

			switch (opcaoMenuFluxoDeCaixa) {

			case (ConstantesMenuFluxoCaixa.ADICIONAR):
				adicionaProduto(listaCompras, produtos);
				break;

			case (ConstantesMenuFluxoCaixa.LISTAR_SACOLA):
				listarSacola(listaCompras);

				break;

			case (ConstantesMenuFluxoCaixa.LISTAR_ESTOQUE):
				listarEstoque(produtos);
				break;

			case (ConstantesMenuFluxoCaixa.REMOVER_PRODUTO):

				removerProduto(listaCompras, produtos);

				break;

			case (ConstantesMenuFluxoCaixa.MODIFICAR_QUANTIDADE):

				modificarQuantidade(listaCompras, produtos);

				break;

			case (ConstantesMenuFluxoCaixa.FINALIZAR_COMPRA):

				finalizarCompra(listaCompras, codigosVendas, vendas, statusNotaFiscal, caminhoNotaFiscal);
				break;

			case (ConstantesMenuFluxoCaixa.LIMPAR):

				limparCarrinho(listaCompras, produtos);
				break;

			case (ConstantesMenuFluxoCaixa.SAIR):
				sair(opcaoMenuFluxoDeCaixa);

				break;
			default:
				JOptionPane.showMessageDialog(null, "Opção inválida. Tente novamente!");
				break;

			}
		} while (opcaoMenuFluxoDeCaixa != ConstantesMenuFluxoCaixa.SAIR);
	}

	private void adicionaProduto(Set<Produto> listaCompras, Set<Produto> produtos) {
		Integer codigoProduto = Integer.parseInt(
				JOptionPane.showInputDialog("Digite o código do produto que você deseja adicionar a lista de compras"));

		if (ComumProdutoVendaService.jaContem(listaCompras, codigoProduto)) {
			JOptionPane.showMessageDialog(null,
					"Se você deseja modificar a quantidade desse produto, vá na opção 5. Modificar quantidade de um produto");
		} else {
			Produto p = ComumProdutoVendaService.retornaPeloCodigo(produtos, codigoProduto);
			if (p != null) {
				Integer quantidade = Integer.parseInt(JOptionPane.showInputDialog("Quantidade: "));

				if (p.getQuantidade() >= quantidade) {
					listaCompras.add(new Produto(p.getCodigo(), p.getNome(), p.getValor(), quantidade));
					p.setQuantidade(p.getQuantidade() - quantidade);

				} else if (p.getQuantidade() == 0) {
					JOptionPane.showMessageDialog(null, "Quantidade em estoque do produto igual a 0. Tente outro!");
				}

				else {
					int escolha = Integer.parseInt(JOptionPane.showInputDialog(
							"Quantidade desejada menor do que em estoque. Deseja adicionar todos os itens restantes ?\n1. Sim\n2. Não"));

					if (escolha == 1) {
						listaCompras.add(new Produto(p.getCodigo(), p.getNome(), p.getValor(), p.getQuantidade()));
						p.setQuantidade(0);
					} else {
						JOptionPane.showMessageDialog(null, "Compra de produto cancelada.");
					}
				}
			} else {
				JOptionPane.showMessageDialog(null, "Produto não consta no estoque. Tente outro!");

			}
		}
	}

	private void listarSacola(Set<Produto> listaCompras) {
		String lista = ComumProdutoVendaService.imprimir(listaCompras);
		String subtotal = String.format("%.2f", String.valueOf(ProdutoService.somaValores(listaCompras)));
		String resultado = lista + "\n" + subtotal;

		JOptionPane.showMessageDialog(null, resultado);
	}

	private void listarEstoque(Set<Produto> produtos) {

		String Estoque = ComumProdutoVendaService.imprimir(produtos);
		JOptionPane.showMessageDialog(null, Estoque);
	}

	private void removerProduto(Set<Produto> listaCompras, Set<Produto> produtos) {
		if (listaCompras.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Carrinho de compras vazio.");
		} else {
			Integer codigoProdutoParaRemover = Integer
					.parseInt(JOptionPane.showInputDialog("Digite o código do produto que você seja remover:"));

			if (listaCompras.isEmpty()) {

				JOptionPane.showMessageDialog(null, "Adicione primeiro um produto a sua sacola para poder remover.");
			} else if (!ComumProdutoVendaService.jaContem(listaCompras, codigoProdutoParaRemover)) {
				JOptionPane.showMessageDialog(null,
						"Produto já não constava no gerenciador de estoque. Tente novamente com uma produto existente.");

			} else {

				int quantidade = ProdutoService.quantidadeRealProduto(listaCompras, produtos, codigoProdutoParaRemover);
				Produto prodrem = ComumProdutoVendaService.retornaPeloCodigo(produtos, codigoProdutoParaRemover);
				prodrem.setQuantidade(quantidade);

				ProdutoService.removeProduto(listaCompras, codigoProdutoParaRemover);
				JOptionPane.showMessageDialog(null, "Produto removida com sucesso!");
			}
		}
	}

	private void modificarQuantidade(Set<Produto> listaCompras, Set<Produto> produtos) {
		if (listaCompras.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Carrinho de compras vazio.");
		} else {
			Integer codigo = Integer.parseInt(JOptionPane.showInputDialog("Digite o código do produto: "));

			if (ComumProdutoVendaService.jaContem(listaCompras, codigo)) {
				Integer novaQuantidade = Integer
						.parseInt(JOptionPane.showInputDialog("Digite a nova quantidade do produto: "));

				Produto prod = ComumProdutoVendaService.retornaPeloCodigo(listaCompras, codigo);
				Produto produtoEstoque = ComumProdutoVendaService.retornaPeloCodigo(produtos, codigo);
				int quantidadeRealProduto = prod.getQuantidade() + produtoEstoque.getQuantidade();

				if (quantidadeRealProduto >= novaQuantidade) {
					Produto produtoModificar = ComumProdutoVendaService.retornaPeloCodigo(listaCompras, codigo);
					ProdutoService.editaProduto(produtoModificar, novaQuantidade);
					produtoEstoque.setQuantidade(quantidadeRealProduto - novaQuantidade);

				} else if (quantidadeRealProduto <= 0) {
					JOptionPane.showMessageDialog(null, "Produto indisponivel. Tente outro!");
				}

				else {
					int escolha = Integer.parseInt(JOptionPane.showInputDialog(
							"Quantidade desejada menor do que em estoque. Deseja adicionar todos os itens restantes ?\n1. Sim\n2. Não"));

					if (escolha == 1) {
						Produto produtoModificar = ComumProdutoVendaService.retornaPeloCodigo(listaCompras, codigo);
						ProdutoService.editaProduto(produtoModificar, quantidadeRealProduto);
						produtoEstoque.setQuantidade(0);
					} else {
						JOptionPane.showMessageDialog(null, "Compra de produto cancelada.");
					}
				}

				JOptionPane.showMessageDialog(null, "Quantidade modificada com sucesso!");
			} else {
				JOptionPane.showMessageDialog(null, "Produto inválido, tente outro!");
			}
		}
	}

	private void finalizarCompra(Set<Produto> listaCompras, Set<Integer> codigosVendas, Set<Venda> vendas,
			Boolean statusNotaFiscal, String caminhoNotaFiscal) {
		if (!listaCompras.isEmpty()) {
			Integer codigoVenda = FluxoDeCaixaService.geradorDeCodigo(codigosVendas);

			Venda novaVenda = new Venda(codigoVenda, Instant.now());
			vendas.add(novaVenda);

			for (Produto p1 : listaCompras) {
				novaVenda.getProdutos().add(p1);
			}

			if (statusNotaFiscal) {
				FluxoDeCaixaService.geradorNotaFiscal(novaVenda, caminhoNotaFiscal);
			}

			listaCompras.clear();
			JOptionPane.showMessageDialog(null, "Obrigado, volte sempre!");
		}
	}

	private void limparCarrinho(Set<Produto> listaCompras, Set<Produto> produtos) {
		if (!listaCompras.isEmpty()) {
			for (Produto compra : listaCompras) {

				int quantidade = ProdutoService.quantidadeRealProduto(listaCompras, produtos, compra.getCodigo());

				Produto prodrem = ComumProdutoVendaService.retornaPeloCodigo(produtos, compra.getCodigo());

				prodrem.setQuantidade(quantidade);

				ProdutoService.removeProduto(listaCompras, compra.getCodigo());
				JOptionPane.showMessageDialog(null, "Sacola de compras limpada com sucesso.");
			}
		}
	}

	private void sair(int opcaoMenuFluxoDeCaixa) {
		String senhaDigitada = JOptionPane.showInputDialog(null, "Digite a senha: ");
		boolean autenticacao = AutenticadorDeSenha.autenticacaoSenha(senhaDigitada);

		if (!autenticacao) {
			JOptionPane.showMessageDialog(null, "Senha incorreta. Tente novamente!");
			opcaoMenuFluxoDeCaixa = 0;
		}
	}
}