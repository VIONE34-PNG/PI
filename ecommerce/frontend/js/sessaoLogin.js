document.addEventListener('DOMContentLoaded', async function () {
    console.log("DOM carregado!");
    const userButton = document.getElementById('userButton');
    const userMenu = document.getElementById('userMenu');
    const menuDropdown = document.getElementById('menuDropdown');
    const logoutBtn = document.getElementById('logoutBtn');
    const estoquistaLink = document.getElementById('estoquistaLink');

    try {
        console.log("Verificando sessão do usuário...");
        const response = await fetch("http://127.0.0.1:8080/api/usuarios/sessao", {
            method: "GET",
            credentials: "include"
        });

        console.log("Status da resposta:", response.status, response.statusText);

        if (response.ok) {
            const usuario = await response.json();
            console.log("Usuário logado:", usuario);

            if (userButton) {
                userButton.innerText = "Olá, " + usuario.nome;

                // Abre/fecha dropdown ao clicar no botão do usuário
                if (menuDropdown) {
                    userButton.addEventListener('click', (event) => {
                        event.stopPropagation();
                        menuDropdown.style.display = menuDropdown.style.display === "block" ? "none" : "block";
                    });
                }
            }

            // Mostrar link do estoquista somente se o usuário for estoquista
            if (estoquistaLink) {
                if (usuario.tipo && usuario.tipo.toLowerCase() === "estoquista") {
                    estoquistaLink.style.display = "block";
                } else {
                    estoquistaLink.style.display = "none";
                }
            }

            // Fecha dropdown ao clicar fora
            if (menuDropdown && userMenu) {
                document.addEventListener('click', (event) => {
                    if (!userMenu.contains(event.target)) {
                        menuDropdown.style.display = "none";
                    }
                });
            }

            // Logout com confirmação
            if (logoutBtn) {
                logoutBtn.addEventListener('click', async () => {
                    const confirmLogout = confirm("Você está prestes a sair. Deseja continuar?");
                    if (confirmLogout) {
                        await fetch("http://127.0.0.1:8080/api/usuarios/logout", {
                            method: "POST",
                            credentials: "include"
                        });
                        alert("Você foi deslogado com sucesso!");
                        window.location.href = "index.html"; 
                    }
                });
            }

        } else {
            console.log("Usuário não logado.");
            if (userMenu) {
                userMenu.innerHTML = `<a href="login.html" class="userBtn">Entre ou Cadastre-se</a>`;
            }
        }
    } catch (error) {
        console.error("Erro ao verificar sessão:", error);
        if (userMenu) {
            userMenu.innerHTML = `<a href="login.html" class="userBtn">Entre ou Cadastre-se</a>`;
        }
    }
});
