import React, { useState, useEffect } from 'react';
import { 
  Container, 
  Box, 
  Typography, 
  Paper, 
  Grid,
  Button,
  CircularProgress,
  Divider,
  Card,
  CardContent,
  CardActions,
  IconButton,
  Tooltip,
  Alert,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
  TextField
} from '@mui/material';
import { 
  Refresh as RefreshIcon,
  LocationOn as LocationIcon,
  Lock as LockIcon,
  VolumeUp as VolumeUpIcon,
  VolumeOff as VolumeOffIcon,
  Phone as PhoneIcon,
  Camera as CameraIcon,
  Settings as SettingsIcon,
  Delete as DeleteIcon
} from '@mui/icons-material';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { 
  getDevice, 
  getDeviceLocations, 
  lockDevice, 
  resetPassword,
  playSoundAlert,
  stopSoundAlert,
  makeCall,
  takePhoto,
  LocationData
} from '../services/deviceService';
import Header from '../components/Header';
import GoogleMapReact from 'google-map-react';

// Componente de marcador para o mapa
const Marker = ({ text }: { text: string }) => (
  <div style={{
    position: 'absolute',
    transform: 'translate(-50%, -50%)',
    color: '#1976D2',
    fontWeight: 'bold',
  }}>
    <LocationIcon style={{ fontSize: 36 }} />
    <div style={{ fontSize: 12, textAlign: 'center' }}>{text}</div>
  </div>
);

const DeviceDetailsPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [device, setDevice] = useState<any>(null);
  const [locations, setLocations] = useState<LocationData[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [actionLoading, setActionLoading] = useState(false);
  const [mapCenter, setMapCenter] = useState({ lat: -23.550520, lng: -46.633308 }); // São Paulo como padrão
  const [mapZoom, setMapZoom] = useState(15);
  
  // Estados para diálogos
  const [lockDialogOpen, setLockDialogOpen] = useState(false);
  const [passwordDialogOpen, setPasswordDialogOpen] = useState(false);
  const [callDialogOpen, setCallDialogOpen] = useState(false);
  const [newPassword, setNewPassword] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');
  
  const { user } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (id) {
      loadDeviceData(id);
    }
  }, [id]);

  const loadDeviceData = async (deviceId: string) => {
    setLoading(true);
    setError('');
    
    try {
      const deviceData = await getDevice(deviceId);
      setDevice(deviceData);
      
      const locationData = await getDeviceLocations(deviceId);
      setLocations(locationData);
      
      // Definir centro do mapa com a localização mais recente
      if (locationData.length > 0) {
        const lastLocation = locationData[0]; // Assumindo que está ordenado por timestamp
        setMapCenter({
          lat: lastLocation.latitude,
          lng: lastLocation.longitude
        });
      }
    } catch (err) {
      console.error('Erro ao carregar dados do dispositivo:', err);
      setError('Não foi possível carregar os dados do dispositivo. Tente novamente.');
    } finally {
      setLoading(false);
    }
  };

  const handleRefresh = () => {
    if (id) {
      loadDeviceData(id);
    }
  };

  const handleLockDevice = async () => {
    if (!id) return;
    
    setActionLoading(true);
    try {
      const success = await lockDevice(id);
      if (success) {
        alert('Dispositivo bloqueado com sucesso!');
      } else {
        alert('Não foi possível bloquear o dispositivo. Tente novamente.');
      }
    } catch (err) {
      console.error('Erro ao bloquear dispositivo:', err);
      alert('Erro ao bloquear dispositivo. Tente novamente.');
    } finally {
      setActionLoading(false);
      setLockDialogOpen(false);
    }
  };

  const handleResetPassword = async () => {
    if (!id || !newPassword) return;
    
    setActionLoading(true);
    try {
      const success = await resetPassword(id, newPassword);
      if (success) {
        alert('Senha alterada com sucesso!');
      } else {
        alert('Não foi possível alterar a senha. Tente novamente.');
      }
    } catch (err) {
      console.error('Erro ao alterar senha:', err);
      alert('Erro ao alterar senha. Tente novamente.');
    } finally {
      setActionLoading(false);
      setPasswordDialogOpen(false);
      setNewPassword('');
    }
  };

  const handlePlaySound = async () => {
    if (!id) return;
    
    setActionLoading(true);
    try {
      const success = await playSoundAlert(id);
      if (success) {
        alert('Alerta sonoro ativado com sucesso!');
      } else {
        alert('Não foi possível ativar o alerta sonoro. Tente novamente.');
      }
    } catch (err) {
      console.error('Erro ao ativar alerta sonoro:', err);
      alert('Erro ao ativar alerta sonoro. Tente novamente.');
    } finally {
      setActionLoading(false);
    }
  };

  const handleStopSound = async () => {
    if (!id) return;
    
    setActionLoading(true);
    try {
      const success = await stopSoundAlert(id);
      if (success) {
        alert('Alerta sonoro desativado com sucesso!');
      } else {
        alert('Não foi possível desativar o alerta sonoro. Tente novamente.');
      }
    } catch (err) {
      console.error('Erro ao desativar alerta sonoro:', err);
      alert('Erro ao desativar alerta sonoro. Tente novamente.');
    } finally {
      setActionLoading(false);
    }
  };

  const handleMakeCall = async () => {
    if (!id || !phoneNumber) return;
    
    setActionLoading(true);
    try {
      const success = await makeCall(id, phoneNumber);
      if (success) {
        alert('Chamada iniciada com sucesso!');
      } else {
        alert('Não foi possível iniciar a chamada. Tente novamente.');
      }
    } catch (err) {
      console.error('Erro ao iniciar chamada:', err);
      alert('Erro ao iniciar chamada. Tente novamente.');
    } finally {
      setActionLoading(false);
      setCallDialogOpen(false);
      setPhoneNumber('');
    }
  };

  const handleTakePhoto = async () => {
    if (!id) return;
    
    setActionLoading(true);
    try {
      const success = await takePhoto(id);
      if (success) {
        alert('Comando para capturar foto enviado com sucesso!');
      } else {
        alert('Não foi possível enviar o comando para capturar foto. Tente novamente.');
      }
    } catch (err) {
      console.error('Erro ao capturar foto:', err);
      alert('Erro ao capturar foto. Tente novamente.');
    } finally {
      setActionLoading(false);
    }
  };

  const formatDate = (timestamp: number) => {
    const date = new Date(timestamp);
    return date.toLocaleString('pt-BR');
  };

  if (loading) {
    return (
      <>
        <Header title="Detalhes do Dispositivo" />
        <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
          <Box sx={{ display: 'flex', justifyContent: 'center', my: 5 }}>
            <CircularProgress />
          </Box>
        </Container>
      </>
    );
  }

  if (error || !device) {
    return (
      <>
        <Header title="Detalhes do Dispositivo" />
        <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
          <Alert severity="error" sx={{ mb: 3 }}>
            {error || 'Dispositivo não encontrado'}
          </Alert>
          <Button variant="contained" onClick={() => navigate('/dashboard')}>
            Voltar para Dashboard
          </Button>
        </Container>
      </>
    );
  }

  return (
    <>
      <Header title="Detalhes do Dispositivo" />
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
          <Typography variant="h4" component="h1">
            {device.name}
          </Typography>
          <Button 
            variant="contained" 
            startIcon={<RefreshIcon />}
            onClick={handleRefresh}
            disabled={actionLoading}
          >
            Atualizar
          </Button>
        </Box>

        <Grid container spacing={3}>
          {/* Informações do dispositivo */}
          <Grid item xs={12} md={4}>
            <Paper sx={{ p: 3, height: '100%' }}>
              <Typography variant="h6" gutterBottom>
                Informações do Dispositivo
              </Typography>
              <Divider sx={{ mb: 2 }} />
              
              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" color="text.secondary">
                  ID do Dispositivo
                </Typography>
                <Typography variant="body1">
                  {device.deviceId}
                </Typography>
              </Box>
              
              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" color="text.secondary">
                  Status
                </Typography>
                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                  <Box 
                    sx={{ 
                      width: 12, 
                      height: 12, 
                      borderRadius: '50%', 
                      bgcolor: device.isActive ? 'success.main' : 'error.main',
                      mr: 1
                    }} 
                  />
                  <Typography variant="body1">
                    {device.isActive ? 'Ativo' : 'Inativo'}
                  </Typography>
                </Box>
              </Box>
              
              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" color="text.secondary">
                  Última atividade
                </Typography>
                <Typography variant="body1">
                  {formatDate(device.lastSeen)}
                </Typography>
              </Box>
              
              <Typography variant="h6" gutterBottom sx={{ mt: 4 }}>
                Funcionalidades Ativas
              </Typography>
              <Divider sx={{ mb: 2 }} />
              
              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                  <Box 
                    sx={{ 
                      width: 12, 
                      height: 12, 
                      borderRadius: '50%', 
                      bgcolor: device.settings.locationTrackingEnabled ? 'success.main' : 'error.main',
                      mr: 1
                    }} 
                  />
                  <Typography variant="body1">
                    Rastreamento de localização
                  </Typography>
                </Box>
                
                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                  <Box 
                    sx={{ 
                      width: 12, 
                      height: 12, 
                      borderRadius: '50%', 
                      bgcolor: device.settings.wrongPasswordPhotoEnabled ? 'success.main' : 'error.main',
                      mr: 1
                    }} 
                  />
                  <Typography variant="body1">
                    Foto em senha incorreta
                  </Typography>
                </Box>
                
                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                  <Box 
                    sx={{ 
                      width: 12, 
                      height: 12, 
                      borderRadius: '50%', 
                      bgcolor: device.settings.simChangeDetectionEnabled ? 'success.main' : 'error.main',
                      mr: 1
                    }} 
                  />
                  <Typography variant="body1">
                    Detecção de troca de SIM
                  </Typography>
                </Box>
                
                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                  <Box 
                    sx={{ 
                      width: 12, 
                      height: 12, 
                      borderRadius: '50%', 
                      bgcolor: device.settings.remoteLockEnabled ? 'success.main' : 'error.main',
                      mr: 1
                    }} 
                  />
                  <Typography variant="body1">
                    Bloqueio remoto
                  </Typography>
                </Box>
                
                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                  <Box 
                    sx={{ 
                      width: 12, 
                      height: 12, 
                      borderRadius: '50%', 
                      bgcolor: device.settings.soundAlertEnabled ? 'success.main' : 'error.main',
                      mr: 1
                    }} 
                  />
                  <Typography variant="body1">
                    Alerta sonoro
                  </Typography>
                </Box>
                
                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                  <Box 
                    sx={{ 
                      width: 12, 
                      height: 12, 
                      borderRadius: '50%', 
                      bgcolor: device.settings.callControlEnabled ? 'success.main' : 'error.main',
                      mr: 1
                    }} 
                  />
                  <Typography variant="body1">
                    Controle de ligações
                  </Typography>
                </Box>
              </Box>
            </Paper>
          </Grid>
          
          {/* Mapa de localização */}
          <Grid item xs={12} md={8}>
            <Paper sx={{ p: 3, height: '400px' }}>
              <Typography variant="h6" gutterBottom>
                Localização
              </Typography>
              <Divider sx={{ mb: 2 }} />
              
              {locations.length === 0 ? (
                <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '300px' }}>
                  <Typography variant="body1" color="text.secondary">
                    Nenhum histórico de localização disponível
                  </Typography>
                </Box>
              ) : (
                <Box sx={{ height: '300px', width: '100%' }}>
                  <GoogleMapReact
                    bootstrapURLKeys={{ key: 'YOUR_GOOGLE_MAPS_API_KEY' }}
                    defaultCenter={mapCenter}
                    defaultZoom={mapZoom}
                    center={mapCenter}
                    zoom={mapZoom}
                  >
                    {locations.map((location, index) => (
                      <Marker
                        key={location.id}
                        lat={location.latitude}
                        lng={location.longitude}
                        text={index === 0 ? 'Atual' : ''}
                      />
                    ))}
                  </GoogleMapReact>
                </Box>
              )}
            </Paper>
          </Grid>
          
          {/* Ações remotas */}
          <Grid item xs={12}>
            <Paper sx={{ p: 3 }}>
              <Typography variant="h6" gutterBottom>
                Ações Remotas
              </Typography>
              <Divider sx={{ mb: 3 }} />
              
              <Grid container spacing={2}>
                <Grid item xs={6} sm={4} md={2}>
                  <Button
                    variant="contained"
                    fullWidth
                    startIcon={<LockIcon />}
                    onClick={() => setLockDialogOpen(true)}
                    disabled={!device.settings.remoteLockEnabled || actionLoading}
                    sx={{ py: 1.5 }}
                  >
                    Bloquear
                  </Button>
                </Grid>
                
                <Grid item xs={6} sm={4} md={2}>
                  <Button
                    variant="contained"
                    fullWidth
                    startIcon={<LockIcon />}
                    onClick={() => setPasswordDialogOpen(true)}
                    disabled={!device.settings.remoteLockEnabled || actionLoading}
                    sx={{ py: 1.5 }}
                  >
                    Alterar Senha
                  </Button>
                </Grid>
                
                <Grid item xs={6} sm={4} md={2}>
                  <Button
                    variant="contained"
                    fullWidth
                    startIcon={<VolumeUpIcon />}
                    onClick={handlePlaySound}
                    disabled={!device.settings.soundAlertEnabled || actionLoading}
                    sx={{ py: 1.5 }}
                  >
                    Tocar Alerta
                  </Button>
                </Grid>
                
                <Grid item xs={6} sm={4} md={2}>
                  <Button
                    variant="contained"
                    fullWidth
                    startIcon={<VolumeOffIcon />}
                    onClick={handleStopSound}
                    disabled={!device.settings.soundAlertEnabled || actionLoading}
                    sx={{ py: 1.5 }}
                  >
                    Parar Alerta
                  </Button>
                </Grid>
                
                <Grid item xs={6} sm={4} md={2}>
                  <Button
                    variant="contained"
                    fullWidth
                    startIcon={<PhoneIcon />}
                    onClick={() => setCallDialogOpen(true)}
                    disabled={!device.settings.callControlEnabled || actionLoading}
                    sx={{ py: 1.5 }}
                  >
                    Fazer Ligação
                  </Button>
                </Grid>
                
                <Grid item xs={6} sm={4} md={2}>
                  <Button
                    variant="contained"
                    fullWidth
                    startIcon={<CameraIcon />}
                    onClick={handleTakePhoto}
                    disabled={!device.settings.wrongPasswordPhotoEnabled || actionLoading}
                    sx={{ py: 1.5 }}
                  >
                    Tirar Foto
                  </Button>
                </Grid>
              </Grid>
            </Paper>
          </Grid>
        </Grid>
      </Container>
      
      {/* Diálogo de confirmação para bloqueio */}
      <Dialog
        open={lockDialogOpen}
        onClose={() => setLockDialogOpen(false)}
        aria-labelledby="lock-dialog-title"
      >
        <DialogTitle id="lock-dialog-title">Bloquear Dispositivo</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Tem certeza que deseja bloquear o dispositivo remotamente? O dispositivo será bloqueado imediatamente.
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setLockDialogOpen(false)} disabled={actionLoading}>
            Cancelar
          </Button>
          <Button onClick={handleLockDevice} color="primary" disabled={actionLoading}>
            {actionLoading ? <CircularProgress size={24} /> : 'Bloquear'}
          </Button>
        </DialogActions>
      </Dialog>
      
      {/* Diálogo para alterar senha */}
      <Dialog
        open={passwordDialogOpen}
        onClose={() => setPasswordDialogOpen(false)}
        aria-labelledby="password-dialog-title"
      >
        <DialogTitle id="password-dialog-title">Alterar Senha do Dispositivo</DialogTitle>
        <DialogContent>
          <DialogContentText sx={{ mb: 2 }}>
            Digite a nova senha para o dispositivo. A senha atual será substituída imediatamente.
          </DialogContentText>
          <TextField
            autoFocus
            margin="dense"
            id="password"
            label="Nova Senha"
            type="password"
            fullWidth
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
            disabled={actionLoading}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setPasswordDialogOpen(false)} disabled={actionLoading}>
            Cancelar
          </Button>
          <Button onClick={handleResetPassword} color="primary" disabled={actionLoading || !newPassword}>
            {actionLoading ? <CircularProgress size={24} /> : 'Alterar Senha'}
          </Button>
        </DialogActions>
      </Dialog>
      
      {/* Diálogo para fazer ligação */}
      <Dialog
        open={callDialogOpen}
        onClose={() => setCallDialogOpen(false)}
        aria-labelledby="call-dialog-title"
      >
        <DialogTitle id="call-dialog-title">Fazer Ligação Remota</DialogTitle>
        <DialogContent>
          <DialogContentText sx={{ mb: 2 }}>
            Digite o número de telefone para o qual deseja ligar. O dispositivo iniciará a chamada automaticamente.
          </DialogContentText>
          <TextField
            autoFocus
            margin="dense"
            id="phoneNumber"
            label="Número de Telefone"
            type="tel"
            fullWidth
            value={phoneNumber}
            onChange={(e) => setPhoneNumber(e.target.value)}
            disabled={actionLoading}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setCallDialogOpen(false)} disabled={actionLoading}>
            Cancelar
          </Button>
          <Button onClick={handleMakeCall} color="primary" disabled={actionLoading || !phoneNumber}>
            {actionLoading ? <CircularProgress size={24} /> : 'Fazer Ligação'}
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
};

export default DeviceDetailsPage;
