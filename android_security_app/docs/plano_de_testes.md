# Plano de Testes para o Aplicativo de Segurança Android

## 1. Testes do Aplicativo Android

### 1.1 Testes de Funcionalidades Básicas
- [ ] Verificar instalação e inicialização do aplicativo
- [ ] Verificar registro e login no aplicativo
- [ ] Verificar navegação entre telas
- [ ] Verificar configurações do aplicativo

### 1.2 Testes de Geolocalização
- [ ] Verificar permissões de localização
- [ ] Verificar rastreamento de localização em tempo real
- [ ] Verificar histórico de localização
- [ ] Verificar precisão das coordenadas

### 1.3 Testes de Captura de Foto
- [ ] Verificar permissões de câmera
- [ ] Verificar detecção de tentativas de senha incorreta
- [ ] Verificar captura de foto com câmera frontal
- [ ] Verificar envio de foto por e-mail

### 1.4 Testes de Detecção de Troca de SIM
- [ ] Verificar permissões de telefonia
- [ ] Verificar detecção de remoção do SIM
- [ ] Verificar detecção de novo SIM
- [ ] Verificar envio de informações do novo SIM por e-mail

### 1.5 Testes de Bloqueio Remoto
- [ ] Verificar permissões de administrador de dispositivo
- [ ] Verificar bloqueio remoto do dispositivo
- [ ] Verificar alteração remota de senha
- [ ] Verificar persistência do bloqueio após reinicialização

### 1.6 Testes de Alerta Sonoro
- [ ] Verificar permissões de áudio
- [ ] Verificar ativação remota do alerta sonoro
- [ ] Verificar volume máximo do alerta
- [ ] Verificar desativação remota do alerta

### 1.7 Testes de Controle de Ligações
- [ ] Verificar permissões de telefonia
- [ ] Verificar execução remota de ligações
- [ ] Verificar registro de ligações no histórico
- [ ] Verificar comportamento durante chamadas em andamento

## 2. Testes do Servidor Web

### 2.1 Testes de API
- [ ] Verificar endpoints de autenticação (login, registro)
- [ ] Verificar endpoints de dispositivos (listar, obter, atualizar)
- [ ] Verificar endpoints de localização (listar, obter)
- [ ] Verificar endpoints de comandos (enviar, listar)

### 2.2 Testes de Autenticação
- [ ] Verificar geração de tokens JWT
- [ ] Verificar validação de tokens
- [ ] Verificar expiração de tokens
- [ ] Verificar proteção de rotas

### 2.3 Testes de Banco de Dados
- [ ] Verificar persistência de usuários
- [ ] Verificar persistência de dispositivos
- [ ] Verificar persistência de localizações
- [ ] Verificar persistência de comandos

### 2.4 Testes de Segurança
- [ ] Verificar hash de senhas
- [ ] Verificar proteção contra injeção SQL
- [ ] Verificar proteção contra XSS
- [ ] Verificar proteção contra CSRF

## 3. Testes da Interface Web

### 3.1 Testes de Interface
- [ ] Verificar responsividade em diferentes dispositivos
- [ ] Verificar acessibilidade
- [ ] Verificar compatibilidade com navegadores
- [ ] Verificar feedback visual para ações

### 3.2 Testes de Funcionalidades
- [ ] Verificar login e registro
- [ ] Verificar visualização de dispositivos
- [ ] Verificar visualização de detalhes do dispositivo
- [ ] Verificar envio de comandos remotos

### 3.3 Testes de Mapa
- [ ] Verificar carregamento do mapa
- [ ] Verificar exibição de marcadores
- [ ] Verificar atualização de posição
- [ ] Verificar histórico de localizações

## 4. Testes de Integração

### 4.1 Testes de Comunicação
- [ ] Verificar comunicação entre aplicativo e servidor
- [ ] Verificar comunicação entre interface web e servidor
- [ ] Verificar tempo de resposta
- [ ] Verificar comportamento offline

### 4.2 Testes de Comandos Remotos
- [ ] Verificar envio de comandos da interface web para o servidor
- [ ] Verificar recebimento de comandos pelo aplicativo
- [ ] Verificar execução de comandos no dispositivo
- [ ] Verificar atualização de status após execução

### 4.3 Testes de Notificações
- [ ] Verificar envio de notificações push
- [ ] Verificar recebimento de notificações pelo aplicativo
- [ ] Verificar ações em resposta a notificações
- [ ] Verificar notificações em segundo plano

## 5. Testes de Desempenho

### 5.1 Testes de Carga
- [ ] Verificar comportamento com múltiplos dispositivos
- [ ] Verificar comportamento com múltiplos usuários
- [ ] Verificar comportamento com grande volume de localizações
- [ ] Verificar comportamento com grande volume de comandos

### 5.2 Testes de Bateria
- [ ] Verificar consumo de bateria em modo normal
- [ ] Verificar consumo de bateria com rastreamento ativo
- [ ] Verificar consumo de bateria em segundo plano
- [ ] Verificar otimizações de bateria

### 5.3 Testes de Rede
- [ ] Verificar consumo de dados
- [ ] Verificar comportamento com conexão lenta
- [ ] Verificar comportamento com conexão instável
- [ ] Verificar comportamento com diferentes tipos de rede (Wi-Fi, 4G, 5G)
