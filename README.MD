# Securi File Upload

### Sobre
Este é um projeto da disciplina Segurança e Auditoria de Sistemas - 2020/1 do Instituto de Informática da Universidade Federal de Goiás. 

### Introdução
Discutir principais cenários de ataque a sistemas backend no que tange a upload de arquivos. 

### Metodologia
Demonstração didática de sistema backend protegido contra upload de arquivos grandes e upload de alguns arquivos maliciosos, utilizando SpringBoot e ClamAV.

### Alguns tipos de ataque a sessões de upload de arquivo
Existem muitos ataques que podem ser efetudos a sessões de upload de arquivo. Abaixo, segue um levantamento com alguns incidentes de segurança que podem ocorrer durante upload de um arquivo a um server backend.

[LISTA COM ATAQUES]

### Avaliação de cenários

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

- [ClamAV (Docker image)](https://hub.docker.com/r/mkodockx/docker-clamav/)
- [Java 11](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
- [Maven](https://maven.apache.org/download.cgi)

### Passo a passo para build

- docker run -d --net=host -p 3310:3310 mkodockx/docker-clamav
- mvn spring-boot:run

### Demonstração

#### Arquivo malicioso
Utilizando o arquivo txt eicar.txt [obtenha neste link](https://www.eicar.org/?page_id=3950), com o seguinte conteúdo 

E a requisição:

**curl -i -X POST -F file=@eicar.txt http://localhost:8082/upload**

É obitda a seguinte resposta:

HTTP/1.1 409 
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
Content-Type: application/json
Transfer-Encoding: chunked
Date: Fri, 20 Nov 2020 22:12:29 GMT

**{"message":"Falha ao enviar arquivo..."}**

#### Arquivo com extensão incompatível

Com a requisição __curl -i -X POST -F file=@teste.pdf.txt http://localhost:8082/upload__, é obtido o seguinte resultado:

HTTP/1.1 100 

HTTP/1.1 409 
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
Content-Type: application/json
Transfer-Encoding: chunked
Date: Fri, 20 Nov 2020 22:20:26 GMT

**{"message":"Arquivo com formato inválido..."}**


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

### Referências
- [Snooping](https://docs.oracle.com/cd/E19957-01/805-7695/ispsecurity-40/index.html)
- [Ataques em sessões de upload de arquivo](https://owasp.org/www-community/vulnerabilities/Unrestricted_File_Upload)
- [Java e ClamAV](https://medium.com/faun/part1-virus-detection-service-using-clamav-and-java-48212a2e5af9)
- [Detecção de conteúdo com Apache Tika](https://tika.apache.org/0.10/detection.html)

