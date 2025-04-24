# Guia de Implementação do Aplicativo de Segurança Android

Este documento fornece instruções detalhadas para implementar o aplicativo de segurança Android "SecureGuard", um sistema completo de proteção contra roubo e perda de dispositivos, inspirado no Cerberus.

## Visão Geral do Sistema

O sistema SecureGuard é composto por três componentes principais:

1. **Aplicativo Android em Kotlin**: Instalado no dispositivo móvel, responsável por monitorar e executar comandos de segurança.
2. **Servidor Web em Kotlin/Ktor**: Gerencia a comunicação entre o aplicativo e a interface web, armazena dados e processa comandos.
3. **Interface Web em React/TypeScript**: Permite ao usuário controlar remotamente o dispositivo através de um navegador.

## Requisitos do Sistema

### Aplicativo Android
- Android 8.0 (API 26) ou superior
- Permissões: Localização, Câmera, Telefonia, Administrador de Dispositivo, Internet
- Google Play Services (para Firebase e Geolocalização)

### Servidor Web
- JDK 11 ou superior
- Banco de dados SQL (H2, PostgreSQL ou MySQL)
- Firebase Admin SDK
- Servidor com suporte a Kotlin/JVM

### Interface Web
- Navegador moderno com suporte a JavaScript ES6+
- Conexão com internet

## Configuração do Ambiente de Desenvolvimento

### Para o Aplicativo Android
1. Instale o Android Studio (versão mais recente)
2. Configure o SDK do Android (API 26 ou superior)
3. Configure o emulador ou dispositivo físico para testes
4. Instale os plugins Kotlin para Android Studio

### Para o Servidor Web
1. Instale o IntelliJ IDEA ou outro IDE com suporte a Kotlin
2. Configure o JDK 11 ou superior
3. Configure o Gradle para gerenciamento de dependências
4. Configure o banco de dados (H2 para desenvolvimento, PostgreSQL para produção)

### Para a Interface Web
1. Instale o Node.js (versão LTS)
2. Configure o npm ou yarn para gerenciamento de pacotes
3. Configure um editor de código (VS Code recomendado)

## Implementação do Aplicativo Android

### Estrutura do Projeto
O aplicativo Android segue a arquitetura MVVM (Model-View-ViewModel) e está organizado nos seguintes pacotes:

- `activities`: Atividades principais da aplicação
- `fragments`: Fragmentos para diferentes telas
- `services`: Serviços em segundo plano
- `receivers`: Receptores de broadcast
- `utils`: Classes utilitárias
- `models`: Classes de modelo de dados

### Funcionalidades Principais

#### 1. Geolocalização
A funcionalidade de geolocalização utiliza o FusedLocationProviderClient do Google Play Services para rastrear a localização do dispositivo com precisão e eficiência energética.

```kotlin
// Exemplo de implementação em LocationUtils.kt
class LocationUtils(private val context: Context) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    
    fun startLocationTracking() {
        // Verificar permissões
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) 
            != PackageManager.PERMISSION_GRANTED) {
            return
        }
        
        // Configurar solicitação de localização
        val locationRequest = LocationRequest.create().apply {
            interval = 10000 // 10 segundos
            fastestInterval = 5000 // 5 segundos
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        
        // Iniciar atualizações de localização
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }
    
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                // Enviar localização para o servidor
                sendLocationToServer(location.latitude, location.longitude, location.accuracy)
            }
        }
    }
    
    private fun sendLocationToServer(latitude: Double, longitude: Double, accuracy: Float) {
        // Implementação da comunicação com o servidor
    }
}
```

#### 2. Captura de Foto em Tentativas de Senha Incorreta
Esta funcionalidade utiliza o DevicePolicyManager para detectar tentativas de desbloqueio e a CameraX API para capturar fotos da câmera frontal.

```kotlin
// Exemplo de implementação em CameraUtils.kt
class CameraUtils(private val context: Context) {
    fun capturePhoto() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            
            // Selecionar câmera frontal
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build()
            
            // Configurar captura de imagem
            val imageCapture = ImageCapture.Builder()
                .setTargetRotation(Surface.ROTATION_0)
                .build()
            
            try {
                // Vincular câmera ao ciclo de vida
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    context as LifecycleOwner,
                    cameraSelector,
                    imageCapture
                )
                
                // Capturar foto
                val photoFile = File(context.getExternalFilesDir(null), "security_photo.jpg")
                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                
                imageCapture.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            // Enviar foto por e-mail
                            EmailUtils.sendEmailWithAttachment(
                                context,
                                "Tentativa de desbloqueio detectada",
                                "Uma tentativa de desbloqueio foi detectada no seu dispositivo.",
                                photoFile
                            )
                        }
                        
                        override fun onError(exception: ImageCaptureException) {
                            Log.e("CameraUtils", "Erro ao capturar foto: ${exception.message}")
                        }
                    }
                )
            } catch (e: Exception) {
                Log.e("CameraUtils", "Erro ao configurar câmera: ${e.message}")
            }
        }, ContextCompat.getMainExecutor(context))
    }
}
```

#### 3. Detecção de Troca de SIM
Esta funcionalidade utiliza o TelephonyManager e BroadcastReceiver para monitorar mudanças no estado do SIM.

```kotlin
// Exemplo de implementação em SimChangeReceiver.kt
class SimChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.SIM_STATE_CHANGED") {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            
            // Verificar se o SIM foi trocado
            val prefs = PreferencesManager(context)
            val savedSimSerialNumber = prefs.getSimSerialNumber()
            val currentSimSerialNumber = telephonyManager.simSerialNumber
            
            if (savedSimSerialNumber != null && savedSimSerialNumber != currentSimSerialNumber) {
                // SIM foi trocado, enviar alerta
                val simInfo = "Número: ${telephonyManager.line1Number}, " +
                              "Operadora: ${telephonyManager.networkOperatorName}, " +
                              "Serial: $currentSimSerialNumber"
                
                EmailUtils.sendEmail(
                    context,
                    "Troca de SIM detectada",
                    "Uma troca de SIM foi detectada no seu dispositivo. Informações do novo SIM:\n$simInfo"
                )
                
                // Atualizar o número do SIM salvo
                prefs.saveSimSerialNumber(currentSimSerialNumber)
            }
        }
    }
}
```

#### 4. Bloqueio Remoto
Esta funcionalidade utiliza o DevicePolicyManager para controlar o bloqueio do dispositivo.

```kotlin
// Exemplo de implementação em DeviceAdminUtils.kt
class DeviceAdminUtils(private val context: Context) {
    private val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    private val componentName = ComponentName(context, DeviceAdminReceiver::class.java)
    
    fun isAdminActive(): Boolean {
        return devicePolicyManager.isAdminActive(componentName)
    }
    
    fun lockDevice() {
        if (isAdminActive()) {
            devicePolicyManager.lockNow()
        }
    }
    
    fun resetPassword(newPassword: String) {
        if (isAdminActive()) {
            devicePolicyManager.resetPassword(newPassword, DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY)
        }
    }
}
```

#### 5. Alerta Sonoro Remoto
Esta funcionalidade utiliza o MediaPlayer e AudioManager para reproduzir sons em volume máximo.

```kotlin
// Exemplo de implementação em SoundUtils.kt
class SoundUtils(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    
    fun playAlertSound() {
        // Configurar volume máximo
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
            0
        )
        
        // Iniciar reprodução do som de alerta
        mediaPlayer = MediaPlayer.create(context, R.raw.alert_sound)
        mediaPlayer?.apply {
            isLooping = true
            start()
        }
    }
    
    fun stopAlertSound() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
    }
}
```

#### 6. Controle de Ligações
Esta funcionalidade utiliza o TelecomManager para iniciar chamadas telefônicas.

```kotlin
// Exemplo de implementação em CallUtils.kt
class CallUtils(private val context: Context) {
    fun makeCall(phoneNumber: String) {
        // Verificar permissões
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) 
            != PackageManager.PERMISSION_GRANTED) {
            return
        }
        
        // Iniciar chamada
        val uri = Uri.parse("tel:$phoneNumber")
        val intent = Intent(Intent.ACTION_CALL, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
```

## Implementação do Servidor Web

O servidor web é implementado usando Kotlin com o framework Ktor, seguindo uma arquitetura de serviços e rotas.

### Estrutura do Projeto
- `models`: Classes de modelo de dados
- `routes`: Definições de rotas da API
- `services`: Serviços de negócio
- `utils`: Classes utilitárias

### Componentes Principais

#### 1. Configuração da Aplicação
```kotlin
// Exemplo de implementação em Application.kt
fun main() {
    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            json()
        }
        
        install(Authentication) {
            jwt {
                // Configuração de autenticação JWT
            }
        }
        
        install(CORS) {
            // Configuração de CORS
        }
        
        routing {
            // Rotas da API
            userRoutes()
            deviceRoutes()
        }
    }.start(wait = true)
}
```

#### 2. Rotas de Dispositivos
```kotlin
// Exemplo de implementação em DeviceRoutes.kt
fun Route.deviceRoutes() {
    authenticate {
        route("/api/devices") {
            get {
                // Listar dispositivos do usuário
            }
            
            post {
                // Registrar novo dispositivo
            }
            
            route("/{id}") {
                get {
                    // Obter detalhes do dispositivo
                }
                
                put {
                    // Atualizar dispositivo
                }
                
                delete {
                    // Excluir dispositivo
                }
                
                post("/commands") {
                    // Enviar comando para o dispositivo
                }
                
                get("/locations") {
                    // Obter histórico de localização
                }
            }
        }
    }
}
```

#### 3. Serviço de Dispositivos
```kotlin
// Exemplo de implementação em DeviceService.kt
class DeviceService(private val database: Database) {
    // Tabela de dispositivos
    object Devices : Table() {
        val id = varchar("id", 36).primaryKey()
        val userId = varchar("user_id", 36).references(Users.id)
        val name = varchar("name", 255)
        val deviceId = varchar("device_id", 255)
        val fcmToken = varchar("fcm_token", 255).nullable()
        val lastSeen = long("last_seen")
        val isActive = bool("is_active")
    }
    
    init {
        transaction(database) {
            SchemaUtils.create(Devices)
        }
    }
    
    // Métodos para gerenciar dispositivos
    fun getDevicesByUserId(userId: String): List<Device> {
        // Implementação
    }
    
    fun getDeviceById(id: String): Device? {
        // Implementação
    }
    
    fun createDevice(device: Device): Device {
        // Implementação
    }
    
    fun updateDevice(id: String, device: Device): Device? {
        // Implementação
    }
    
    fun deleteDevice(id: String): Boolean {
        // Implementação
    }
    
    fun sendCommand(deviceId: String, command: Command): Boolean {
        // Implementação usando Firebase Cloud Messaging
    }
}
```

## Implementação da Interface Web

A interface web é implementada usando React com TypeScript e Material UI para uma experiência de usuário moderna e responsiva.

### Estrutura do Projeto
- `src/components`: Componentes reutilizáveis
- `src/pages`: Páginas da aplicação
- `src/services`: Serviços de API
- `src/contexts`: Contextos React (autenticação, etc.)

### Componentes Principais

#### 1. Contexto de Autenticação
```typescript
// Exemplo de implementação em AuthContext.tsx
export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
    const [user, setUser] = useState<User | null>(null);
    const [loading, setLoading] = useState(true);
    
    useEffect(() => {
        // Carregar usuário do token salvo
    }, []);
    
    const login = async (email: string, password: string): Promise<boolean> => {
        // Implementação
    };
    
    const register = async (name: string, email: string, password: string): Promise<boolean> => {
        // Implementação
    };
    
    const logout = () => {
        // Implementação
    };
    
    return (
        <AuthContext.Provider
            value={{
                user,
                isAuthenticated: !!user,
                loading,
                login,
                register,
                logout,
            }}
        >
            {children}
        </AuthContext.Provider>
    );
};
```

#### 2. Serviço de Dispositivos
```typescript
// Exemplo de implementação em deviceService.ts
export const getDevices = async (): Promise<Device[]> => {
    const response = await api.get('/api/devices');
    return response.data;
};

export const getDevice = async (deviceId: string): Promise<Device> => {
    const response = await api.get(`/api/devices/${deviceId}`);
    return response.data;
};

export const lockDevice = async (deviceId: string): Promise<boolean> => {
    return sendCommand(deviceId, CommandType.LOCK_DEVICE);
};

export const playSoundAlert = async (deviceId: string): Promise<boolean> => {
    return sendCommand(deviceId, CommandType.SOUND_ALERT);
};

export const makeCall = async (deviceId: string, phoneNumber: string): Promise<boolean> => {
    return sendCommand(deviceId, CommandType.MAKE_CALL, { phoneNumber });
};
```

#### 3. Página de Detalhes do Dispositivo
```typescript
// Exemplo de implementação em DeviceDetailsPage.tsx
const DeviceDetailsPage: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const [device, setDevice] = useState<Device | null>(null);
    const [locations, setLocations] = useState<LocationData[]>([]);
    
    useEffect(() => {
        // Carregar dados do dispositivo
    }, [id]);
    
    const handleLockDevice = async () => {
        // Implementação
    };
    
    const handlePlaySound = async () => {
        // Implementação
    };
    
    return (
        <>
            <Header title="Detalhes do Dispositivo" />
            <Container>
                {/* Interface de detalhes do dispositivo */}
                <Grid container spacing={3}>
                    {/* Informações do dispositivo */}
                    <Grid item xs={12} md={4}>
                        {/* ... */}
                    </Grid>
                    
                    {/* Mapa de localização */}
                    <Grid item xs={12} md={8}>
                        {/* ... */}
                    </Grid>
                    
                    {/* Ações remotas */}
                    <Grid item xs={12}>
                        {/* ... */}
                    </Grid>
                </Grid>
            </Container>
        </>
    );
};
```

## Comunicação entre Componentes

### Aplicativo Android para Servidor
- O aplicativo Android se comunica com o servidor através de requisições HTTP REST
- Firebase Cloud Messaging (FCM) é usado para enviar comandos do servidor para o aplicativo
- As localizações são enviadas periodicamente para o servidor

### Interface Web para Servidor
- A interface web se comunica com o servidor através de requisições HTTP REST
- Autenticação é feita usando tokens JWT
- Os comandos são enviados através da API e encaminhados para o dispositivo via FCM

## Considerações de Segurança

1. **Autenticação e Autorização**
   - Uso de tokens JWT para autenticação
   - Senhas armazenadas com hash seguro (BCrypt)
   - Verificação de propriedade do dispositivo

2. **Proteção de Dados**
   - Comunicação HTTPS para todas as requisições
   - Dados sensíveis criptografados no banco de dados
   - Tokens FCM validados no servidor

3. **Permissões do Aplicativo**
   - Solicitação de permissões apenas quando necessário
   - Explicação clara do uso de cada permissão
   - Verificação de permissões antes de usar recursos protegidos

## Implantação

### Aplicativo Android
1. Gerar APK assinado ou bundle para Google Play
2. Distribuir através da Google Play Store ou diretamente para o usuário

### Servidor Web
1. Configurar servidor com JVM (AWS, Google Cloud, DigitalOcean, etc.)
2. Configurar banco de dados PostgreSQL
3. Configurar HTTPS com certificado SSL
4. Implantar aplicação Ktor como serviço

### Interface Web
1. Construir aplicação React para produção
2. Implantar em serviço de hospedagem estática (Netlify, Vercel, etc.)
3. Configurar domínio personalizado e HTTPS

## Conclusão

O sistema SecureGuard oferece uma solução completa de segurança para dispositivos Android, permitindo rastreamento de localização, captura de fotos em tentativas de senha incorreta, detecção de troca de SIM, bloqueio remoto, alerta sonoro e controle de ligações. A arquitetura modular e a separação em três componentes (aplicativo, servidor e interface web) garantem flexibilidade, segurança e facilidade de manutenção.

Para implementar o sistema, siga as instruções detalhadas neste documento, adaptando conforme necessário para seus requisitos específicos. Lembre-se de testar todas as funcionalidades em diferentes cenários e dispositivos para garantir a robustez da solução.
