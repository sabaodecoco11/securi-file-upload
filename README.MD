# Securi File Upload

### Sobre
Este é um projeto da disciplina Segurança e Auditoria de Sistemas - 2020/1 do Instituto de Informática da Universidade Federal de Goiás. 


### Introdução

Upload de arquivos estão se tornando cada vez mais populares nas aplicações cotidianas. Quando criamos uma conta no banco, comumente temos que enviar uma foto que  comprove nossa identidade, de modo que ela também pode conter um documento de identificação; se estamos fazendo parte de um processo seletivo, pode ser que o envio do currículo seja necessário, geralmente no formato pdf, por exemplo. Entretanto, se o servidor que recebe estes arquivos não possuir controles de segurança contra ataques a sessões de upload de arquivos, o sistema ou seus usuários podem estar dispostos a grandes riscos. 

Diante disso, serão descritos neste artigo:
- Introdução de conceitos importantes da Segurança da informação, tendo como base as normas NBR 27001 e NBR 27002.
- Discussão sobre tipos de ataque de envio de arquivos.
- Discussão sobre alguns cenários de ataque a sistemas backend no que tange a upload de arquivos.
- Demonstração didática de um sistema backend com segurança reforçada contra:
  - Upload de arquivos grandes.
  - Upload de arquivos maliciosos.
  - Upload de arquivos com formatos não permitidos.
 
### Conceitos importantes sobre Segurança da Informação
Segundo as normas NBR 27001 e NBR 27002, sintetizadas no livro Gestão da Segurança da Informação (_COELHO, Flávia; ARAÚJO, Luis; BEZERRA, Edson, 2014_), compreende-se como:
* **Ativo**: qualquer coisa que tenha valor para organização e para seus negócios. Exemplo: pessoas, servidores, computadores, software, etc.
* **Ameaça**: qualquer evento que explore vulnerabilidades.
* **Vulnerabilidade**: qualquer fraqueza que pode ser explorada e comprometer a segurança  de sistemas ou informações. Exemplo: 
* **Risco**: combinação da probabilidade (chance da ameaça se concretizar)  de um evento ocorrer e de suas consequências para a organização.
* **Ataque**: qualquer ação que comprometa a segurança de uma organização.
    * **Modelo de ataque**:
        * *Interrupção*: quando um ativo fica indisponível ou é destruído. Exemplo: ataque distribuído de negação de serviço    [(DDoS)](https://www.kaspersky.com.br/resource-center/threats/ddos-attacks).
        * *Interceptação*: quando um ativo é acessado por um agente não autorizado, de modo que ***não há modificação***. Exemplo: [ARP Spoofing](https://www.welivesecurity.com/br/2019/07/25/spoofing-entenda-a-tecnica-que-ganhou-destaque-nos-ultimos-dias/).
        * *Modificação*: quando um ativo é acessado por um agente não autorizado, de modo que ***há modificação***. Exemplo: [redirecionamento de dns](https://rockcontent.com/br/blog/dns-hijacking/).
        * *Fabricação*: quando um ativo tem algum dado falsificado inserido. Exemplo: [crackear](https://pt.wikipedia.org/wiki/Crack_(software)) um software pago.
  
    * **Forma de ataque**:
        * *Ativa*: quando ocorre inserção ou deleção de dados em um ativo. Exemplo: [ataque de negação de serviço](https://www.gta.ufrj.br/grad/06_1/dos/intro.html).
        * *Passiva*: quando ocorre somente escuta dos dados de um ativo, sem modificação. Exemplo: ataques de  [keylogger](https://canaltech.com.br/seguranca/O-que-e-keylogger/#:~:text=Keylogger%20%C3%A9%20um%20programa%20criado,outros%20tipos%20de%20dados%20pessoais.).
* **Incidente de segurança**: qualquer evento contrário à segurança. Exemplo: serviço de email atacado por negação de serviço.
* **Controles de segurança**: são medidas ou um conjunto de medidas adotadas para tratar vulnerabilidades e reduzir riscos de incidentes de segurança da informação.



### Alguns tipos de ataque a sessões de upload de arquivo
Existem muitos ataques que podem ser efetuados a sessões de upload de arquivo. Abaixo, segue um levantamento com alguns incidentes de segurança que podem ocorrer:
| Tipo de ataque | Forma de ataque | Modelo de ataque |
| --- | --- | --- |
| [Envio de arquivos maliciosos (Cross Site Scripting, Zip Bombs etc)](https://cheatsheetseries.owasp.org/cheatsheets/File_Upload_Cheat_Sheet.html#malicious-files) | Ataque ativo | Interrupção/Modificação
| [Obtenção de arquivos públicos](https://cheatsheetseries.owasp.org/cheatsheets/File_Upload_Cheat_Sheet.html#public-file-retrieval) | Ataque passivo | Interceptação
| [Obtenção de arquivos chave]() | Ataque passivo | Interceptação
| [Arquivos com dupla extensão](https://owasp.org/www-community/vulnerabilities/Unrestricted_File_Upload#Examples) | Ataque ativo | Fabricação
| [Arquivos com tamanhos  grandes](https://owasp.org/www-community/vulnerabilities/Unrestricted_File_Upload#Examples) | Ataque ativo | Interrupção
| [Arquivos com nomes críticos](https://owasp.org/www-community/vulnerabilities/Unrestricted_File_Upload#Examples) | Ataque ativo | Fabricação/Modificação
| [Alteração do conteúdo de metadados que indicam o formato do arquivo](https://owasp.org/www-community/vulnerabilities/Unrestricted_File_Upload#Examples) | Ataque ativo | Fabricação/Modificação


### Avaliação de cenários

#### Upload de arquivos grandes: desperdício de recursos e ataque contra disponibilidade
João Bad Guy é um usuário mal intencionado de um ambiente virtual de aprendizagem. Em uma turma que ele faz parte, o tamanho máximo para upload de arquivo é de 200MB, de modo que podem ser anexados 5 arquivos em cada tarefa. Dispondo dessas informações, João envia 5 arquivos de 200MB a 20 tarefas de diferentes módulos. Ao todo, foram ocupados 20GB de armazenamento no servidor. Após alguns minutos, percebe-se que o sistema está fora do ar.

Envio de arquivos grandes é um ataque clássico de negação de serviço. Além disso, se aliado a uma [rede de bots](https://www.kaspersky.com.br/resource-center/threats/botnet-attacks), os danos podem ser piores.

Para tanto, algumas soluções podem ser adotadas:
- Implementação de testes cognitivos para distinguir humanos de máquinas, como, por exemplo, [CAPTCHA](https://support.google.com/a/answer/1217728?hl=pt-BR).
- Uso de sistema de [autorização e autenticação](https://www.ibm.com/support/pages/understanding-application-authentication-and-authorization-security).
- Ajuste fino do [limite de download e upload](https://cheatsheetseries.owasp.org/cheatsheets/File_Upload_Cheat_Sheet.html#upload-and-download-limits) dos arquivos, no servidor.
- Se necessário, utilizar um serviço de proteção contra [DDoS](https://www.kaspersky.com.br/resource-center/threats/ddos-attacks).

#### Upload de arquivos maliciosos
Joana Bad Girl é uma usuária de um fórum online sobre música. Ela percebeu que o sistema permite enviar arquivos no formato *zip*. Sendo assim, Joana envia um arquivo malicioso do tipo [zip-bomb](https://pt.wikipedia.org/wiki/Bomba_Zip) (um arquivo zip que quando descompactado, consegue se expandir para tamanhos muito grandes, na ordem de petabytes) a um sub fórum de sua preferência, dizendo que nele haveriam coletâneas de música gospel.
Marcela Good Girl acha interessante a postagem de Joana e decide baixar o arquivo zip. Ao extraí-lo, em alguns segundos o sistema para de responder. Desesperada, Marcela resolve reiniciar o sistema, mas constata que o mesmo está extremamente lento, inoperável. Carlos, um profissional de T.I., constata que o sistema foi atingido por um ataque de zip bomb, e deleta a pasta que contém o arquivo grande. Carlos recomenda que ela tenha cuidado ao baixar arquivos zip.

Códigos maliciosos podem ser embutidos nos arquivos dos mais diversos formatos. Quando esses arquivos podem ser compartilhados entre vários usuários ou fluxos, a situação fica ainda pior.
No formato PDF, atacantes podem [disfarçar códigos em JavaScript](https://www.sentinelone.com/blog/malicious-pdfs-revealing-techniques-behind-attacks/) nos objetos do arquivo. Desta forma, ao ser aberto no leitor, pode desempenhar as mais diversas ações, como envio de form’s, ataques ao navegador de internet, etc.

No formato html, é possível embutir códigos maliciosos em sites confiáveis a partir do momento em que um código html maliciosos é chamado por uma fonte não confiável. Trata-se do ataque chamado  [XSS Scripting ou Html Injection](https://owasp.org/www-community/attacks/xss/).

Para tanto, algumas [soluções](https://owasp.org/www-community/vulnerabilities/Unrestricted_File_Upload) podem ser adotadas:
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

