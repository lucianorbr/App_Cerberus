# Pesquisa sobre Desenvolvimento Android em Kotlin

## Introdução ao Kotlin para Android

Kotlin é uma linguagem de programação moderna, concisa e segura que é oficialmente suportada para o desenvolvimento Android desde 2017. Algumas características importantes do Kotlin incluem:

- **Código robusto**: Kotlin distingue entre tipos anuláveis e não-anuláveis, o que ajuda a capturar mais erros em tempo de compilação.
- **Plataforma madura**: Kotlin existe desde 2011 e atingiu a versão 1.0 em 2016.
- **Código conciso e legível**: Kotlin elimina código boilerplate como getters e setters.
- **Interoperabilidade com Java**: Kotlin é totalmente compatível com código Java existente.

## Funcionalidades de Segurança Solicitadas

### 1. Geolocalização

Para implementar a funcionalidade de geolocalização no Android usando Kotlin, podemos utilizar:

- **Google Maps API**: Para exibir mapas e localização do dispositivo.
- **FusedLocationProviderClient**: Para obter atualizações de localização de forma eficiente em termos de bateria e precisão.
- **LocationRequest**: Para configurar a frequência e precisão das atualizações de localização.

Recursos úteis:
- [Solicitar atualizações de localização](https://developer.android.com/develop/sensors-and-location/location/request-updates?hl=pt-br)
- [Adicionar um mapa ao seu app Android](https://developers.google.com/codelabs/maps-platform/maps-platform-101-android?hl=pt-br)

### 2. Captura de Foto em Tentativas de Senha Incorreta

Para implementar a funcionalidade de captura de foto quando a senha for digitada incorretamente:

- **DevicePolicyManager**: Para monitorar tentativas de desbloqueio.
- **CameraX API**: Para acessar a câmera frontal e capturar fotos.
- **BroadcastReceiver**: Para detectar eventos de tentativa de desbloqueio.

Será necessário:
1. Criar um serviço em segundo plano para monitorar tentativas de desbloqueio.
2. Implementar um receptor para o evento de senha incorreta.
3. Acessar a câmera frontal e capturar uma foto.
4. Enviar a foto por e-mail.

### 3. Detecção de Troca de Chip SIM

Para detectar quando um novo chip SIM é inserido no dispositivo:

- **TelephonyManager**: Para acessar informações do SIM card.
- **BroadcastReceiver**: Para detectar eventos de mudança de estado do SIM.
- **SubscriptionManager**: Para gerenciar múltiplos SIMs em dispositivos dual-SIM.

Recursos úteis:
- [Sim-Change-Detection-POC](https://github.com/alihaider78222/Sim-Change-Detection-POC): Um exemplo de aplicativo Android em Kotlin para detecção de troca de SIM.
- [Implementing SIM Change Detection in Your Android App](https://medium.com/@ssvaghasiya61/implementing-sim-change-detection-in-your-android-app-a-comprehensive-guide-8db84197241c)

### 4. Bloqueio Remoto

Para implementar a funcionalidade de bloqueio remoto:

- **DevicePolicyManager**: Para controlar políticas de segurança do dispositivo.
- **Firebase Cloud Messaging (FCM)**: Para enviar comandos remotos ao dispositivo.
- **AdminReceiver**: Para receber e processar comandos de administração do dispositivo.

Recursos úteis:
- [Controle do dispositivo](https://developer.android.com/work/dpc/device-management?hl=pt-br)

### 5. Alerta Sonoro Remoto

Para implementar a funcionalidade de emitir um alerta sonoro remotamente:

- **MediaPlayer**: Para reproduzir sons de alerta.
- **Firebase Cloud Messaging (FCM)**: Para receber comandos remotos.
- **AudioManager**: Para controlar o volume do dispositivo.

### 6. Controle de Ligações Remotas

Para implementar a funcionalidade de controle de ligações remotas:

- **TelecomManager**: Para iniciar chamadas telefônicas.
- **CallLog.Calls**: Para acessar o registro de chamadas.
- **Firebase Cloud Messaging (FCM)**: Para receber comandos remotos.

## Servidor Web para Gerenciamento

Para o servidor web que gerenciará o acesso remoto ao dispositivo:

- **Kotlin Multiplatform**: Para compartilhar código entre o aplicativo Android e o servidor web.
- **Ktor**: Framework web em Kotlin para criar o servidor.
- **Firebase Authentication**: Para autenticação segura de usuários.
- **Firebase Realtime Database ou Firestore**: Para armazenar dados e comandos.
- **Firebase Cloud Messaging**: Para enviar comandos remotos aos dispositivos.

Recursos úteis:
- [Kotlin for server side](https://kotlinlang.org/docs/server-overview.html)
- [Firebase](https://firebase.google.com/)

## Próximos Passos

1. Aprofundar a pesquisa sobre cada uma das funcionalidades de segurança.
2. Configurar o ambiente de desenvolvimento Android.
3. Criar a estrutura básica do aplicativo.
4. Implementar cada funcionalidade de segurança.
5. Desenvolver o servidor web para gerenciamento.
6. Criar a interface web para controle remoto.
7. Testar e validar a solução completa.
