# Configuração do Ambiente de Desenvolvimento

## Ambiente de Desenvolvimento Android

Para desenvolver o aplicativo Android de segurança em Kotlin, precisamos configurar o seguinte ambiente:

### 1. Android Studio

O Android Studio é o ambiente de desenvolvimento integrado (IDE) oficial para o desenvolvimento de aplicativos Android.

**Requisitos do sistema:**
- Windows: Microsoft Windows 8/10/11 (64-bit)
- Mac: macOS 10.14 (Mojave) ou superior
- Linux: GNOME ou KDE desktop

**Componentes necessários:**
- JDK (Java Development Kit) 8 ou superior
- SDK do Android (será instalado com o Android Studio)
- Emulador Android ou dispositivo físico para testes

### 2. Configuração do Projeto Android

Após a instalação do Android Studio, precisamos configurar um novo projeto com as seguintes especificações:

**Configurações do projeto:**
- Nome do aplicativo: SecureGuard
- Pacote: com.secureguard.app
- Linguagem: Kotlin
- Versão mínima do SDK: API 21 (Android 5.0 Lollipop)
- Versão alvo do SDK: API 34 (Android 14)

**Dependências necessárias:**
```gradle
// Localização
implementation 'com.google.android.gms:play-services-location:21.0.1'
implementation 'com.google.android.gms:play-services-maps:18.1.0'

// Firebase
implementation platform('com.google.firebase:firebase-bom:32.3.1')
implementation 'com.google.firebase:firebase-analytics-ktx'
implementation 'com.google.firebase:firebase-messaging-ktx'
implementation 'com.google.firebase:firebase-auth-ktx'
implementation 'com.google.firebase:firebase-firestore-ktx'

// CameraX
implementation 'androidx.camera:camera-core:1.3.0'
implementation 'androidx.camera:camera-camera2:1.3.0'
implementation 'androidx.camera:camera-lifecycle:1.3.0'
implementation 'androidx.camera:camera-view:1.3.0'

// Coroutines
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3'
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'

// JavaMail para envio de e-mails
implementation 'com.sun.mail:android-mail:1.6.7'
implementation 'com.sun.mail:android-activation:1.6.7'

// Lifecycle
implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.6.2'
implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2'
implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.6.2'

// Material Design
implementation 'com.google.android.material:material:1.10.0'

// Retrofit para comunicação com o servidor
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
implementation 'com.squareup.okhttp3:logging-interceptor:4.11.0'
```

### 3. Configuração do Firebase

O Firebase será utilizado para autenticação, armazenamento de dados e mensagens em tempo real.

**Passos para configuração:**
1. Criar uma conta no Firebase (console.firebase.google.com)
2. Criar um novo projeto no Firebase
3. Adicionar o aplicativo Android ao projeto Firebase
4. Baixar o arquivo de configuração google-services.json
5. Adicionar o arquivo ao diretório app/ do projeto
6. Configurar as regras de segurança para o Firestore e Storage

## Ambiente de Desenvolvimento do Servidor Web

Para desenvolver o servidor web que gerenciará o acesso remoto ao dispositivo, utilizaremos Kotlin com o framework Ktor.

### 1. IntelliJ IDEA

O IntelliJ IDEA é a IDE recomendada para desenvolvimento Kotlin no lado do servidor.

**Requisitos do sistema:**
- Windows: Microsoft Windows 8/10/11 (64-bit)
- Mac: macOS 10.14 (Mojave) ou superior
- Linux: GNOME ou KDE desktop

**Componentes necessários:**
- JDK (Java Development Kit) 8 ou superior
- Kotlin Plugin (geralmente já vem instalado)

### 2. Configuração do Projeto Ktor

**Configurações do projeto:**
- Nome do projeto: SecureGuardServer
- Linguagem: Kotlin
- Build System: Gradle

**Dependências necessárias:**
```gradle
// Ktor
implementation 'io.ktor:ktor-server-core:2.3.5'
implementation 'io.ktor:ktor-server-netty:2.3.5'
implementation 'io.ktor:ktor-server-content-negotiation:2.3.5'
implementation 'io.ktor:ktor-serialization-gson:2.3.5'
implementation 'io.ktor:ktor-server-auth:2.3.5'
implementation 'io.ktor:ktor-server-auth-jwt:2.3.5'
implementation 'io.ktor:ktor-server-cors:2.3.5'
implementation 'io.ktor:ktor-server-websockets:2.3.5'

// Firebase Admin SDK
implementation 'com.google.firebase:firebase-admin:9.2.0'

// Coroutines
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3'

// Logging
implementation 'ch.qos.logback:logback-classic:1.4.11'

// Database
implementation 'org.jetbrains.exposed:exposed-core:0.44.0'
implementation 'org.jetbrains.exposed:exposed-dao:0.44.0'
implementation 'org.jetbrains.exposed:exposed-jdbc:0.44.0'
implementation 'com.h2database:h2:2.2.224'
```

### 3. Configuração do Firebase Admin SDK

Para que o servidor possa se comunicar com o Firebase e enviar mensagens para os dispositivos, precisamos configurar o Firebase Admin SDK.

**Passos para configuração:**
1. No console do Firebase, ir para Configurações do Projeto > Contas de serviço
2. Gerar uma nova chave privada
3. Baixar o arquivo JSON da chave privada
4. Adicionar o arquivo ao diretório de recursos do projeto do servidor
5. Configurar o SDK Admin no código do servidor

## Configuração do Ambiente de Desenvolvimento Frontend Web

Para a interface web de controle remoto, utilizaremos React com TypeScript.

### 1. Node.js e npm

Node.js e npm são necessários para o desenvolvimento frontend.

**Requisitos:**
- Node.js versão 18.x ou superior
- npm versão 9.x ou superior

### 2. Configuração do Projeto React

**Configurações do projeto:**
- Nome do projeto: secure-guard-web
- Framework: React
- Linguagem: TypeScript
- Gerenciador de pacotes: npm

**Dependências necessárias:**
```json
{
  "dependencies": {
    "@emotion/react": "^11.11.1",
    "@emotion/styled": "^11.11.0",
    "@mui/icons-material": "^5.14.16",
    "@mui/material": "^5.14.17",
    "axios": "^1.6.0",
    "firebase": "^10.5.2",
    "leaflet": "^1.9.4",
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-leaflet": "^4.2.1",
    "react-router-dom": "^6.18.0",
    "react-scripts": "5.0.1",
    "typescript": "^4.9.5"
  }
}
```

## Ferramentas Adicionais

### 1. Git

Git será utilizado para controle de versão do código.

**Configuração:**
```bash
git init
git config --global user.name "Seu Nome"
git config --global user.email "seu.email@exemplo.com"
```

### 2. Postman

Postman será utilizado para testar as APIs do servidor.

### 3. Firebase CLI

Firebase CLI será utilizado para gerenciar o projeto Firebase e fazer deploy do frontend.

**Instalação:**
```bash
npm install -g firebase-tools
firebase login
```

## Estrutura do Projeto

### Aplicativo Android
```
app/
├── src/
│   ├── main/
│   │   ├── java/com/secureguard/app/
│   │   │   ├── activities/
│   │   │   ├── services/
│   │   │   ├── receivers/
│   │   │   ├── utils/
│   │   │   ├── models/
│   │   │   └── api/
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   ├── values/
│   │   │   ├── drawable/
│   │   │   └── xml/
│   │   └── AndroidManifest.xml
│   └── test/
├── build.gradle
└── proguard-rules.pro
```

### Servidor Web
```
server/
├── src/
│   ├── main/
│   │   ├── kotlin/com/secureguard/server/
│   │   │   ├── routes/
│   │   │   ├── models/
│   │   │   ├── services/
│   │   │   ├── utils/
│   │   │   └── Application.kt
│   │   └── resources/
│   │       ├── application.conf
│   │       └── logback.xml
│   └── test/
└── build.gradle
```

### Frontend Web
```
web/
├── public/
├── src/
│   ├── components/
│   ├── pages/
│   ├── services/
│   ├── utils/
│   ├── App.tsx
│   └── index.tsx
├── package.json
└── tsconfig.json
```

## Próximos Passos

Após a configuração do ambiente de desenvolvimento, os próximos passos serão:

1. Criar a estrutura básica do aplicativo Android
2. Implementar as funcionalidades de segurança no aplicativo
3. Desenvolver o servidor web para gerenciamento
4. Criar a interface web para controle remoto
5. Testar e validar a solução completa
