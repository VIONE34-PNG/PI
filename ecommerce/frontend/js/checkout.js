document.addEventListener("DOMContentLoaded", async () => {

  async function buscarEnderecoPorCEP(cep) {
        try {
            const response = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
            if (!response.ok) {
                throw new Error("Erro ao buscar o CEP.");
            }
            const data = await response.json();
            if (data.erro) {
                throw new Error("CEP não encontrado.");
            }
            return data;
        } catch (error) {
            console.error("Erro ao buscar endereço:", error);
            alert(error.message);
            return null;
        }
    }
  async function verificarSessao() {
    try {
      const response = await fetch("http://127.0.0.1:8080/api/usuarios/sessao", {
        method: "GET",
        credentials: "include",
      });

      if (!response.ok) {
        window.location.href = "login.html";
        return null;
      }

      const usuario = await response.json();
      if (!usuario || !usuario.nome) {
        window.location.href = "login.html";
        return null;
      }

      return usuario;
    } catch (error) {
      console.error("Erro ao verificar sessão:", error);
      window.location.href = "login.html";
      return null;
    }
  }

  async function buscarCarrinhoBackend() {
    try {
      const response = await fetch("http://127.0.0.1:8080/api/carrinho/listar", {
        method: "GET",
        credentials: "include",
      });

      if (!response.ok) {
        console.error("Erro ao buscar carrinho:", response.status);
        return [];
      }

      return await response.json();
    } catch (error) {
      console.error("Erro na requisição do carrinho:", error);
      return [];
    }
  }

  const usuario = await verificarSessao();
  if (!usuario) return;

  document.getElementById("nome").value = usuario.nome || "";
  document.getElementById("email").value = usuario.email || "";
  document.getElementById("telefone").value = usuario.telefone || "";

  document.getElementById("telefone").addEventListener("input", (e) => {
    let telefone = e.target.value.replace(/\D/g, "");
    telefone = telefone.replace(/^(\d{2})(\d)/g, "($1) $2");
    telefone = telefone.replace(/(\d{5})(\d)/, "$1-$2");
    e.target.value = telefone;
  });

  function renderizarEnderecos() {
    const listaEnderecosContainer = document.getElementById("lista-enderecos");
    enderecos = JSON.parse(localStorage.getItem("enderecos") || "[]");

    listaEnderecosContainer.innerHTML = "";

    if (enderecos.length === 0) {
      listaEnderecosContainer.innerHTML = "<p>Nenhum endereço salvo.</p>";
      return;
    }

    enderecos.forEach((endereco, index) => {
      const div = document.createElement("div");
      div.classList.add("endereco-item");

      const radio = document.createElement("input");
      radio.type = "radio";
      radio.name = "enderecoSelecionado";
      radio.value = index;
      radio.id = `endereco-${index}`;

      const label = document.createElement("label");
      label.htmlFor = `endereco-${index}`;
      label.textContent = `${endereco.logradouro}, ${endereco.numero || ""} - ${endereco.bairro || ""}, ${endereco.cidade} - ${endereco.uf} (${endereco.cep})`;

      div.appendChild(radio);
      div.appendChild(label);
      listaEnderecosContainer.appendChild(div);
    });
  }

  let enderecos = JSON.parse(localStorage.getItem("enderecos") || "[]");
  renderizarEnderecos();

  const frete = parseFloat(sessionStorage.getItem("freteSelecionado")) || 0;
  const cartItemsElement = document.getElementById("cart-items");
  const totalPriceElement = document.getElementById("total-price");
  const cartItems = await buscarCarrinhoBackend();

  if (!cartItems || cartItems.length === 0) {
    cartItemsElement.innerHTML = "<li>O carrinho está vazio.</li>";
    totalPriceElement.textContent = "R$ 0,00";
  } else {
    cartItemsElement.innerHTML = "";

    let total = 0;
    cartItems.forEach((item) => {
      const li = document.createElement("li");
      li.textContent = `${item.produto.nome} - Quantidade: ${item.quantidade}`;
      cartItemsElement.appendChild(li);
      const precoUnitario = item.produto.valorProduto || 0;
      total += precoUnitario * item.quantidade;
    });

    total += frete;
    totalPriceElement.textContent = `R$ ${total.toFixed(2).replace(".", ",")}`;
    sessionStorage.setItem("total", total.toFixed(2));
  }

  const pagamentoSelect = document.getElementById("pagamento");
  const cartaoInfo = document.getElementById("cartao-info");
  const boletoInfo = document.getElementById("boleto-info");

  pagamentoSelect.addEventListener("change", (e) => {
    const selectedPayment = e.target.value;
    cartaoInfo.style.display = selectedPayment === "cartao" ? "block" : "none";
    boletoInfo.style.display = selectedPayment === "boleto" ? "block" : "none";
  });

  const btnAdicionarEndereco = document.getElementById("btn-adicionar-endereco");
  const novoEnderecoContainer = document.getElementById("novo-endereco");

  btnAdicionarEndereco.addEventListener("click", () => {
    novoEnderecoContainer.style.display =
      novoEnderecoContainer.style.display === "none" || !novoEnderecoContainer.style.display
        ? "block"
        : "none";
  });

  document.getElementById("salvar-endereco").addEventListener("click", async () => {
  const cep = document.getElementById("novo-endereco-cep").value.trim();

  if (!cep) {
    alert("Por favor, informe o CEP.");
    return;
  }

  const enderecoAPI = await buscarEnderecoPorCEP(cep);
  if (!enderecoAPI) {
    return; 
  }

  const logradouroInput = document.getElementById("novo-endereco-rua");
  const bairroInput = document.getElementById("novo-endereco-bairro");
  const cidadeInput = document.getElementById("novo-endereco-cidade");
  const estadoInput = document.getElementById("novo-endereco-estado");

  if (!logradouroInput.value.trim()) logradouroInput.value = enderecoAPI.logradouro;
  if (!bairroInput.value.trim()) bairroInput.value = enderecoAPI.bairro;
  if (!cidadeInput.value.trim()) cidadeInput.value = enderecoAPI.localidade;
  if (!estadoInput.value.trim()) estadoInput.value = enderecoAPI.uf;

  const logradouro = logradouroInput.value.trim();
  const numero = document.getElementById("novo-endereco-numero").value.trim();
  const complemento = document.getElementById("novo-endereco-complemento").value.trim();
  const bairro = bairroInput.value.trim();
  const cidade = cidadeInput.value.trim();
  const uf = estadoInput.value.trim();

  if (!logradouro || !cep || !cidade || !uf) {
    alert("Por favor, preencha todos os campos obrigatórios: rua, cep, cidade e estado.");
    return;
  }

  const novoEndereco = { logradouro, numero, complemento, bairro, cep, cidade, uf };
  enderecos.push(novoEndereco);
  localStorage.setItem("enderecos", JSON.stringify(enderecos));

  logradouroInput.value = "";
  document.getElementById("novo-endereco-numero").value = "";
  document.getElementById("novo-endereco-complemento").value = "";
  bairroInput.value = "";
  document.getElementById("novo-endereco-cep").value = "";
  cidadeInput.value = "";
  estadoInput.value = "";

  novoEnderecoContainer.style.display = "none";
  renderizarEnderecos();
});

  document.getElementById("resumo-pedido").addEventListener("click", async (e) => {
    e.preventDefault();

    const radios = document.getElementsByName("enderecoSelecionado");
    let enderecoSelecionado = null;
    for (const radio of radios) {
      if (radio.checked) {
        const index = parseInt(radio.value);
        enderecoSelecionado = enderecos[index];
        break;
      }
    }

    if (!enderecoSelecionado) {
      alert("Por favor, selecione um endereço para entrega.");
      return;
    }

    const enderecoEntregaString = `${enderecoSelecionado.logradouro}, ${enderecoSelecionado.numero || ""} - ${enderecoSelecionado.bairro || ""}, ${enderecoSelecionado.cidade} - ${enderecoSelecionado.uf}, CEP: ${enderecoSelecionado.cep}`;

    const itensPedido = cartItems.map((item) => ({
      produto: {codigo: item.produto.codigo},
      quantidade: item.quantidade,
      precoUnitario: item.produto.valorProduto,
    }));

    let pagamento = {};
    if (pagamentoSelect.value === "cartao") {
    pagamento = {
    "@type": "cartao",
    numeroCartao: document.getElementById("numero-cartao").value,
    nomeImpresso: document.getElementById("nome-cartao").value,
    validade: document.getElementById("vencimento-cartao").value,
    codigoVerificador: document.getElementById("codigo-cartao").value,
    parcelas: parseInt(document.getElementById("parcelas-cartao").value || "1")
    };
     } else if (pagamentoSelect.value === "boleto") {
     pagamento = {
     "@type": "boleto"
    };
   }

    const pedido = {
      usuario: { id: usuario.id },
      enderecoEntrega: enderecoEntregaString,
      pagamento: pagamento,
      formaPagamento: pagamentoSelect.value,
      frete: frete,
      totalGeral: parseFloat(sessionStorage.getItem("total")) || 0,
      itens: itensPedido
    };

    console.log("Pedido a ser enviado:", pedido);

    try {
      const response = await fetch("http://127.0.0.1:8080/api/pedidos/finalize", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify(pedido),
      });

      if (response.ok) {
        const pedidoSalvo = await response.json();
        sessionStorage.setItem("resumoPedido", JSON.stringify(pedidoSalvo));
        alert("Pedido ok! Direcionando ao resumo");
        window.location.href = "resumo-pedido.html";
      } else {
        const erro = await response.text();
        alert("Erro ao finalizar pedido: " + erro);
      }
    } catch (error) {
      console.error("Erro no pedido:", error);
      alert("Erro ao finalizar pedido.");
    }
  });
});
