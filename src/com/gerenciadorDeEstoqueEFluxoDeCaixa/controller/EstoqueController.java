package com.gerenciadorDeEstoqueEFluxoDeCaixa.controller;

import java.util.Set;

import javax.swing.JOptionPane;

import com.gerenciadorDeEstoqueEFluxoDeCaixa.entities.Produto;
import com.gerenciadorDeEstoqueEFluxoDeCaixa.entities.Venda;
import com.gerenciadorDeEstoqueEFluxoDeCaixa.services.CaixaService;
import com.gerenciadorDeEstoqueEFluxoDeCaixa.services.ProdutoService;
import com.gerenciadorDeEstoqueEFluxoDeCaixa.view.MenusView;

public class EstoqueController {


	public void gerenciarEstoque(Set<Produto> produtos, Set<Venda> vendas) {

		int opcao = 0;
		Produto produto;
		opcao = Integer.parseInt(JOptionPane.showInputDialog(MenusView.exibirMenuEstoque()));

		do {
			switch (opcao) {
			// cadastro de produto
			case 1:

				int opcaoAdicionar = Integer
						.parseInt(JOptionPane.showInputDialog("1. Adicionar manualmente\n2. Adicionar por arquivo"));

				if (opcaoAdicionar == 1) {
					JOptionPane.showMessageDialog(null, "Cadastro de produto");
					String codigo = JOptionPane.showInputDialog("Código do produto: ");
					String nome = JOptionPane.showInputDialog("Digite o nome do produto: ");
					Double valor = Double.parseDouble(JOptionPane.showInputDialog("Valor do produto: "));
					Integer quantidade = Integer.parseInt(JOptionPane.showInputDialog("Quantidade do produto: "));

					produto = new Produto(codigo, nome, valor, quantidade);

					if (ProdutoService.jaContemProduto(produtos, codigo)) {
						JOptionPane.showMessageDialog(null,
								"O produto já foi cadastrado anteriormente. Por favor, insira um novo produto!");
					} else {
						ProdutoService.adicionarProduto(produtos, produto);
						JOptionPane.showMessageDialog(null, "Tarefa adicionada com sucesso!");
					}
				} else {
					String caminho = JOptionPane.showInputDialog("Digite o caminho do arquivo:");

					ProdutoService.adicionaProdutoPorArquivo(caminho, produtos);
				}
				// edição de produto(s)
			case 2:

				JOptionPane.showMessageDialog(null, "Edição de dados do produto");

				int opcaoEditar = Integer.parseInt(JOptionPane
						.showInputDialog("1. Modificar o nome, o valor e a quantidade.\n2. Apenas a quantidade."));

				String codigo = JOptionPane.showInputDialog("Digite o código do produto: ");

				if (opcaoEditar == 1) {

					String novoNome = JOptionPane.showInputDialog("Digite o novo nome do produto: ");
					Double novoValor = Double
							.parseDouble(JOptionPane.showInputDialog("Digite o novo valor do produto: "));
					Integer novaQuantidade = Integer
							.parseInt(JOptionPane.showInputDialog("Digite a nova quantidade do produto: "));

					if (ProdutoService.jaContemProduto(produtos, codigo)) {
						System.out.println("Produto inválido");
					} else {
						ProdutoService.editarProdutoCompleto(produtos, codigo, novoNome, novoValor, novaQuantidade);
						JOptionPane.showMessageDialog(null, "Produto modificado com sucesso!");
					}

				} else if (opcaoEditar == 2) {
					// esta modificando de arquivos inexistente
					Integer novaQuatidade = Integer
							.parseInt(JOptionPane.showInputDialog("Digite a nova quantidade do produto: "));

					// implementar

					ProdutoService.editarProdutoQuantidade(produtos, codigo, novaQuatidade);
					JOptionPane.showMessageDialog(null, "Quantidade modificada com sucesso!");

				} else {

					JOptionPane.showMessageDialog(null, "Opção inválida. Tente Novamente!");
				}
				// Visualizar produto(s)
			case 3:
				ProdutoService.visualizarProduto(produtos);
				// Remoção de produto(s)
			case 4:
				String produtoParaRemover = JOptionPane
						.showInputDialog("Digite o código do produto que você seja remover:");

				if (produtos.isEmpty()) {

					JOptionPane.showMessageDialog(null, "Adicione primeiro um produto para poder remover.");
				} else if (!ProdutoService.jaContemProduto(produtos, produtoParaRemover)) {
					JOptionPane.showMessageDialog(null,
							"Produto já não constava no gerenciador de estoque. Tente novamente com uma produto existente.");

				} else {
					ProdutoService.removerProduto(produtos, produtoParaRemover.toLowerCase());
					JOptionPane.showMessageDialog(null, "Produto removida com sucesso!");
				}

			case (5):

				/// implementar codigo de imprimir

				if (!vendas.isEmpty()) {
					System.out.println("Todas as vendas do estabelecimento");
					for (Venda v : vendas) {
						System.out.println(v);
					}
				} else {
					JOptionPane.showMessageDialog(null, "Sem venda registrada.");
				}

			case 6:

				if (!vendas.isEmpty()) {

					String codigo1 = JOptionPane.showInputDialog("Codigo de venda: ");
					Venda venda = CaixaService.retornaVendaPeloCodigo(vendas, codigo1);
					JOptionPane.showMessageDialog(null, venda);
				} else {
					JOptionPane.showMessageDialog(null, "Sem venda registrada ainda.");
				}

				// Opção voltar
			case 7:
				break;

			default:
				JOptionPane.showMessageDialog(null, "Opção inválida. Tente outra!");
			}
		} while (opcao != 7);
	}

}
