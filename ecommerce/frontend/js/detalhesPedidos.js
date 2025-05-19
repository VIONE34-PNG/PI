document.addEventListener("DOMContentLoaded", () => {
    const urlParams = new URLSearchParams(window.location.search);
    const orderId = urlParams.get("orderId");

    if (!orderId) {
        alert("ID do pedido não informado.");
        return;
    }

    fetch(`http://127.0.0.1:8080/api/pedidos/${orderId}`, {
        credentials: "include"
    })
    .then(response => {
        if (!response.ok) {
            if (response.status === 401) {
                alert("Você precisa estar logado para ver esse pedido.");
                window.location.href = "/ecommerce/frontend/login.html";
            } else if (response.status === 403) {
                alert("Você não tem permissão para acessar este pedido.");
                window.location.href = "/ecommerce/frontend/pedidos.html";
            } else {
                throw new Error("Erro ao buscar os detalhes do pedido.");
            }
        }
        return response.json();
    })
    .then(pedido => {
        document.getElementById("numero-pedido").textContent = pedido.numeroPedido;
        document.getElementById("data-pedido").textContent = new Date(pedido.dataHora).toLocaleDateString("pt-BR");
        document.getElementById("status-pedido").textContent = pedido.status;

        const tbody = document.getElementById("order-items");
        tbody.innerHTML = "";

        pedido.itens.forEach(item => {
            const tr = document.createElement("tr");

            const precoUnitario = item.precoUnitario?.toLocaleString("pt-BR", {
            style: "currency",
            currency: "BRL"
           });
            const totalItem = (item.precoUnitario * item.quantidade).toLocaleString("pt-BR", {
            style: "currency",
            currency: "BRL"
           });

            tr.innerHTML = `
                <td>${item.produto.nome}</td>
                <td>${item.quantidade}</td>
                <td>${precoUnitario}</td>
                <td>${totalItem}</td>
            `;
            tbody.appendChild(tr);
        });

        document.getElementById("shipping-cost").textContent = pedido.frete.toLocaleString("pt-BR", {
        style: "currency",
        currency: "BRL"
        });
        
        document.getElementById("total-value").textContent = pedido.totalGeral.toLocaleString("pt-BR", {
        style: "currency",
        currency: "BRL"
        });

        document.getElementById("delivery-address").textContent = pedido.enderecoEntrega;

        document.getElementById("payment-method").textContent = pedido.formaPagamento;
    })
    .catch(error => {
        console.error("Erro ao carregar detalhes do pedido:", error);
        alert("Erro ao carregar os detalhes do pedido.");
    });
});
