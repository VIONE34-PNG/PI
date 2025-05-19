const statusOptions = [
    "AGUARDANDO_PAGAMENTO",
    "PAGAMENTO_REJEITADO",
    "PAGAMENTO_COM_SUCESSO",
    "AGUARDANDO_RETIRADA",
    "EM_TRANSITO",
    "ENTREGUE",
    "CANCELADO"
];

const verificarPermissaoEstoquista = async () => {
    try {
        const response = await fetch("http://127.0.0.1:8080/api/usuarios/sessao", {
            credentials: "include",
        });

        if (!response.ok) throw new Error("Sessão não encontrada.");

        const usuario = await response.json();

        if (!usuario || usuario.tipo?.toLowerCase() !== "estoquista") {
            alert("Acesso não autorizado. Apenas estoquistas podem acessar esta página.");
            window.location.href = "/ecommerce/frontend/index.html";
            return false;
        }

        return true;
    } catch (error) {
        console.error("Erro ao verificar sessão:", error);
        alert("Erro ao verificar permissão. Faça login novamente.");
        window.location.href = "/ecommerce/frontend/login.html";
        return false;
    }
};

const carregarPedidos = async () => {
    const corpoTabela = document.querySelector("#tabela-corpo");

    if (!corpoTabela) {
        console.error("Elemento tbody da tabela não encontrado.");
        return;
    }

    try {
        const response = await fetch("http://127.0.0.1:8080/api/pedidos", {
            credentials: "include"
        });

        if (!response.ok) {
            throw new Error("Falha ao carregar pedidos");
        }

        const pedidos = await response.json();
        corpoTabela.innerHTML = "";

        pedidos.forEach(pedido => {
            const tr = document.createElement("tr");

            const data = new Date(pedido.dataHora);
            const dataFormatada = isNaN(data) ? "---" : data.toLocaleDateString("pt-BR");

            // Cria o select para alterar status
            const select = document.createElement("select");
            statusOptions.forEach(status => {
                const option = document.createElement("option");
                option.value = status;
                option.textContent = status.replaceAll("_", " ");
                if (status === pedido.status) {
                    option.selected = true;
                }
                select.appendChild(option);
            });

            select.addEventListener("change", async () => {
                const novoStatus = select.value;

                try {
                    const res = await fetch(`http://127.0.0.1:8080/api/pedidos/${pedido.id}/status`, {
                        method: "PUT",
                        headers: {
                            "Content-Type": "application/json"
                        },
                        body: JSON.stringify({ novoStatus }),
                        credentials: "include"
                    });

                    if (!res.ok) {
                        const erro = await res.json();
                        throw new Error(erro.message || "Erro ao atualizar o status.");
                    }

                    alert("Status atualizado com sucesso!");
                    pedido.status = novoStatus; // Atualiza o status localmente
                } catch (error) {
                    console.error("Erro ao atualizar status:", error);
                    alert("Erro ao atualizar status: " + error.message);
                    select.value = pedido.status; // Reverte seleção ao antigo status
                }
            });

            tr.innerHTML = `
                <td>${pedido.numeroPedido || "---"}</td>
                <td>${dataFormatada}</td>
                <td>${(pedido.totalGeral ?? 0).toLocaleString("pt-BR", { style: "currency", currency: "BRL" })}</td>
                <td>${(pedido.status ?? "AGUARDANDO_PAGAMENTO").replaceAll("_", " ")}</td>
            `;

            const tdSelect = document.createElement("td");
            tdSelect.appendChild(select);
            tr.appendChild(tdSelect);

            corpoTabela.appendChild(tr);
        });

    } catch (error) {
        console.error("Erro ao carregar pedidos:", error);
        corpoTabela.innerHTML = `<tr><td colspan="5">Erro ao carregar pedidos: ${error.message}</td></tr>`;
    }
};

document.addEventListener("DOMContentLoaded", async () => {
    const autorizado = await verificarPermissaoEstoquista();
    if (autorizado) {
        carregarPedidos();
    }
});
