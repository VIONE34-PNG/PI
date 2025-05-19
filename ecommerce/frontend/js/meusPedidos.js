document.addEventListener("DOMContentLoaded", () => {
    fetch("http://127.0.0.1:8080/api/pedidos/user", {
        credentials: "include" 
    })
    .then(res => {
        if (!res.ok) {
            throw new Error("Falha ao carregar pedidos");
        }
        return res.json();
    })
    .then(pedidos => {
        const tbody = document.querySelector("#pedidos-table tbody");
        tbody.innerHTML = "";

        pedidos.forEach(pedido => {
            const tr = document.createElement("tr");

            const data = new Date(pedido.dataHora);
            const dataFormatada = data.toLocaleDateString("pt-BR");

            tr.innerHTML = `
                <td>${pedido.numeroPedido || "---"}</td>
                <td>${dataFormatada}</td>
                <td>${pedido.totalGeral.toLocaleString("pt-BR", { style: "currency", currency: "BRL" })}</td>
                <td>${pedido.status}</td>
                <td><button data-id="${pedido.id}">Mais detalhes</button></td>
            `;

            tbody.appendChild(tr);
        });

        tbody.querySelectorAll("button").forEach(btn => {
            btn.addEventListener("click", e => {
                const idPedido = e.target.getAttribute("data-id");
                window.location.href = `/ecommerce/frontend/detalhesPedido.html?orderId=${idPedido}`;
            });
        });
    })
    .catch(error => {
        alert(error.message);
    });
});