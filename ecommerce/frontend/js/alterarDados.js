document.addEventListener('DOMContentLoaded', async function () {
    const formDados = document.getElementById("formEditarDados");
    const formSenha = document.getElementById("formTrocarSenha");
    const formEndereco = document.getElementById("formNovoEndereco");

    const mensagemDados = document.getElementById("mensagemDados");
    const mensagemSenha = document.getElementById("mensagemSenha");
    const mensagemEndereco = document.getElementById("mensagemEndereco");
    const listaEnderecos = document.getElementById("listaEnderecos");

    const cepInput = document.getElementById("cep");
    const ruaInput = document.getElementById("rua");
    const numeroInput = document.getElementById("numero");
    const complementoInput = document.getElementById("complemento");
    const bairroInput = document.getElementById("bairro");
    const cidadeInput = document.getElementById("cidade");
    const ufInput = document.getElementById("uf");
    const enderecoPadraoInput = document.getElementById("enderecoPadrao");

    let enderecos = JSON.parse(localStorage.getItem("enderecos")) || [];

    const buscarEnderecoPorCep = async (cep) => {
        const cepLimpo = cep.replace(/\D/g, "");
        if (cepLimpo.length === 8) {
            try {
                const response = await fetch(`https://viacep.com.br/ws/${cepLimpo}/json/`);
                const dados = await response.json();
                if (!dados.erro) {
                    ruaInput.value = dados.logradouro || "";
                    bairroInput.value = dados.bairro || "";
                    cidadeInput.value = dados.localidade || "";
                    ufInput.value = dados.uf || "";
                } else {
                    mostrarMensagem(mensagemEndereco, "CEP inválido ou não encontrado.", "red");
                }
            } catch (error) {
                console.error("Erro ao buscar CEP:", error);
                mostrarMensagem(mensagemEndereco, "Erro ao buscar informações do CEP. Tente novamente.", "red");
            }
        } else {
            mostrarMensagem(mensagemEndereco, "CEP inválido. Deve conter 8 dígitos.", "red");
        }
    };

    cepInput.addEventListener("blur", () => buscarEnderecoPorCep(cepInput.value));

    const mostrarMensagem = (elemento, texto, cor = "red") => {
        elemento.innerText = texto;
        elemento.style.color = cor;
        elemento.style.display = "block";
    };

    // Buscar dados do usuário
    try {
        const response = await fetch("http://127.0.0.1:8080/api/usuarios/sessao", {
            method: "GET",
            credentials: "include"
        });

        if (!response.ok) {
            window.location.href = "/ecommerce/frontend/login.html";
            return;
        }

        const usuarioLogado = await response.json();
        document.getElementById("nome").value = usuarioLogado.nome || "";
        document.getElementById("dataNascimento").value = usuarioLogado.dataNascimento || "";
        document.getElementById("genero").value = usuarioLogado.genero || "";
    } catch (error) {
        console.error("Erro ao buscar sessão:", error);
        window.location.href = "/ecommerce/frontend/login.html";
    }

    // Atualizar dados
    formDados.addEventListener("submit", async function (e) {
        e.preventDefault();

        const nome = document.getElementById("nome").value;
        const dataNascimento = document.getElementById("dataNascimento").value;
        const genero = document.getElementById("genero").value;

        try {
            const response = await fetch("http://127.0.0.1:8080/api/usuarios/atualizar", {
                method: "PUT",
                credentials: "include",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ nome, dataNascimento, genero })
            });

            if (response.ok) {
                mostrarMensagem(mensagemDados, "Dados atualizados com sucesso!", "green");
            } else {
                const erro = await response.text();
                mostrarMensagem(mensagemDados, `Erro: ${erro}`);
            }
        } catch (error) {
            console.error("Erro ao atualizar dados:", error);
            mostrarMensagem(mensagemDados, "Erro ao atualizar. Tente novamente.");
        }
    });

    // Trocar senha
    formSenha.addEventListener("submit", async function (e) {
        e.preventDefault();

        const senhaAtual = document.getElementById("senhaAtual").value;
        const novaSenha = document.getElementById("novaSenha").value;
        const confirmarSenha = document.getElementById("confirmarSenha").value;

        if (!senhaAtual || !novaSenha || !confirmarSenha) {
            mostrarMensagem(mensagemSenha, "Preencha todos os campos.");
            return;
        }

        if (novaSenha !== confirmarSenha) {
            mostrarMensagem(mensagemSenha, "As senhas não coincidem.");
            return;
        }

        try {
            const response = await fetch("http://127.0.0.1:8080/api/usuarios/alterarSenha", {
                method: "PUT",
                credentials: "include",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ senhaAtual, novaSenha })
            });

            if (response.ok) {
                mostrarMensagem(mensagemSenha, "Senha alterada com sucesso!", "green");
                formSenha.reset();
            } else {
                const erro = await response.text();
                mostrarMensagem(mensagemSenha, `Erro: ${erro}`);
            }
        } catch (error) {
            console.error("Erro ao alterar senha:", error);
            mostrarMensagem(mensagemSenha, "Erro ao alterar senha. Tente novamente.");
        }
    });

    // Adicionar novo endereço
    formEndereco.addEventListener("submit", (e) => {
        e.preventDefault();

        const novoEndereco = {
            cep: cepInput.value,
            logradouro: ruaInput.value,
            numero: numeroInput.value,
            complemento: complementoInput.value,
            bairro: bairroInput.value,
            cidade: cidadeInput.value,
            uf: ufInput.value,
            enderecoPadrao: enderecoPadraoInput.checked
        };

        if (novoEndereco.enderecoPadrao) {
            enderecos.forEach(end => end.enderecoPadrao = false);
        }

        enderecos.push(novoEndereco);
        localStorage.setItem("enderecos", JSON.stringify(enderecos));

        mostrarMensagem(mensagemEndereco, "Endereço adicionado com sucesso!", "green");
        renderizarEnderecos();
        formEndereco.reset();
    });

    // Definir endereço padrão
    function definirEnderecoPadrao(index) {
        enderecos.forEach((end, i) => {
            end.enderecoPadrao = (i === index);
        });

        localStorage.setItem("enderecos", JSON.stringify(enderecos));
        mostrarMensagem(mensagemEndereco, "Endereço padrão atualizado!", "green");
        renderizarEnderecos();
    }

    // Renderizar lista de endereços
    function renderizarEnderecos() {
        listaEnderecos.innerHTML = '';

        enderecos.forEach((endereco, index) => {
            const li = document.createElement("li");
            li.style.marginBottom = "12px";

            li.innerHTML = `
                <strong>${endereco.logradouro}, ${endereco.numero}</strong>
                ${endereco.complemento ? ` (${endereco.complemento})` : ""}<br>
                ${endereco.bairro}, ${endereco.cidade} - ${endereco.uf}<br>
                CEP: ${endereco.cep}<br>
                ${endereco.enderecoPadrao ? "<span style='color:green;'>✅ Endereço Padrão</span>" : ""}
                <br>
                <button class="btn-definir-padrao" data-index="${index}" style="margin-top: 5px;">
                    Definir como padrão
                </button>
            `;

            listaEnderecos.appendChild(li);
        });

        document.querySelectorAll(".btn-definir-padrao").forEach(btn => {
            btn.addEventListener("click", () => {
                const index = parseInt(btn.getAttribute("data-index"));
                definirEnderecoPadrao(index);
            });
        });
    }

    renderizarEnderecos();
});
