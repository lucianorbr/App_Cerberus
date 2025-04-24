# README - SecureGuard: Aplicativo de Segurança Android

## Visão Geral

O SecureGuard é uma solução completa de segurança para dispositivos Android, inspirada no Cerberus, que oferece proteção contra roubo ou perda do dispositivo. O sistema é composto por três componentes principais:

1. **Aplicativo Android em Kotlin**: Instalado no dispositivo móvel, responsável por monitorar e executar comandos de segurança.
2. **Servidor Web em Kotlin/Ktor**: Gerencia a comunicação entre o aplicativo e a interface web, armazena dados e processa comandos.
3. **Interface Web em React/TypeScript**: Permite ao usuário controlar remotamente o dispositivo através de um navegador.

## Funcionalidades Principais

### Geolocalização
- Rastreamento de localização em tempo real
- Histórico de localização com visualização em mapa
- Registro de coordenadas precisas

### Captura de Foto em Tentativas de Senha Incorreta
- Detecção de tentativas de desbloqueio malsucedidas
- Captura automática de foto com a câmera frontal
- Envio da foto por e-mail para o endereço configurado

### Detecção de Troca de SIM
- Monitoramento do estado do SIM card
- Detecção de troca de chip com coleta de informações do novo SIM
- Notificação automática por e-mail

### Bloqueio Remoto
- Controle remoto do bloqueio de tela
- Alteração remota de senha
- Funcionalidades avançadas de administrador de dispositivo

### Alerta Sonoro Remoto
- Reprodução de som de alarme em volume máximo
- Controle remoto para iniciar e parar o alerta
- Configurações de volume personalizáveis

### Controle de Ligações
- Execução remota de chamadas telefônicas
- Verificação do estado atual de chamadas
- Controle completo das funcionalidades de telefonia

## Estrutura do Projeto

```
android_security_app/
├── app/                      # Aplicativo Android
│   ├── build.gradle          # Configuração do Gradle
│   └── src/                  # Código-fonte do aplicativo
│       ├── main/
│           ├── java/         # Código Kotlin
│           └── res/          # Recursos do aplicativo
├── server/                   # Servidor Web
│   ├── build.gradle.kts      # Configuração do Gradle
│   └── src/                  # Código-fonte do servidor
│       ├── main/
│           └── kotlin/       # Código Kotlin
├── web/                      # Interface Web
│   ├── package.json          # Dependências e scripts
│   └── src/                  # Código-fonte da interface
│       ├── components/       # Componentes React
│       ├── pages/            # Páginas da aplicação
│       └── services/         # Serviços de API
└── docs/                     # Documentação
    ├── guia_implementacao.md # Guia de implementação
    ├── manual_usuario.md     # Manual do usuário
    └── plano_de_testes.md    # Plano de testes
```

## Documentação

O projeto inclui documentação detalhada para implementação, uso e testes:

- **Guia de Implementação**: Instruções técnicas para desenvolvedores sobre como implementar e configurar cada componente do sistema.
- **Manual do Usuário**: Guia completo para usuários finais sobre como instalar, configurar e utilizar o aplicativo e a interface web.
- **Plano de Testes**: Estratégia detalhada para testar todas as funcionalidades do sistema, garantindo qualidade e confiabilidade.

## Tecnologias Utilizadas

### Aplicativo Android
- Kotlin
- Android Jetpack (ViewModel, LiveData, Room)
- Google Play Services (Maps, Location)
- Firebase Cloud Messaging
- CameraX API
- Material Design Components

### Servidor Web
- Kotlin
- Ktor Framework
- Exposed (ORM)
- PostgreSQL / H2 Database
- JWT Authentication
- Firebase Admin SDK

### Interface Web
- React
- TypeScript
- Material UI
- Google Maps API
- Axios
- React Router

## Requisitos do Sistema

### Aplicativo Android
- Android 8.0 (API 26) ou superior
- Google Play Services
- Permissões: Localização, Câmera, Telefonia, Administrador de Dispositivo, Internet

### Servidor Web
- JDK 11 ou superior
- Banco de dados SQL (PostgreSQL recomendado para produção)
- Servidor com suporte a Kotlin/JVM

### Interface Web
- Navegador moderno com suporte a JavaScript ES6+
- Conexão com internet

## Instalação e Configuração

Consulte o [Guia de Implementação](docs/guia_implementacao.md) para instruções detalhadas sobre como instalar e configurar cada componente do sistema.

## Uso

Consulte o [Manual do Usuário](docs/manual_usuario.md) para instruções detalhadas sobre como utilizar o aplicativo e a interface web.

## Testes

Consulte o [Plano de Testes](docs/plano_de_testes.md) para informações sobre como testar todas as funcionalidades do sistema.

## Segurança e Privacidade

O SecureGuard foi projetado com foco na segurança e privacidade:

- Todas as comunicações são criptografadas (HTTPS/SSL)
- Autenticação segura com tokens JWT
- Senhas armazenadas com hash seguro (BCrypt)
- Dados sensíveis criptografados no banco de dados
- Permissões mínimas necessárias no dispositivo Android
- Controle de acesso baseado em propriedade do dispositivo

## Contribuição

Este projeto foi desenvolvido como uma solução personalizada. Para contribuições ou adaptações, entre em contato com os desenvolvedores.

## Licença

Este projeto é proprietário e não está disponível para redistribuição sem autorização expressa.

---

Desenvolvido por SecureGuard Team © 2025
