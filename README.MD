# Securi File Upload

## Sobre
Este é um projeto da disciplina Segurança e Auditoria de Sistemas - 2020/1 do Instituto de Informática da Universidade Federal de Goiás.


## Introdução

Upload de arquivos estão se tornando cada vez mais populares nas aplicações cotidianas. Quando criamos uma conta no banco, comumente temos que enviar uma foto que  comprove nossa identidade, de modo que ela também pode conter um documento de identificação; se estamos fazendo parte de um processo seletivo, pode ser que o envio do currículo seja necessário, geralmente no formato pdf. Entretanto, se o servidor que recebe estes arquivos não possuir controles de segurança contra ataques a sessões de upload de arquivos, o sistema ou seus usuários podem estar dispostos a grandes riscos.

Diante disso, serão descritos neste artigo:
- Introdução de conceitos.
- Discussão sobre tipos de ataque de envio de arquivos.
- Demonstração didática de uma aplicação backend utilizando SpringBoot, ClamAV e JWT com segurança reforçada contra:
    - Upload de arquivos em sessões não autenticadas.
    - Upload de arquivos grandes.
    - Upload de arquivos maliciosos.
    - Upload de arquivos com formatos não permitidos.

## Conceitos

###### Microsserviços
Correspondem a uma arquitetura e abordagem de criação de aplicações cujo conceito é dividir as funções da aplicação em componentes, de modo que esses componentes em conjunto operam para entregar as funcionalidades. Por exemplo, a funcionalidade de login em um sistema pode conter os seguintes componentes: redefinição de senha, cadastro e autenticação.

###### Arquitetura cliente-servidor
A arquitetura cliente-servidor é uma arquitetura distribuída cujo funcionamento provém da divisão do processamento de dados entre duas partes, o servidor e o cliente. O cliente inicialmente envia uma mensagem de solicitação para um ou mais servidores, de modo que o servidor retorna as solicitações do cliente através de seus serviços.

###### Backend
Representação de um servidor cujo papel é fornecer os recursos para um agente cliente, chamado de frontend. É proveniente da arquitetura Cliente-Servidor. O papel do backend é fazer com que a complexidade das aplicações seja separada do agente cliente.

###### Autenticação
Autenticação trata-se da verificação de que uma entidade ou objeto são válidos. No contexto de usuários, por exemplo, consta a validação de que o usuário existe, com as credenciais fornecidas na autenticação.

###### Autorização
A verificação de privilégios de uma entidade ou objeto que já foram autenticados.

#### Segurança da Informação

Em uma organização, qualquer coisa que agregue valor a ela e seus negócios é compreendida como um **ativo**, tais como computadores, servidores, softwares, pessoas, processos etc.

Por mais valioso que o ativo seja, é necessário tomar alguns cuidados para que ele não caia em mãos erradas. Portanto, é comum que existam **vulnerabilidades** inerentes a ele, fraquezas que podem ser exploradas a fim de comprometerem quaisquer funcionalidades. Quando uma vulnerabilidade é de fato explorada, ocorre uma **ameaça**. Quando são combinadas a probabilidade da ameaça se concretizar e suas consequências, tem-se o **risco**. As consequências provêm dos **ataques**, estes que possuem **modelo** e **forma de ataque**:

* Modelo de ataque:

    * Interrupção: quando um ativo fica indisponível ou é destruído. Exemplo: ataque distribuído de negação de serviço  [(DDoS)](https://www.kaspersky.com.br/resource-center/threats/ddos-attacks), onde um computador mestre orquestra múltiplos ataque s de negação a partir de computadores zumbis. Desta forma, pacotes enviados de um agente fonte não conseguem chegar ao agente destino.

      ![alt text](https://github.com/sabaodecoco11/securi-file-upload/blob/master/img/interrupcao.png?raw=true)

    * **Interceptação**: quando um ativo é acessado por um agente não autorizado, de modo que ***não há modificação***. Exemplo: [Man in the Middle](https://minutodaseguranca.blog.br/o-que-e-um-ataque-do-tipo-man-in-the-midle/), onde um usuário malicioso intercepta requisições enviadas por um agente fonte a um agente destino.

      ![alt text](https://github.com/sabaodecoco11/securi-file-upload/blob/master/img/interceptacao.png?raw=true)

    * **Modificação**: quando um ativo é acessado por um agente não autorizado, de modo que ***há modificação***. Exemplo: [Dns hijacking; redirecionamento de DNS](https://rockcontent.com/br/blog/dns-hijacking/), cujo funcionamento baseia-se no redirecionamento dos domínios de uma máquina do usuário (fonte) a partir de um agente malicioso (malware, por exemplo), de modo que o destino da resolução de endereço é alterado.

      ![alt text](https://github.com/sabaodecoco11/securi-file-upload/blob/master/img/modificacao.png?raw=true)

    * **Fabricação**: quando um ativo tem algum dado falsificado inserido. Exemplo: [crackear](https://pt.wikipedia.org/wiki/Crack_(software)) um software pago. Neste caso, um agente malicioso insere registros em um programa (destino) a fim de torná-lo em uma versão paga.

      ![alt text](https://github.com/sabaodecoco11/securi-file-upload/blob/master/img/fabricacao.png?raw=true)

* **Forma de ataque**:
    * **Ativa**: quando ocorre inserção ou deleção de dados em um ativo. Os modelos de ataque de modificação, fabricação e interrupção são ataques ativos.
    * **Passiva**: quando ocorre somente escuta dos dados de um ativo, sem modificação. Ataques do modelo de interceptação são passivos.

Qualquer evento contrário à segurança é classificado como **incidente de segurança**. Deste modo, é necessário aplicar constantemente **controles de segurança**, conjuntos de medidas adotadas para tratar vulnerabilidades e reduzir riscos nos sistemas.



### Alguns tipos de ataque a sessões de upload de arquivo
Existem muitos ataques que podem ser efetuados a sessões de upload de arquivo. Neste [artigo](https://owasp.org/www-community/vulnerabilities/Unrestricted_File_Upload) da OWASP, é possível encontrar uma lista completa com vários tipos e categorias de ataque.  Abaixo, serão discutidos 5 tipos comuns:

##### Envio de arquivos maliciosos
Quando um arquivo malicioso é enviado em algum fluxo de upload, o sistema e seus usuários estão expostos a grande risco. Dentre os 3 tipos de arquivos mais perigosos, estão:
* **Arquivos Zip e RAR**: Nestes arquivos, atacantes comumente camuflam códigos maliciosos, os famosos *malwares*. Por exemplo, no Dia dos Namorados, foram enviados arquivos zip com nome *Love_You0891* (os dígitos podiam mudar)  que continham o *ransomware* [**GrandCrab**](https://www.kaspersky.com.br/blog/gandcrab-ransomware-is-back/11547/) cujo intuito era extorquir as pessoas cujos computadores foram infectados.
* **Documentos do pacote Office**: Estes são os arquivos [mais visados para ataques no sistema operacional Windows](https://www.csoonline.com/article/3390221/microsoft-office-now-the-most-targeted-platform-as-browser-security-improves.html), isso porque o pacote Office permite que sejam criados *macros*, sequências de comandos programados em [Visual Basic for Applications](https://pt.wikipedia.org/wiki/Visual_Basic_for_Applications) que podem fazer estragos graves no sistema, como extração de arquivos em diretórios importantes, exclusão de arquivos, download de *malwares*, dentre outros.
* **Arquivos PDF**: Arquivos PDF parecem inofensivos para muitas pessoas. Ora, eles contém somente arquivos de leitura, não é? Errado. Códigos em JavaScript maliciosos podem ser [embutidos](https://helpx.adobe.com/acrobat/using/pdf-properties-metadata.html) neles, o que é preocupante, já que comumente arquivos PDF são abertos em navegadores de internet por padrão. Além disso, é possível anexar arquivos dos mais diversos formatos nas seções do PDF, o que aumenta bastante os estragos em um sistema.

Configura-se como ataque ativo de interrupção, podendo também ser classificado como ataque passivo de interceptação, se for disseminado um spyware, por exemplo.

##### Obtenção de arquivos públicos
O upload de arquivos públicos em um sistema é preocupante se não existirem controles de segurança de tratamento de arquivos. Se um arquivo infectado for enviado a um fluxo que é público, multiplos usuários podem estar comprometidos. É um ataque comde interrupção.

##### Arquivos com dupla extensão
Neste caso, atacantes disfarçam arquivos que seriam bloqueados no ato de upload ao adicionarem uma extensão permitida ao final do arquivo. Exemplo: *teste.exe.pdf*. É um ataque ativo de fabricação.

##### Arquivos com tamanhos  grandes
Quando upload de arquivos com tamanhos grandes é permitido, atacantes podem explorar ataques de negação de serviço enviando multiplos arquivos massivos, de modo que a capacidade de armazenamento do sistema de arquivos pode chegar ao limite. É um ataque ativo de interrupção.

##### Alteração do conteúdo de metadados que indicam o formato do arquivo
Neste tipo de ataque, ocorre adulteração dos metadados  do arquivo na requisição HTTP de upload, no cabeçalho `Content-Type`. Atacantes utilizam essa abordagem para enviar arquivos não permitidos no sistema. É um ataque ativo de fabricação.

### Experimentação
Visando demonstrar didaticamente um sistema backend com segurança reforçada contra upload em sessões não autenticadas, upload de arquivos grandes, upload de arquivos maliciosos e upload de arquivos com formatos não permitidos, abaixo seguem as tecnologias utilizadas, passo a passo para build do projeto e casos de teste.

#### Fluxograma
![alt text](https://github.com/sabaodecoco11/securi-file-upload/blob/master/img/fluxograma.png?raw=true)

#### Tecnologias utilizadas
A fim de demonstrar os casos de teste, foram utilizadas as seguintes tecnologias:

##### Spring Boot
[**Spring-Boot**](https://blog.geekhunter.com.br/tudo-o-que-voce-precisa-saber-sobre-o-spring-boot/#:~:text=O%20Spring%20Boot%20%C3%A9%20uma,que%20utilizem%20o%20ecossistema%20Spring.&text=Com%20configura%C3%A7%C3%B5es%20r%C3%A1pidas%2C%20voc%C3%AA%20consegue,o%20Thymeleaf%20como%20template%20engine.) é um framework de desenvolvimento de aplicações baseado em Java cujo objetivo é prover um ecossistema simples e autoconfigurável com suporte a arquitetura de microsserviços. É muito utilizado em aplicações web para prover o backend.

##### ClamAV
[**ClamAV**](https://www.clamav.net/) é um antivírus  [open source](https://www.redhat.com/pt-br/topics/open-source/what-is-open-source) feito especialmente para o Linux, contando também versões para MacOS e Windows.

##### JWT
JWT (JSON Web Token) é um padrão de transferência de dados entre duas partes possibilitando assinatura e criptografia, definido no RFC 7519.
Quando aplicado a estrutura de autenticação, seu funcionamento ocorre basicamente da seguinte forma:
*  São enviados dados de autenticação de um certo objeto ou entidade a partir de __agente fonte__(geralmente, um cliente) para um __agente autenticador__(geralmente, um servidor). Comumente, no contexto de login de usuário, são enviados email e senha.
*  O __agente autenticador__ verifica se os dados estão válidos e correspodem a uma instância de alguma entidade ou objeto. Se os dados estiverem ok, ele criará um token JWT [assinado](https://pt.wikipedia.org/wiki/Assinatura_digital), podendo também ser criptografado, e posteriormente codificado em [Base64](https://pt.wikipedia.org/wiki/Base64) com alguns campos especiais (data de expiração do token, papel do usuário no sistema, dentre outros), de modo que a verificação de assinatura ou descriptografia são realizadas mediante apresentação da chave secreta, conhecida intrinsecamente pelo __autenticador__. Este token será posteriormente encapsulado em *Notação de Objeto Javascript* ([JSON](https://www.devmedia.com.br/json-tutorial/25275)) para então ser retornado ao __agente fonte__.
*  A **autorização** é verificada mediante a extração de quaisquer campos relacionados ao papel da entidade ou objeto, informados no token jwt.
*  Por exemplo, tendo em mãos o seguinte token **`
eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE2MDc4ODA2NDcsImV4cCI6MTYzOTQxNzAwOCwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoianJvY2tldEBleGFtcGxlLmNvbSIsIkdpdmVuTmFtZSI6IkpvaG5ueSIsIlN1cm5hbWUiOiJSb2NrZXQiLCJFbWFpbCI6Impyb2NrZXRAZXhhbXBsZS5jb20iLCJSb2xlIjpbIk1hbmFnZXIiLCJQcm9qZWN0IEFkbWluaXN0cmF0b3IiXX0.-nIAjxrlNRmwqrIxcTwT_D8alnQpD-geE13McFxpAM4`**  criado no website [jwtbuilder]( http://jwtbuilder.jamiekurtz.com/), podemos verificar os campos presentes nele:![alt text](https://github.com/sabaodecoco11/securi-file-upload/blob/master/img/jwt_token.png?raw=true)

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
- obtenha a imagem docker do clamav, através do comando `docker pull mkodockx/docker-clamav`
- inicie ClamAV através do comando `docker run -d --net=host -p 3310:3310 mkodockx/docker-clamav`
- na raiz do projeto, execute: `mvn spring-boot:run`

#### Casos de teste

##### Preparação

Após ter feito build, para executar os passos de teste, será necessário enviar requisições http para aplicação. Nas requisições dos exemplos abaixo, será utilizado [cURL](https://www.hostinger.com.br/tutoriais/comando-curl-linux/), uma ferramenta de linha de comando presente na maior parte dos sistemas operacionais modernos.

##### Autenticação: passo necessário

Visando que autorização e autenticação de usuários são importantes para manter o sistema mais seguro durante sessões de upload de arquivo, o sistema didático também conta com essas funcionalidades. Portanto, é necessário que seja feita inicialmente uma autenticação. Caso contrário, a rota de upload retornará erro HTTP 401, pois não haverá autorização de execução.

Como o sistema não conta com banco de dados, o usuário padrão é _user_ e a senha é _password_, tal que quaisquer outras combinações de usuário/senha resultarão em erro. Para obter o [token JWT](https://www.devmedia.com.br/como-o-jwt-funciona/40265), execute:

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

**```curl -i -X POST http://localhost:8082/upload --header "Authorization: <BEARER_JWT_TOKEN>" -F file=@pdf-doc-vba-eicar-dropper.pdf http://localhost:8082/upload```**

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

Neste caso, sugere-se que antivírus mais potentes ou soluções de [segurança de endpoint](https://blog.infomach.com.br/seguranca-de-endpoint-o-que-e/) sejam utilizadas.


### Referências
-[Microsserviços](https://www.redhat.com/pt-br/topics/microservices/what-are-microservices)
-[Arquitetura cliente-servidor](https://www.gta.ufrj.br/ensino/eel878/redes1-2016-1/16_1/p2p/modelo.html)
-[Backend](https://www.alura.com.br/artigos/o-que-e-front-end-e-back-end)
-[Autenticação](https://www.treinaweb.com.br/blog/autenticacao-x-autorizacao/#:~:text=Por%20sua%20vez%2C%20a%20autoriza%C3%A7%C3%A3o,usu%C3%A1rio%20ao%20utilizar%20uma%20aplica%C3%A7%C3%A3o.)
-[Autorização](https://www.treinaweb.com.br/blog/autenticacao-x-autorizacao/#:~:text=Por%20sua%20vez%2C%20a%20autoriza%C3%A7%C3%A3o,usu%C3%A1rio%20ao%20utilizar%20uma%20aplica%C3%A7%C3%A3o.)
-[Conceitos de Segurança da Informação](https://esr.rnp.br/gti35)
-[JWT](https://www.devmedia.com.br/como-o-jwt-funciona/40265)
-[Ataques em sessões de upload de arquivo](https://owasp.org/www-community/vulnerabilities/Unrestricted_File_Upload)
-[Java e ClamAV](https://medium.com/faun/part1-virus-detection-service-using-clamav-and-java-48212a2e5af9)
-[Detecção de conteúdo com Apache Tika](https://tika.apache.org/0.10/detection.html)

