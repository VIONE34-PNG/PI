document.addEventListener("DOMContentLoaded", () => {
    const resumoPedido = JSON.parse(sessionStorage.getItem("resumoPedido"));

    if (!resumoPedido) {
        alert("Nenhum pedido encontrado. Redirecionando para a escolha de pagamento.");
        window.location.href = "/ecommerce/frontend/checkout.html";
        return;
    }

    document.getElementById("numero-pedido").textContent = resumoPedido.numeroPedido || "---";
    document.getElementById("data-pedido").textContent = resumoPedido.dataPedido || new Date().toLocaleDateString();
    document.getElementById("status-pedido").textContent = resumoPedido.status || "Pendente";

    const orderItems = document.getElementById("order-items");
    orderItems.innerHTML = resumoPedido.itens.map(item => `
        <tr>
            <td>${item.produto.nome}</td>
            <td>${item.quantidade}</td>
            <td>R$ ${item.precoUnitario.toLocaleString("pt-BR", { style: "currency", currency: "BRL" })}</td>
            <td>R$ ${(item.quantidade * item.precoUnitario).toLocaleString("pt-BR", { style: "currency", currency: "BRL" })}</td>
        </tr>
    `).join("");

    
    document.getElementById("total-value").textContent = `R$ ${resumoPedido.totalGeral.toFixed(2)}`;
    document.getElementById("shipping-cost").textContent = `R$ ${resumoPedido.frete || 0.00}`;

    document.getElementById("delivery-address").textContent = resumoPedido.enderecoEntrega;

    document.getElementById("payment-method").textContent = resumoPedido.formaPagamento;

    document.getElementById("finalizar-pedido").addEventListener("click", () => {
        alert("Compra concluída com sucesso!");
        sessionStorage.removeItem("resumoPedido");
        window.location.href = "/ecommerce/frontend/index.html";
    });
});
