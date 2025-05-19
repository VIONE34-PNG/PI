document.addEventListener('DOMContentLoaded', () => {
    const listaCarrinho = document.getElementById('lista-carrinho');
    const botaoLimpar = document.getElementById('limpar-carrinho');
    const resumoCarrinho = document.getElementById('resumo-carrinho');
    const inputCEP = document.getElementById('cep');
    const opcoesFrete = document.getElementById('frete-opcoes');
    const finalizarCompra = document.getElementById('finalizar-compra');

    let valorFreteSelecionado = 0;

    const atualizarCarrinho = async () => {
        try {
            const response = await fetch('http://127.0.0.1:8080/api/carrinho/listar', {
                method: 'GET',
                credentials: 'include',
                headers: { 'Content-Type': 'application/json' }
            });

            if (!response.ok) {
                throw new Error(`Erro ${response.status}: ${response.statusText}`);
            }

            const itensCarrinho = await response.json();
            console.log("Itens recebidos:", itensCarrinho);

            sessionStorage.setItem("carrinhoTemporario", JSON.stringify(itensCarrinho));

            listaCarrinho.innerHTML = '';
            resumoCarrinho.innerHTML = '';
            opcoesFrete.innerHTML = '';

            if (!itensCarrinho || itensCarrinho.length === 0) {
                listaCarrinho.innerHTML = '<p>O carrinho está vazio.</p>';
                return;
            }

            let valorTotal = 0;
            let quantidadeTotal = 0;

            itensCarrinho.forEach((item) => {
                if (!item.id || item.id === 0) {
                    console.error("Erro: Produto com ID inválido encontrado!", item);
                    return; 
                }

                const precoUnitario = parseFloat(item.precoUnitario || 0);
                const quantidade = parseInt(item.quantidade || 0);
                const totalItem = precoUnitario * quantidade;

                valorTotal += totalItem;
                quantidadeTotal += quantidade;

                const card = document.createElement('div');
                card.classList.add('card-produto');
                card.innerHTML = `
                    <img src="${item.produto.imagemPrincipal || '/ecommerce/frontend/assets/default.png'}" alt="Produto">
                    <div class="info">
                        <h3>${item.produto.nome}</h3>
                        <p>
                            <button class="quantidade-btn" data-id="${item.id}" data-op="sub" data-qtd="${quantidade}">−</button>
                            <span class="quantidade-text">${quantidade}</span>
                            <button class="quantidade-btn" data-id="${item.id}" data-op="add" data-qtd="${quantidade}">+</button>
                        </p>
                        <p><span style="color: green;">Em estoque e pronto para envio.</span></p>
                    </div>
                    <div class="preco">
                        R$ ${totalItem.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
                    </div>
                `;
                listaCarrinho.appendChild(card);
            });

            if (inputCEP.value) {
                const fretes = gerarFretesAleatorios();
                fretes.forEach((valor, index) => {
                    const label = document.createElement('label');
                    label.innerHTML = `
                        <input type="radio" name="frete" value="${valor}" ${index === 0 ? 'checked' : ''}>
                        Frete opção ${index + 1}: R$ ${valor.toFixed(2)}
                    `;
                    opcoesFrete.appendChild(label);
                    if (index === 0){
                        valorFreteSelecionado = valor;
                        sessionStorage.setItem("freteSelecionado", valor);
                    } 
                });

                document.querySelectorAll('input[name="frete"]').forEach(radio => {
                    radio.addEventListener('change', () => {
                        valorFreteSelecionado = parseFloat(radio.value);
                        sessionStorage.setItem("freteSelecionado", valorFreteSelecionado);
                        exibirResumo(valorTotal);
                    });
                });
            }

            exibirResumo(valorTotal);

            document.querySelectorAll('.quantidade-btn').forEach(btn => {
                btn.addEventListener('click', async () => {
                    const id = btn.getAttribute('data-id');
                    const op = btn.getAttribute('data-op');
                    let novaQtd = parseInt(btn.getAttribute('data-qtd'));
            
                    console.log("Tentando atualizar/remover item. ID:", id, "Qtd atual:", novaQtd);
            
                    if (!id || id === "0") {
                        console.error("Erro: ID inválido ao tentar atualizar/remover.");
                        return;
                    }
            
                    if (op === 'add') {
                        novaQtd++;
                    } else if (op === 'sub' && novaQtd > 1) {
                        novaQtd--;
                    } else if (op === 'sub' && novaQtd <= 0) {
                        console.log(`Removendo item ID: ${id}`);
                        const response = await fetch(`http://127.0.0.1:8080/api/carrinho/${id}`, { method: 'DELETE', credentials: 'include' });
            
                        if (!response.ok) {
                            console.error("Erro ao remover item.");
                        }
            
                        return atualizarCarrinho();
                    }
            
                    const response = await fetch(`http://127.0.0.1:8080/api/carrinho/atualizar/${id}`, {
                        method: "PUT",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify({ quantidade: novaQtd }),
                        credentials: "include"
                    });
            
                    if (response.ok) {
                        atualizarCarrinho();
                    } else {
                        console.error("Falha ao atualizar quantidade.");
                    }
                });
            });
        } catch (error) {
            console.error("Erro ao atualizar o carrinho:", error);
            listaCarrinho.innerHTML = '<p>Erro ao carregar o carrinho.</p>';
        }
    };

    const exibirResumo = (subtotal) => {
        resumoCarrinho.innerHTML = `
            <p>Subtotal: R$ ${subtotal.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}</p>
            <p>Frete: R$ ${valorFreteSelecionado.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}</p>
            <hr>
            <p class="total">Total: R$ ${(subtotal + valorFreteSelecionado).toLocaleString('pt-BR', { minimumFractionDigits: 2 })}</p>
        `;
    };

    const gerarFretesAleatorios = () => {
        return [
            Math.floor(Math.random() * 10) + 5,
            Math.floor(Math.random() * 10) + 15,
            Math.floor(Math.random() * 10) + 25
        ];
    };

    inputCEP.addEventListener('change', atualizarCarrinho);

    botaoLimpar.addEventListener('click', async () => {
        try {
            const response = await fetch('http://127.0.0.1:8080/api/carrinho/limpar', {
                method: 'DELETE',
                credentials: 'include' 
            });
    
            if (!response.ok) throw new Error("Erro ao limpar o carrinho.");
            atualizarCarrinho();
        } catch (error) {
            console.error("Erro ao limpar o carrinho:", error);
            listaCarrinho.innerHTML = '<p>Erro ao limpar o carrinho.</p>';
        }
    });

    finalizarCompra.addEventListener('click', () => {
        const usuarioLogado = localStorage.getItem("usuarioLogado");
        const freteSelecionado = document.querySelector('input[name="frete"]:checked');

        if (!freteSelecionado) {
        alert("Por favor, selecione uma opção de frete antes de finalizar a compra.");
        return; 
    }
        window.location.href = '/ecommerce/frontend/checkout.html';
        
    });

    atualizarCarrinho();
});

