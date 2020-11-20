# Securi File Upload

### Sobre
Este é um projeto da disciplina Segurança e Auditoria de Sistemas - 2020/1 do Instituto de Informática da Universidade Federal de Goiás. 

### Objetivo
Detalhar principais tipos de vulnerabilidades em sistemas backend no que tange a upload de arquivos e implementar um sistema backend utilizando SpringBoot e ClamAV, a fim de preveni-lo contra os principais ataques.

### Um pouco sobre os principais ataques
Abaixo, seguem os principais tipos de ataque que podem ocorrer durante upload de um arquivo a um server backend.

#### Escutas de rede: ataque do tipo snooping
Em uma sessão de transmissão de arquivo, é possível efetuar um ataque do tipo snooping para escutar os dados dos ‘chunks’ do arquivo, a fim de obter dados importantes.

Para prevenir contra esses ataques:
- A sessão e os protocolos de transferência na rede devem ser seguros, com encriptação confiável.

#### Upload de arquivos grandes: desperdício de recursos e ataque contra disponibilidade
Envio de arquivos grandes pode acarretar em falta de armazenamento. Além disso, se aliado a uma rede de bots, pode aumentar significativamente o risco de ter o serviço negado. 

Para tanto, algumas soluções podem ser adotadas:
- Implementação de testes cognitivos para distinguir humanos de máquinas, como, por exemplo, CAPTCHA.
- Uso de sistema de autorização.
- Ajuste fino do limite de upload dos arquivos, no servidor.
- Ajuste fino de banda de rede, no servidor.
- Se necessário, utilizar um serviço de proteção contra DDoS.

#### Upload de arquivos maliciosos
Códigos maliciosos podem ser embutidos nos arquivos dos mais diversos formatos. Quando esses arquivos podem ser compartilhados entre vários usuários ou fluxos, a situação fica ainda pior.
No formato PDF, atacantes podem disfarçar códigos em JavaScript nos objetos do arquivo. Desta forma, ao ser aberto no leitor, pode desempenhar as mais diversas ações, como envio de form’s, ataques ao navegador de internet, etc.
No formato zip, é possível criar arquivos que fazem uso do ataque ‘zip-bomb’, por exemplo, de modo que  ao serem extraídos podem expandir para tamanhos maiores que o sistema suporta (ordem de terabytes a petabytes).

Para tanto, algumas soluções podem ser adotadas:
- Gerar de arquivos com nomes aleatórios, de modo que o atacante tenha dificuldade em obter o caminho do arquivo com exatidão.
- Ajustar configurações CORS do servidor a fim de permitir requisições somente de domínios permitidos.
- Na resposta das requisições, retornar somente o que é necessário.
- Requisitar autorização nas rotas de upload.
- Implementar verificador de código malicioso (sanitizer).
- Adotar whitelist de tipos de arquivos suportados (ou blacklist, para os não suportados) e verificar se o tipo do arquivo é de legítimo (não houve renomeação de um formato de arquivo .exe para .txt, por exemplo).
- Manter o sistema atualizado.

### Requisitos

- ClamAV
- Java 11
- Maven

### Resultados

#### Limitações
A solução com ClamAV tem algumas limitações, como, por exemplo, o upload de um zip-bomb famoso, o '42.zip'. ClamAV **não  foi capaz** de identificar o código malicioso:
Requisição: __curl -i -X POST -F file=@42.zip http://localhost:8082/upload__
Resposta: 
HTTP/1.1 200 
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
Content-Type: application/json
Transfer-Encoding: chunked
Date: Fri, 20 Nov 2020 21:40:18 GMT

**{"message":"OK"}**


