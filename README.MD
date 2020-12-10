# Securi File Upload

### Sobre
Este é um projeto da disciplina Segurança e Auditoria de Sistemas - 2020/1 do Instituto de Informática da Universidade Federal de Goiás. 


### Introdução

Upload de arquivos estão se tornando cada vez mais populares nas aplicações cotidianas. Quando criamos uma conta no banco, comumente temos que enviar uma foto que  comprove nossa identidade, de modo que ela também pode conter um documento de identificação; se estamos fazendo parte de um processo seletivo, pode ser que o envio do currículo seja necessário, geralmente no formato pdf. Entretanto, se o servidor que recebe estes arquivos não possuir controles de segurança contra ataques a sessões de upload de arquivos, o sistema ou seus usuários podem estar dispostos a grandes riscos. 

Diante disso, serão descritos neste artigo:
- Introdução de conceitos.
- Discussão sobre tipos de ataque de envio de arquivos.
- Demonstração didática de uma aplicação backend utilizando SpringBoot, ClamAV e JWT com segurança reforçada contra:
  - Upload de arquivos em sessões sem autorização.
  - Upload de arquivos grandes.
  - Upload de arquivos maliciosos.
  - Upload de arquivos com formatos não permitidos.
 
### Conceitos

##### Conceitos e definições no contexto da aplicação

- Compreende-se como  [**backend**]( https://www.alura.com.br/artigos/o-que-e-front-end-e-back-end) a representação de um servidor cujo papel é fornecer os recursos para um agente cliente, chamado de frontend. É proveniente da arquitetura Cliente-Servidor. O papel do backend é fazer com que a complexidade das aplicações seja separada do agente cliente.
- Comprende-se como  [**ClamAV**](https://www.clamav.net/) um antivírus  [open source](https://www.redhat.com/pt-br/topics/open-source/what-is-open-source) feito especialmente para o Linux, contando com versões para MacOS e Windows.
- Comprende-se como [**autenticação**](https://www.treinaweb.com.br/blog/autenticacao-x-autorizacao/#:~:text=Por%20sua%20vez%2C%20a%20autoriza%C3%A7%C3%A3o,usu%C3%A1rio%20ao%20utilizar%20uma%20aplica%C3%A7%C3%A3o.) a verificação de que uma entidade ou  objeto são válidos. No contexto de usuários, consta a validação de que o usuário existe.
- Compreende-se como [**JWT**](https://www.devmedia.com.br/como-o-jwt-funciona/40265) um método de autenticação de duas partes definido no RFC 7519.
- Compreende-se como [**autorização**](https://www.treinaweb.com.br/blog/autenticacao-x-autorizacao/#:~:text=Por%20sua%20vez%2C%20a%20autoriza%C3%A7%C3%A3o,usu%C3%A1rio%20ao%20utilizar%20uma%20aplica%C3%A7%C3%A3o.)  a verificação de privilégios de uma entidade ou objeto autenticados.


##### Conceitos de Segurança da Informação
Segundo as normas NBR 27001 e NBR 27002, sintetizadas no livro Gestão da Segurança da Informação (_COELHO, Flávia; ARAÚJO, Luis; BEZERRA, Edson, 2014_), compreende-se como:
* **Ativo**: qualquer coisa que tenha valor para organização e para seus negócios. Exemplo: pessoas, servidores, computadores, software, etc.
* **Ameaça**: qualquer evento que explore vulnerabilidades.
* **Vulnerabilidade**: qualquer fraqueza que pode ser explorada e comprometer a segurança  de sistemas ou informações. Exemplo: 
* **Risco**: combinação da probabilidade (chance da ameaça se concretizar)  de um evento ocorrer e de suas consequências para a organização.
* **Ataque**: qualquer ação que comprometa a segurança de uma organização.
    * **Modelo de ataque**:
        ![alt text](https://github.com/sabaodecoco11/securi-file-upload/blob/master/img/interrupcao.png?raw=true)
        
        
        * *Interrupção*: quando um ativo fica indisponível ou é destruído. Exemplo: ataque distribuído de negação de serviço    [(DDoS)](https://www.kaspersky.com.br/resource-center/threats/ddos-attacks).
        ![alt text](https://github.com/sabaodecoco11/securi-file-upload/blob/master/img/interceptacao.png?raw=true)
        * *Interceptação*: quando um ativo é acessado por um agente não autorizado, de modo que ***não há modificação***. Exemplo: [Spoofing](https://www.welivesecurity.com/br/2019/07/25/spoofing-entenda-a-tecnica-que-ganhou-destaque-nos-ultimos-dias/).
        ![alt text](https://github.com/sabaodecoco11/securi-file-upload/blob/master/img/modificacao.png?raw=true)
        * *Modificação*: quando um ativo é acessado por um agente não autorizado, de modo que ***há modificação***. Exemplo: [Dns hijacking; redirecionamento de DNS](https://rockcontent.com/br/blog/dns-hijacking/).
        ![alt text](https://github.com/sabaodecoco11/securi-file-upload/blob/master/img/fabricacao.png?raw=true)
        * *Fabricação*: quando um ativo tem algum dado falsificado inserido. Exemplo: [crackear](https://pt.wikipedia.org/wiki/Crack_(software)) um software pago.
  
    * **Forma de ataque**:
        * *Ativa*: quando ocorre inserção ou deleção de dados em um ativo. Exemplo: [ataque de negação de serviço](https://www.gta.ufrj.br/grad/06_1/dos/intro.html).
        * *Passiva*: quando ocorre somente escuta dos dados de um ativo, sem modificação. Exemplo: ataques de  [keylogger](https://canaltech.com.br/seguranca/O-que-e-keylogger/#:~:text=Keylogger%20%C3%A9%20um%20programa%20criado,outros%20tipos%20de%20dados%20pessoais.).
* **Incidente de segurança**: qualquer evento contrário à segurança. Exemplo: serviço de email atacado por negação de serviço.
* **Controles de segurança**: são medidas ou um conjunto de medidas adotadas para tratar vulnerabilidades e reduzir riscos de incidentes de segurança da informação.



### Alguns tipos de ataque a sessões de upload de arquivo
Existem muitos ataques que podem ser efetuados a sessões de upload de arquivo. Abaixo, segue um levantamento com alguns incidentes de segurança que podem ocorrer, baseado nos artigos [File  Upload Cheat Sheet - OWASP](https://cheatsheetseries.owasp.org/cheatsheets/File_Upload_Cheat_Sheet.html) e [Unrestricted File Upload - OWASP](https://owasp.org/www-community/vulnerabilities/Unrestricted_File_Upload)   :
| Tipo de ataque | Forma de ataque | Modelo de ataque |
| --- | --- | --- |
| [Envio de arquivos maliciosos (Cross Site Scripting, Zip Bombs etc)](https://cheatsheetseries.owasp.org/cheatsheets/File_Upload_Cheat_Sheet.html#malicious-files) | Ataque ativo | Interrupção
| [Obtenção de arquivos públicos](https://cheatsheetseries.owasp.org/cheatsheets/File_Upload_Cheat_Sheet.html#public-file-retrieval) | Ataque passivo | Interceptação
| [Obtenção de arquivos chave](https://cheatsheetseries.owasp.org/cheatsheets/File_Upload_Cheat_Sheet.html#file-storage-location) | Ataque passivo | Interceptação
| [Arquivos com dupla extensão](https://owasp.org/www-community/vulnerabilities/Unrestricted_File_Upload#Examples) | Ataque ativo | Fabricação/Modificação
| [Arquivos com tamanhos  grandes](https://owasp.org/www-community/vulnerabilities/Unrestricted_File_Upload#Examples) | Ataque ativo | Interrupção
| [Arquivos com nomes críticos](https://owasp.org/www-community/vulnerabilities/Unrestricted_File_Upload#Examples) | Ataque ativo | Fabricação/Modificação
| [Alteração do conteúdo de metadados que indicam o formato do arquivo](https://owasp.org/www-community/vulnerabilities/Unrestricted_File_Upload#Examples) | Ataque ativo | Fabricação

### Experimentação

Através de uma aplicação didática backend rodando com Spring Boot (Java 11) e ClamAV, serão demonstrados abaixo alguns casos de teste de uploads não confiáveis de arquivo.

#### Bibliotecas adicionais utilizadas (Maven)
- [jjwt](https://github.com/jwtk/jjwt) v0.9.1
- [Apache Tika](https://tika.apache.org/) v1.24
- [clamav-client](https://github.com/cdarras/clamav-client) v2.0.2

#### Requisitos para build

- [Docker](https://www.docker.com/)
- [ClamAV (Docker image)](https://hub.docker.com/r/mkodockx/docker-clamav/)
- [Java 11](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
- [Maven](https://maven.apache.org/download.cgi)

#### Passo a passo para build
- clone o projeto
- inicie ClamAV através do comando`docker run -d --net=host -p 3310:3310 mkodockx/docker-clamav`
- na raiz do projeto, execute: `mvn spring-boot:run`

#### Casos de teste

##### Preparação

Visando que [autorização e autenticação](https://devpleno.com/autenticacao-e-autorizacao/) de usuários são importantes para manter o sistema mais seguro durante sessões de upload de arquivo, o sistema didático também conta com essas funcionalidades. Portanto, é **necessário** que seja feita inicialmente uma autenticação. Caso contrário, a rota de ***upload*** retornará erro [HTTP 401](https://developer.mozilla.org/pt-BR/docs/Web/HTTP/Status/401).

Como o sistema não conta com banco de dados, o usuário padrão é _user_ e a senha é _password_, tal que quaisquer outras combinações de usuário/senha resultarão em erro. Para obter o [token de autorização JWT](https://www.devmedia.com.br/como-o-jwt-funciona/40265), execute: 

**```curl -X GET http://localhost:8082/authenticate --header "Content-Type: application/json" --data '{"username":"user", "password":"password"}'```**

De modo que a resposta será:

**`{"token":"<BEARER_JWT_TOKEN>"}`**

***Observações:***

- _O token tem 5 horas de validade._

==================================================================


##### * Upload de arquivos grandes: desperdício de recursos
Ao submeter um arquivo pdf cujo tamanho excede o limte suportado pelo servidor, através da seguinte requisição:

**```curl -i -X POST http://localhost:8082/upload --header "Authorization: <BEARER_JWT_TOKEN>" -F file=@large-pdf-file.pdf http://localhost:8082/upload```**

A resposta do servidor é:

**`{"message": "Tamanho do arquivo excede o limite definido (3MB)"}`**

==================================================================

##### * Upload de arquivos maliciosos
Ao submeter um [arquivo pdf](https://blog.didierstevens.com/2015/08/28/test-file-pdf-with-embedded-doc-dropping-eicar/) com um arquivo malicioso embutido ([EICAR.doc](https://www.eicar.org/?page_id=3950)), através da seguinte requisição: 

**```curl -i -X POST http://localhost:8082/upload --header "Authorization: <BEARER_JWT_TOKEN>" -F file=@42.zip http://localhost:8082/upload```**

A resposta do servidor é:

**`{"message": "Arquivo malicioso encontrado"}`** 

 [Resultado do scan](https://www.virustotal.com/gui/file/86a96ec03ba8242c1486456d67ee17f919128754846dbb3bdf5e836059091dba/detection) no VirusTotal.

=================================================================


##### * Arquivo com dupla extensão

Com a requisição:

**```curl -i -X POST http://localhost:8082/upload --header "Authorization: <BEARER_JWT_TOKEN>" -F file=@teste.pdf.txt http://localhost:8082/upload```**

A resposta do servidor é:

**`{"message": "Arquivo com formato inválido"}`**

=================================================================


##### * Arquivo com extensão não permitida

Ao submeter um arquivo com formato .exe, de modo que o servidor aceita somente os formatos {.txt, .pdf}:

**```curl -i -X POST http://localhost:8082/upload --header "Authorization: <BEARER_JWT_TOKEN>" -F file=@teste.exe http://localhost:8082/upload```**

A resposta do servidor é:

**`{"message": "Arquivo com formato inválido"}`**

=================================================================


##### ** Limitações
A solução com ClamAV tem algumas limitações, como, por exemplo, o upload do famoso Zip Bomb chamado [42.zip](https://www.unforgettable.dk/). ClamAV **não  foi capaz** de identificar o código malicioso:

Requisição: 

**```curl -i -X POST http://localhost:8082/upload --header "Authorization: <BEARER_JWT_TOKEN>" -F file=@42.zip http://localhost:8082/upload```**

Resposta: 

**`{"message":"OK"}`**

Os motores que detectaram o código malicioso podem ser verificados no [VirusTotal](https://www.virustotal.com/gui/file/bbd05de19aa2af1455c0494639215898a15286d9b05073b6c4817fe24b2c36fa/detection)

Neste caso, sugere-se que antivírus mais potentes ou soluções de [segurança de endpoint](https://blog.infomach.com.br/seguranca-de-endpoint-o-que-e/) sejam utilizadas, a fim de reduzir a probabilidade de incidentes de segurança.


### Referências
- [Ataques em sessões de upload de arquivo](https://owasp.org/www-community/vulnerabilities/Unrestricted_File_Upload)
- [Java e ClamAV](https://medium.com/faun/part1-virus-detection-service-using-clamav-and-java-48212a2e5af9)
- [Detecção de conteúdo com Apache Tika](https://tika.apache.org/0.10/detection.html)

