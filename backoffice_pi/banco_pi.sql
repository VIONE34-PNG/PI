CREATE database ecommerce_pi;
USE ecommerce_pi;
CREATE TABLE userBackoffice(
id INT auto_increment PRIMARY KEY,
email VARCHAR(300) NOT NULL UNIQUE,
senha VARCHAR(100) NOT NULL,
tipoUser ENUM('adm', 'estoquista', 'cliente') NOT NULL,
status ENUM('ativado', 'desativado') NOT NULL,
nome VARCHAR(100),
cpf char(11)
);

CREATE TABLE IF NOT EXISTS produtos(
codigo INT auto_increment PRIMARY KEY,
nome varchar(200) NOT NULL,
avaliacao DECIMAL(2,1) CHECK (avaliacao >= 1 AND avaliacao <= 5),
descricaoDetalhada varchar(2000) NOT NULL,
qtdEstoque int NOT NULL,
valorProduto DECIMAL(10,2) NOT NULL);

CREATE TABLE IF NOT EXISTS imagensProduto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    produto_id INT NOT NULL,
    nome_arquivo VARCHAR(255) NOT NULL,
    diretorio_origem VARCHAR(255) NOT NULL,
    principal BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (produto_id) REFERENCES produtos(codigo) ON DELETE CASCADE
);


ALTER TABLE produtos ADD COLUMN status ENUM('ativo', 'desativado') NOT NULL DEFAULT 'ativo';

SELECT * FROM produtos WHERE valorProduto IS NULL;
UPDATE produtos SET valorProduto = 1.0 WHERE valorProduto IS NULL;



ALTER TABLE produtos MODIFY COLUMN qtdEstoque INT NOT NULL DEFAULT 1;
UPDATE produtos SET qtdEstoque = 1 WHERE qtdEstoque IS NULL;

UPDATE produtos SET valorProduto = 0 WHERE valorProduto IS NULL;
UPDATE produtos SET valorProduto = ROUND(valorProduto, 2) WHERE valorProduto IS NOT NULL;

SET SQL_SAFE_UPDATES = 0;

CREATE TABLE CarrinhoProduto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    produto_id INT NOT NULL,
    quantidade INT NOT NULL DEFAULT 1,
    preco_unitario DECIMAL(10,2) NOT NULL,
    total DECIMAL(10,2) GENERATED ALWAYS AS (quantidade * preco_unitario) STORED,
    FOREIGN KEY (produto_id) REFERENCES produtos (codigo)
);


CREATE TABLE usuarios (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  senha VARCHAR(255) NOT NULL,
  cpf VARCHAR(11) NOT NULL UNIQUE,
  tipoUser VARCHAR(20),
  status VARCHAR(20),
  dataNascimento DATE,
  genero VARCHAR(20)
);

SELECT * FROM usuarios;

CREATE TABLE enderecos (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    cep VARCHAR(20) NOT NULL,
    logradouro VARCHAR(255) NOT NULL,
    numero VARCHAR(20) NOT NULL,
    complemento VARCHAR(255),
    bairro VARCHAR(255),
    cidade VARCHAR(255),
    uf VARCHAR(2),
    usuario_id BIGINT NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

ALTER TABLE CarrinhoProduto ADD COLUMN id_usuario BIGINT;

ALTER TABLE CarrinhoProduto ADD CONSTRAINT fk_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id);

CREATE TABLE pedido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    pagamento_id BIGINT,  
    data_pedido DATETIME DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'pendente', 
    total DECIMAL(10,2) NOT NULL,
    numero_pedido VARCHAR(50) UNIQUE,

    CONSTRAINT fk_pedido_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id) ON DELETE CASCADE,

    CONSTRAINT fk_pedido_pagamento FOREIGN KEY (pagamento_id)
        REFERENCES pagamento(id) ON DELETE SET NULL  
);


CREATE TABLE item_pedido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    produto_codigo INT NOT NULL,
    quantidade INT NOT NULL,
    preco_unitario DECIMAL(10,2) NOT NULL,

    CONSTRAINT fk_item_pedido FOREIGN KEY (pedido_id)
        REFERENCES pedido(id) ON DELETE CASCADE,

    CONSTRAINT fk_item_produto FOREIGN KEY (produto_codigo)
        REFERENCES produtos(codigo) ON DELETE CASCADE
);


CREATE TABLE pagamento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo_pagamento VARCHAR(20) NOT NULL,  
    status_pagamento VARCHAR(20) DEFAULT 'pendente', 
    pedido_id BIGINT NOT NULL,  
    data_pagamento DATETIME,   

    numero_cartao VARCHAR(16),  
    nome_cartao VARCHAR(100),   
    validade_cartao DATE,       
    codigo_verificador VARCHAR(3), 
    parcelas INT,               

    
    numero_boleto VARCHAR(50), 
    data_vencimento_boleto DATE, 

    CONSTRAINT fk_pagamento_pedido FOREIGN KEY (pedido_id)
        REFERENCES pedido(id) ON DELETE CASCADE
);






