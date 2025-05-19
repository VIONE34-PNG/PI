document.getElementById('formCadastro').addEventListener('submit', async function (e) {
  e.preventDefault();

  const nome = document.getElementById("nome").value;
  const email = document.getElementById("email").value;
  const senha = document.getElementById("senha").value;
  const confirmarSenha = document.getElementById("confirmarSenha").value;
  const cpf = document.getElementById("cpf").value;
  const dataNascimento = document.getElementById("dataNascimento").value;
  const genero = document.getElementById("genero").value;
  const tipoUser = document.getElementById("tipoUsuario").value;

  if (!validarNome(nome)) return mostrarErro("Nome precisa ter pelo menos 2 palavras com 3 letras cada.");
  if (!validarCPF(cpf)) return mostrarErro("CPF inválido.");
  if (senha !== confirmarSenha) return mostrarErro("Senhas não coincidem.");
  if (!tipoUser) return mostrarErro("Por favor, selecione o tipo de usuário.");

  const enderecoFaturamento = {
    cep: document.getElementById("cep").value,
    logradouro: document.getElementById("logradouro").value,
    numero: document.getElementById("numero").value,
    complemento: document.getElementById("complemento").value,
    bairro: document.getElementById("bairro").value,
    cidade: document.getElementById("cidade").value,
    uf: document.getElementById("uf").value
  };

  if (!validarEndereco(enderecoFaturamento)) {
    return mostrarErro("Preencha todos os campos obrigatórios do endereço de faturamento.");
  }

  const enderecosEntrega = Array.from(document.querySelectorAll('.endereco-entrega')).map(container => ({
    cep: container.querySelector('.cep').value,
    logradouro: container.querySelector('.logradouro').value,
    numero: container.querySelector('.numero').value,
    complemento: container.querySelector('.complemento').value,
    bairro: container.querySelector('.bairro').value,
    cidade: container.querySelector('.cidade').value,
    uf: container.querySelector('.uf').value
  }));

  const dados = {
    nome,
    email,
    senha,
    cpf,
    tipo: tipoUser,
    status: "ativo",
    dataNascimento,
    genero,
    enderecoFaturamento,
    enderecosEntrega
  };

  try {
    const response = await fetch("http://localhost:8080/api/usuarios/cadastro", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(dados),
      credentials: "include"
    });

    if (response.ok) {
      const resultado = await response.json();

      // Salvar dados no localStorage para uso em alterar.html
      const dadosParaArmazenar = {
        nome,
        email,
        cpf,
        dataNascimento,
        genero,
        tipoUser
      };
      localStorage.setItem('dadosUsuario', JSON.stringify(dadosParaArmazenar));
      localStorage.setItem('enderecos', JSON.stringify([enderecoFaturamento, ...enderecosEntrega]));

      if (resultado.redirect === "carrinho") {
        window.location.href = "/ecommerce/frontend/carrinho.html";
      } else {
        alert("Cadastro realizado com sucesso!");
        window.location.href = "/ecommerce/frontend/login.html";
      }
    } else {
      const erro = await response.text();
      mostrarErro("Erro: " + erro);
    }
  } catch (error) {
    mostrarErro("Erro ao conectar com o servidor.");
  }
});

function mostrarErro(msg) {
  document.getElementById("mensagem").innerText = msg;
}

function validarNome(nome) {
  const partes = nome.trim().split(" ");
  return partes.length >= 2 && partes.every(p => p.length >= 3);
}

function validarCPF(cpf) {
  return /^\d{11}$/.test(cpf); // Validação simples
}

function validarEndereco(endereco) {
  return endereco.cep && endereco.logradouro && endereco.numero &&
         endereco.bairro && endereco.cidade && endereco.uf;
}

document.getElementById("cep").addEventListener("blur", async function () {
  const cep = this.value.replace(/\D/g, "");
  if (cep.length === 8) {
    const response = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
    const dados = await response.json();
    if (!dados.erro) {
      document.getElementById("logradouro").value = dados.logradouro;
      document.getElementById("bairro").value = dados.bairro;
      document.getElementById("cidade").value = dados.localidade;
      document.getElementById("uf").value = dados.uf;
    } else {
      mostrarErro("CEP inválido.");
    }
  }
});

function copiarEndereco() {
  adicionarEndereco(true);
}

function adicionarEndereco(copiar = false) {
  const container = document.createElement("div");
  container.classList.add("endereco-entrega");
  container.innerHTML = `
    <input type="text" class="cep" placeholder="CEP" required />
    <input type="text" class="logradouro" placeholder="Logradouro" required />
    <input type="text" class="numero" placeholder="Número" required />
    <input type="text" class="complemento" placeholder="Complemento" />
    <input type="text" class="bairro" placeholder="Bairro" required />
    <input type="text" class="cidade" placeholder="Cidade" required />
    <input type="text" class="uf" placeholder="UF" required />
  `;

  if (copiar) {
    container.querySelector(".cep").value = document.getElementById("cep").value;
    container.querySelector(".logradouro").value = document.getElementById("logradouro").value;
    container.querySelector(".numero").value = document.getElementById("numero").value;
    container.querySelector(".complemento").value = document.getElementById("complemento").value;
    container.querySelector(".bairro").value = document.getElementById("bairro").value;
    container.querySelector(".cidade").value = document.getElementById("cidade").value;
    container.querySelector(".uf").value = document.getElementById("uf").value;
  }

  document.getElementById("enderecosEntrega").appendChild(container);
}
