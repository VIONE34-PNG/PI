document.getElementById('formLogin').addEventListener('submit', async function (e) {
  e.preventDefault(); 

  const email = document.getElementById("email").value; 
  const senha = document.getElementById("senha").value; 

  if (!validarEmail(email)) {
    return mostrarErro("Email inválido."); 
  }
  if (!senha) {
    return mostrarErro("Senha não pode estar vazia."); 
  }

  const dadosLogin = { email, senha };

  try {
    const response = await fetch("http://127.0.0.1:8080/api/usuarios/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(dadosLogin),
      credentials: "include"
    });

    if (response.ok) {
      const mensagem = await response.text();
      alert(mensagem); 

      // Buscar os dados atualizados do usuário
      const userResponse = await fetch("http://127.0.0.1:8080/api/usuarios/dados", {
        method: "GET",
        credentials: "include"
      });

      if (userResponse.ok) {
        const dadosUsuario = await userResponse.json();
        localStorage.setItem('usuarioLogado', JSON.stringify(dadosUsuario)); // Atualiza o localStorage
      }

      window.location.href = "index.html"; // Redireciona após login bem-sucedido
    } else {
      const erro = await response.text();
      mostrarErro("Erro: " + erro);
    }
  } catch (error) {
    mostrarErro("Erro ao conectar com o servidor."); 
  }
});

function mostrarErro(msg) {
  const mensagemElement = document.getElementById("mensagem");
  mensagemElement.innerText = msg;
  mensagemElement.style.display = "block"; 
}

function validarEmail(email) {
  const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/; 
  return regex.test(email);
}
